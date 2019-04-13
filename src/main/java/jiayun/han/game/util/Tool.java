package jiayun.han.game.util;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Jiayun Han
 *
 */
public final class Tool {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Tool.class);
	private static final Random RAND = new Random();

	private Tool() {
	}
	
	public static <T> void shuffleList(List<T> list, int start) {
		
		int bound = list.size() - start;

		for (int i = start; i < list.size(); ++i) {

			// randomly generate a different swapping index
			int j = start + RAND.nextInt(bound);

			while (i == j) {
				j = start + RAND.nextInt(bound);
			}
			
			T temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
			
		}

		//System.out.println(list);
		LOGGER.info("Shuffled cards");
	}
	
	public static boolean isEmptyOrNullString(String s) {
		if(s == null) {
			return true;
		}
		
		return s.trim().isEmpty();
	}

}
