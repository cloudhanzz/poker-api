package jiayun.han.game.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Get the count of how many cards per suit are left undealt in the game deck
 * (example: 5 hearts, 3 spades, etc.)
 * 
 * @author Jiayun Han
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UndeltCardsSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private int spades;
	private int hearts;
	private int diamonds;
	private int clubs;

	public int getSpades() {
		return spades;
	}

	public void setSpades(int spades) {
		this.spades = spades;
	}

	public UndeltCardsSummary withSpades(int value) {
		setSpades(value);
		return this;
	}

	public int getHearts() {
		return hearts;
	}

	public void setHearts(int hearts) {
		this.hearts = hearts;
	}

	public UndeltCardsSummary withHearts(int value) {
		setHearts(value);
		return this;
	}

	public int getDiamonds() {
		return diamonds;
	}

	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
	}

	public UndeltCardsSummary withDiamonds(int value) {
		setDiamonds(value);
		return this;
	}

	public int getClubs() {
		return clubs;
	}

	public void setClubs(int clubs) {
		this.clubs = clubs;
	}

	public UndeltCardsSummary withClubs(int value) {
		setClubs(value);
		return this;
	}
}
