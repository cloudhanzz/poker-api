package jiayun.han.game.controller;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jiayun.han.game.enums.Suit;
import jiayun.han.game.exception.CreateSamePlayerMoreThanOnceException;
import jiayun.han.game.exception.GameNotFoundException;
import jiayun.han.game.exception.NoEnoughRemainingCardsException;
import jiayun.han.game.exception.PlayerNotFoundException;
import jiayun.han.game.exception.TooManyPlayersException;
import jiayun.han.game.exception.checked.InvalidRequestException;
import jiayun.han.game.model.Card;
import jiayun.han.game.model.Game;
import jiayun.han.game.model.Player;
import jiayun.han.game.model.UndeltCardsSummary;
import jiayun.han.game.request.GameRequest;
import jiayun.han.game.request.PlayerRequest;
import jiayun.han.game.util.LoggingUtil;


/**
 * 
 * @author Jiayun Han
 *
 */
@RestController
@RequestMapping(value = "/poker-game/v1.0/games", produces = "application/json")
public class GameController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);	
	private static final LoggingUtil CENTRAL_LOGGER = new LoggingUtil(LOGGER);
	
	private final Map<String, Game> games = new ConcurrentHashMap<>();
	

	@PostMapping("/{gameId}/players")
	@ApiOperation(value = "Create and add a player", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 406, message = "Not Acceptable request body: too many players"),
	@ApiResponse(code = 409, message = "Conflict: the specified player already exists."),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> addPlayer(@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId,
			@RequestBody(required = false) PlayerRequest request) {
		
		if(requestId == null) {
			requestId = UUID.randomUUID().toString();
		}
		
		CENTRAL_LOGGER.logStart(requestId, HttpMethod.POST, "Adding a player");
		
		Game game = findGame(gameId, requestId); 	
		Player player = Player.fromRequest(request);
		
		try {
			game.addPlayer(player);
		} catch (InvalidRequestException e) {
			switch(e.getCode()) {
			case PLAYER_NAME_TAKEN:
				throw new CreateSamePlayerMoreThanOnceException(e.getMessage(), requestId);
			case TOO_MANY_PLAYERS:
				throw new TooManyPlayersException(e.getMessage(), requestId);
			case INADIQUATE_REMAINING_CARDS:
				break;
			default:
				break;			
			}
		}
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{playerId}")
				.buildAndExpand(player.getId()).toUri();
		
		CENTRAL_LOGGER.logEnd(requestId, HttpMethod.POST, "Adding a player");
		
		return ResponseEntity.created(location).build();	
	}
	
	@PostMapping("/{gameId}/decks")
	@ApiOperation(value = "Create and add a deck", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> createDeck(
			@PathVariable String gameId, 
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {

		
		CENTRAL_LOGGER.logStart(requestId, HttpMethod.POST, "Create and add a deck");
		
		Game game = findGame(gameId, requestId); 		
		game.addDeck();
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{gameId}")
				.buildAndExpand(game.getId()).toUri();
		
		games.put(game.getId(), game);
		
		CENTRAL_LOGGER.logEnd(requestId, HttpMethod.POST, "Create and add a deck");
		
		return ResponseEntity.created(location).body(game);		
	}
	
	@PostMapping
	@ApiOperation(value = "Create a new game", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
	@ApiResponse(code = 406, message = "Not Acceptable request body, too many players"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> createGame(
			@RequestBody(required = false) GameRequest request,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId)
			throws JsonProcessingException, IOException {
		
		final String reqId = prepareRequestId(requestId);
		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.POST, "Creating a new game");
		
		Game game = null;
		try {
			game = Game.fromRequest(request);
		} catch (InvalidRequestException e) {
			throw new TooManyPlayersException(e.getMessage(), reqId);
		}
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{gameId}")
				.buildAndExpand(game.getId()).toUri();
		
		games.put(game.getId(), game);
		
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.POST, "Creating a new game");
		
		return ResponseEntity.created(location).body(game);		
	}
		
	@GetMapping
	@ApiOperation(value = "List all games", response = Game.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public List<Game> listAllGames(@RequestHeader(value = "X_Request_Id", required = false) String requestId){
		
		final String reqId = prepareRequestId(requestId);
		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, "List all games");
		
		List<Game> existingGames = games.values().stream().collect(toList());
		
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, "List all games");
		
		return existingGames;	
	}
	

	@GetMapping("/{gameId}")
	@ApiOperation(value = "Find a game by game id", response = Game.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Found"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public Game findGame(@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId){
		
		final String reqId = prepareRequestId(requestId);
		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, "Retrieving for a new game");
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));		
		
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, "Retrieving for a new game");
		
		return game;
		
	}
	

	@DeleteMapping("/{gameId}")
	@ApiOperation(value = "Delete a game by game id", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Success with no content"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> deleteGame(@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId){
		
		final String reqId = prepareRequestId(requestId);
		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.DELETE, "Deleting a game");
		
		Optional.ofNullable(games.remove(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));		
		
		CENTRAL_LOGGER.logEnd(requestId, HttpMethod.DELETE, "Deleting a game");
		
		return CENTRAL_LOGGER.logAndReturn204(reqId, HttpMethod.DELETE);
		
	}
	
	@DeleteMapping("/{gameId}/players/{playerId}")
	@ApiOperation(value = "Remove a player", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Success with no content"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 404, message = "Specified player not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> deletePlayer(@PathVariable String gameId, 
			@PathVariable String playerId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		CENTRAL_LOGGER.logStart(requestId, HttpMethod.DELETE, "Removing a player");
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		Optional.ofNullable(game.getPlayers().remove(playerId)).orElseThrow(() -> new PlayerNotFoundException(reqId));
				
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.DELETE, "Removing a player");
		
		return CENTRAL_LOGGER.logAndReturn204(reqId, HttpMethod.DELETE);	
	}
	
	@PatchMapping("/{gameId}/players/{playerId}")
	@ApiOperation(value = "Deal one or more cards to a player", response = Player.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 404, message = "Specified player not exists"),
	@ApiResponse(code = 406, message = "Not enough cards to deal"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public Player dealCards(
			@PathVariable String gameId, 
			@PathVariable String playerId,
			@RequestParam(defaultValue = "1", required = false) Integer num,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Dealing " + num + " card(s)";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.PATCH, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		List<Card> cards = null;
		try {
			cards = game.getDealer().deal(game, num);
		} catch (InvalidRequestException e) {
			throw new NoEnoughRemainingCardsException(e.getMessage(), reqId);
		}
		
		Player player = Optional.ofNullable(game.getPlayers().get(playerId)).orElseThrow(() -> new PlayerNotFoundException(reqId));
		player.addCards(cards);
		
		
				
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.PATCH, msg);
		
		return player;
	}
	
	@GetMapping("/{gameId}/players/{playerId}")
	@ApiOperation(value = "Get cards of a player", response = Card.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 404, message = "Specified player not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public List<Card> getCards(
			@PathVariable String gameId, 
			@PathVariable String playerId,
			@RequestParam(defaultValue = "1", required = false) Integer num,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Retrieving cards of a player";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		Player player = Optional.ofNullable(game.getPlayers().get(playerId)).orElseThrow(() -> new PlayerNotFoundException(reqId));
		
		List<Card> cards = player.getCards();
		
				
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, msg);
		
		return cards;
	}
	
	
	@GetMapping("/{gameId}/players")
	@ApiOperation(value = "Get all players of a game", response = Player.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public List<Player> getAndSortPlayers(@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Retrieving and ordering players of a game";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		List<Player> players = game.getPlayers().values().stream().sorted().collect(toList());
						
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, msg);
		
		return players;
	}
	
	@GetMapping("/{gameId}/summarize_undelt_by_suits")
	@ApiOperation(value = "Summarize undelt cards of a game by suits", response = UndeltCardsSummary.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public UndeltCardsSummary getUndeltCards(
			@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Summarizing undelt cards of a game by suits";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		List<Card> remainingCards = game.getRemainingCards();
		
		int spades = 0;
		int hearts = 0;
		int clubs = 0;
		int diamonds = 0;
		
		for(Card card : remainingCards) {
			Suit suit = card.getSuit();
			switch(suit) {
			case CLUBS:
				clubs++;
				break;
			case DIAMONDS:
				diamonds++;
				break;
			case HEARTS:
				hearts++;
				break;
			case SPADES:
				spades++;
				break;			
			}
		}
		
		UndeltCardsSummary summary = new UndeltCardsSummary()
				.withClubs(clubs)
				.withDiamonds(diamonds)
				.withHearts(hearts)
				.withSpades(spades);
		
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, msg);
		
		return summary;
	}
	
	
	@GetMapping("/{gameId}/sort_and_list_undelt")
	@ApiOperation(value = "Display remaining cards sorted by suits and face value", response = Card.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public List<Card> sortRemainingCards(@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Displaying remaining cards sorted by suits and face value";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.GET, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		List<Card> remainingCards = game.getRemainingCards();
		
		Collections.sort(remainingCards);
		
		CENTRAL_LOGGER.logEnd(reqId, HttpMethod.GET, msg);
		
		return remainingCards;
	}
	
	
	@PutMapping("/{gameId}/shuffle")
	@ApiOperation(value = "Shuffle cards", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Success with no content"),
	@ApiResponse(code = 404, message = "Specified game not exists"),
	@ApiResponse(code = 500, message = "Server failed to perform the operation") })
	public ResponseEntity<?> shuffleCards(
			@PathVariable String gameId,
			@RequestHeader(value = "X_Request_Id", required = false) String requestId,
			@RequestParam(value = "on_entire_shoe", defaultValue = "true", required = false) boolean onEntireShoe) {
		
		final String reqId = prepareRequestId(requestId);
		
		String msg = "Shuffling cards";		
		CENTRAL_LOGGER.logStart(reqId, HttpMethod.PUT, msg);
		
		Game game = Optional.ofNullable(games.get(gameId)).orElseThrow(() -> new GameNotFoundException(reqId));
		game.shuffle(onEntireShoe);
						
		return CENTRAL_LOGGER.logAndReturn204(reqId, HttpMethod.PUT);
	}

	private String prepareRequestId(String requestId) {
		return Optional.ofNullable(requestId).orElseGet(() -> UUID.randomUUID().toString());
	}
}
