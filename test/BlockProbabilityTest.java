import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import tetris.GameArea;

class BlockProbabilityTest {

	@Test
	void testMakeRandom() {
		final GameArea ga = new GameArea(600,450,10);

		int[] cnt = new int[3];
		for(int j = 0; j < 3; j++) {
			for(int i = 0; i < 1000; i++) {
				int r = ga.makeRandom(j);
				if(r == 0)
					cnt[j]++;
			}
		}

		assertEquals(cnt[0], 167, 50);
		assertEquals(cnt[1], 167, 50);
		assertEquals(cnt[2], 167, 50);

	}

}