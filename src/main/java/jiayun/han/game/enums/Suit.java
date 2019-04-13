package jiayun.han.game.enums;

/**
 * The constants of this enum represent the four suits of a deck of poker cards
 * 
 * @author Jiayun Han
 *
 */
public enum Suit {
	
	HEARTS(1), SPADES(2), CLUBS(3), DIAMONDS(4);
	
	private int value;
	
	/**
	 * For internal instantiation
	 * 
	 * @param value
	 */
	private Suit(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
