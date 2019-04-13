package jiayun.han.game.model;

import static jiayun.han.game.util.Constants.CARDS_OF_ONE_DECK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jiayun.han.game.enums.Suit;
import jiayun.han.game.util.Tool;

/**
 * <p>
 * A game deck contains one or more decks
 * 
 * @author Jiayun Han
 */
public final class GameDeck {

	private int numOfDecks;

	private List<Integer> cardIds;
	private final Map<Integer, Card> cardsLookup = new HashMap<>();

	public GameDeck() {
		this(1);
	}
	
	public GameDeck(int numOfDecks) {

		cardIds = new ArrayList<>(numOfDecks * CARDS_OF_ONE_DECK);
		
		for(int i = 0; i < numOfDecks; i++) {
			addDeck();
		}
		
		Tool.shuffleList(cardIds, 0);
	}
	
	public Deck addDeck() {

		// The id of a card.
		int id = cardsLookup.size();

		Deck deck = new Deck();
		
		for (Suit suit : Suit.values()) {
			
	        List<Card> cards = new ArrayList<>();
			for (int value = 1; value <= 13; value++) {
				id++;

				Card card = new Card(suit, value);
				cards.add(card);
				
				cardsLookup.put(id, card);
				cardIds.add(id);
			}
			
			deck.addSuit(suit, cards);
		}

		numOfDecks++;
		
		return deck;
	}
	
	public void addDeck(int start) {
		addDeck();		
		Tool.shuffleList(cardIds, start);
	}

	/**
	 * Returns the card whose id matches the given id.
	 * 
	 * @param id The id whose card to be returned
	 * @return The card whose id matches the given id. As this will be called by
	 *         {@link Dealer} who ensures that it always gives the valid card id, no
	 *         {@code null} will ever be returned
	 */
	public Card getCard(int index) {
		int id = cardIds.get(index);
		return cardsLookup.get(id);
	}
	
	public int getNumberOfDecks() {
		return numOfDecks;
	}
	
	public List<Card> getRemainingCards(int start){
		
		List<Card> cards = new ArrayList<>();
		
		for(int index = start; index < cardIds.size(); index++) {
			cards.add(getCard(index));
		}
		
		return cards;
	}
	
    public int getNumOfRemainingCards(int start){		
		return cardIds.size() - start;
	}
    
    public void shuffle(int start) {
    	Tool.shuffleList(cardIds, start);
    }
}
