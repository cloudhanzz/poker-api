package jiayun.han.game.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The constants of this enum represent the value of the four face cards.
 * 
 * @author Jiayun Han
 *
 */
public enum Face {

	ACE(1),

	JACK(11),

	QUEEN(12),

	KING(13);

	private int value;

	private static Map<Integer, Face> map;
	static {
		map = new HashMap<>();
		for (Face face : values()) {
			map.put(face.value, face);
		}
	}

	/**
	 * For internal instantiation
	 * 
	 * @param value
	 */
	private Face(int value) {
		this.value = value;
	}

	/**
	 * Returns a constant of this enum by parsing the the passed integer.
	 * 
	 * @param value
	 *            The integer to parse into a constant of this enum
	 * @return A constant of this enum if the passed value is a valid value;
	 *         null otherwise
	 */
	public static Face parse(int value) {
		return map.get(value);
	}
}
