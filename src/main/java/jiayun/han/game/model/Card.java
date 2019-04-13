package jiayun.han.game.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jiayun.han.game.enums.Face;
import jiayun.han.game.enums.Suit;

/**
 * This class models a poker card.
 * 
 * @author Jiayun Han
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Card implements Serializable, Comparable<Card> {

	private static final long serialVersionUID = 1L;
	private Suit suit;	
	private int value;
	private Face face;

	public Card() {
	}

	/**
	 * Creates a Card object
	 * 
	 * @param suit
	 *            The suit of this card which is {@link Suit#SPADES}, or
	 *            {@link Suit#CLUBS}, or {@link Suit#HEARTS}, or
	 *            {@link Suit#DIAMONDS}. Use {@code null} along with the value of
	 *            {@code -1} to represent a dummy card
	 * @param value
	 *            The value of this card.
	 *            
	 * TODO add value scope check 1-13
	 */
	public Card(Suit suit, int value) {
		this.suit = suit;
		this.value = value;
		this.face = Face.parse(value);
	}

	/**
	 * Returns the suit of this card
	 * 
	 * @return The suit of this card
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Returns the value of this card
	 * 
	 * @return The value of this card
	 */
	public int getValue() {
		return value;
	}
	
	public Face getFace() {
		return face;
	}
	@Override
	public String toString() {
		
		String s = "";
		String strVal = "";
		Face face = Face.parse(value);
		if(face == null) {
			strVal = String.valueOf(value);
		}else {
			strVal = face.name();
		}
		
		s = suit.name() + " " + strVal;
		
		return s;
	}

	@Override
	public int compareTo(Card o) {
		int result = Integer.compare(this.suit.getValue(), o.suit.getValue());
		if(result == 0) {
			
			// in descending
			result = Integer.compare(this.value, o.value) * (-1);
		}
		return result;
	}
}
