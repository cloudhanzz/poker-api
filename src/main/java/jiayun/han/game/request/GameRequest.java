package jiayun.han.game.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * 
 * @author Jiayun Han
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	private String dealerName;
	private List<String> playerNames;
	private int numOfDecks;

	public GameRequest() {
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public List<String> getPlayerNames() {
		return playerNames;
	}

	public void setPlayerNames(List<String> playerNames) {
		this.playerNames = (playerNames == null) ? new ArrayList<>() : playerNames;
	}

	public int getNumOfDecks() {
		return numOfDecks;
	}

	public void setNumOfDecks(int numOfDecks) {
		this.numOfDecks = numOfDecks;
	}
}
