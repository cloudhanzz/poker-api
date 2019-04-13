package jiayun.han.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jiayun.han.game.request.PlayerRequest;
import jiayun.han.game.util.Tool;

/**
 * 
 * @author Jiayun Han
 *
 */
public class Player implements Comparable<Player>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private List<Card> cards;
	
	public Player() {
		this(null);
	}
	
	public Player(String name) {
		
		id = UUID.randomUUID().toString();		
		cards = new ArrayList<>();
		
		if(Tool.isEmptyOrNullString(name)) {
			this.name = "player-" + id;
		}else {
			this.name = name.trim();
		}
	}
	
	public static Player fromRequest(PlayerRequest request) {
		return Optional
				.ofNullable(request)
				.map(PlayerRequest::getName)
				.map(Player::new)
				.orElseGet(() -> new Player());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public void addCards(List<Card> cards) {
		this.cards.addAll(cards);
	}
		
	public int getScore() {
		return score();
	}

	public boolean hasSameName(Player other) {
		return name.equalsIgnoreCase(other.name);
	}

	@Override
	public int compareTo(Player o) {
		
		// In descending order
		return - (Integer.compare(this.score(), o.score()));
	}
	
	private int score() {
		//TODO sonaqube
		int score = cards.stream().mapToInt(Card::getValue).sum();
		return score;
	}
	
}
