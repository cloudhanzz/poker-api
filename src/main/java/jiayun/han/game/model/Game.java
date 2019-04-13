package jiayun.han.game.model;

import static java.util.stream.Collectors.toMap;
import static jiayun.han.game.util.Constants.CARDS_OF_ONE_DECK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jiayun.han.game.enums.ExceptionCode;
import jiayun.han.game.exception.checked.InvalidRequestException;
import jiayun.han.game.request.GameRequest;

/**
 * A Game contains a game deck and 1 or more players
 * 
 * @author Jiayun Han
 *
 */
public class Game {
		
	private String id;
	private GameDeck gameDeck;
	private Map<String, Player> players;
	private Dealer dealer;
	
	
	public Game() throws InvalidRequestException {		
		this(1, new HashMap<String, Player>(), new Dealer());
	}
	
	public static Game fromRequest(GameRequest request) throws InvalidRequestException {

		if(request == null) {
			return new Game();
		}
		
		// ensure minimum of 1 deck
		int numOfDecks = Math.max(request.getNumOfDecks(), 1);
		
		Map<String, Player> players = Optional
				.ofNullable(request.getPlayerNames())
				.orElseGet(ArrayList::new)
				.stream()
				.map(Player::new)				
				.collect(toMap(Player::getId, player -> player));		
		
	    Dealer dealer = new Dealer(request.getDealerName());
	    
	    return new Game(numOfDecks, players, dealer);
	}
	
	public Game(int numOfDecks, Map<String, Player> players, Dealer dealer) throws InvalidRequestException {
		
		int cards = CARDS_OF_ONE_DECK * numOfDecks;
		
		if(players.size() > cards) {
			throw new InvalidRequestException(ExceptionCode.TOO_MANY_PLAYERS, "When requesting creating a game, the number of players should not be greater than that of cards");
		}
		
		this.id = UUID.randomUUID().toString();		
		this.gameDeck = new GameDeck(numOfDecks);
		this.players = players;
		this.dealer = dealer;
	}

	public String getId() {
		return id;
	}
	
	public int getNumOfDecks() {
		return gameDeck.getNumberOfDecks();
	}	
	
	public Dealer getDealer() {
		return dealer;
	}

	public Map<String, Player> getPlayers() {
		return players;
	}
	
	public int getNumOfRemainingCards() {
		return gameDeck.getNumOfRemainingCards(dealer.getCurrentIndex());
	}

	public Card getCard(int index) {
		return gameDeck.getCard(index);
	}
	
	public void addDeck() {
		addDeck(true);
	}
	
	public void addDeck(boolean shuffle) {
		if(shuffle) {
			gameDeck.addDeck(dealer.getCurrentIndex());
		}else {
			gameDeck.addDeck();
		}
	}

	public void addPlayer(Player player) throws InvalidRequestException {
		
		for(Player p : players.values()) {
			if(p.hasSameName(player)) {
				throw new InvalidRequestException(ExceptionCode.PLAYER_NAME_TAKEN, "Player with the same name existed in this game");
			}
		}
				
		if(players.size() + 1 > this.getNumOfRemainingCards()) {
			throw new InvalidRequestException(ExceptionCode.TOO_MANY_PLAYERS, "Not enough remaining cards to accept new player");
		}
		
		players.put(player.getId(), player);
		
	}
	
	public void deal(Game game, int num) {
		
	}

	@JsonIgnore
	public int getNumOfCards() {
		return gameDeck.getNumberOfDecks() * CARDS_OF_ONE_DECK;
	}

	public List<Card> getRemainingCards() {
		return gameDeck.getRemainingCards(dealer.getCurrentIndex());
	}

	public void shuffle(boolean reset) {
		dealer.shuffle(this, reset);
	}

	public GameDeck getGameDesk() {
		return gameDeck;
		
	}
}
