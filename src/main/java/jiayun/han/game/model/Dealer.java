package jiayun.han.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jiayun.han.game.enums.ExceptionCode;
import jiayun.han.game.exception.checked.InvalidRequestException;
import jiayun.han.game.util.Tool;

/**
 * It models a poker dealer.
 * 
 * @author Jiayun Han
 */
public class Dealer {

	/*
	 * A running index pointing to one of the card-id array elements
	 */
	private int currentIndex;
	
	private String id;
	private String name;

	/**
	 * Constructing an instance of this class. Once a dealer is created, the ID's of
	 * the 52 cards will be stored in an array and they are shuffled for the first
	 * time.
	 */
	public Dealer() {
		this(null);
	}
	
	
	public Dealer(String name) {
		currentIndex = 0;
		id = UUID.randomUUID().toString();
		if(Tool.isEmptyOrNullString(name)) {
			this.name = "dealer-" + id;
		}else {
			this.name = name.trim();
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
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


	/**
	 * Returns the next available card or a dummy card if no card is available
	 * 
	 * <p>
	 * The algorithm is to check the running index of the array of card ID's: if the
	 * running index is out of range, return a dummy card; otherwise, locate the
	 * card ID pointed by this running index, then retrieve the corresponding card
	 * from the deck.
	 * 
	 * @return The next available card or a dummy card if no card is available
	 * @throws InvalidRequestException 
	 */
	public synchronized List<Card> deal(Game game, int num) throws InvalidRequestException {

		if (currentIndex + num > game.getNumOfCards()) {			
			throw new InvalidRequestException(ExceptionCode.INADIQUATE_REMAINING_CARDS, "No enough cards to deal");
		}

		List<Card> cards = new ArrayList<>();
		for(int i = 0; i< num; i++) {
			Card card = game.getCard(currentIndex++);
			cards.add(card);
		}
		
		return cards;
	}


	public synchronized void shuffle(Game game, boolean reset) {
		
		if(reset || currentIndex == game.getNumOfCards()) {
			
			currentIndex = 0;
			
			for(Player player: game.getPlayers().values()) {
				player.setCards(new ArrayList<>());
			}
		}
		
		game.getGameDesk().shuffle(currentIndex);
	}
	
	
}
