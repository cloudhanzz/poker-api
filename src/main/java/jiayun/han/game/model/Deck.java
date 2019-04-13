package jiayun.han.game.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jiayun.han.game.enums.Suit;

/**
 * <p>
 * A deck contains 52 cards
 * 
 * @author Jiayun Han
 */
public final class Deck {

	private Map<Suit, List<Card>> cards;

	public Deck() {
		this.cards = new HashMap<>();
	}
	
	public void addSuit(Suit suit, List<Card> cards2) {
		cards.put(suit, cards2);
	}

	public List<Card> getSpades() {
		return cards.get(Suit.SPADES);
	}

	public List<Card> getHearts() {
		return cards.get(Suit.HEARTS);
	}

	public List<Card> getDiamonds() {
		return cards.get(Suit.DIAMONDS);
	}

	public List<Card> getClubs() {
		return cards.get(Suit.CLUBS);
	}
}
