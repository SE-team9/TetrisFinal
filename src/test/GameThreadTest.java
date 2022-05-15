package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import form.GameForm;
import tetris.GameArea;
import tetris.GameThread;
import tetris.NextBlockArea;

class GameThreadTest {
	
	final GameArea ga = new GameArea(600, 450, 10);
	private NextBlockArea nba = new NextBlockArea(600, 450, ga);
	
	final GameForm gf = new GameForm(600,450);
	final GameThread gt = new GameThread(gf, ga, nba);

	@Test
	void testRun() {
		fail("Not yet implemented");
	}

	@Test
	void testGameThread() {
		fail("Not yet implemented");
	}

	@Test
	void testPause() {
		gt.pause();
		assertTrue(gt.getIsPaused());
	}

	@Test
	void testReStart() {
		gt.reStart();
		assertFalse(gt.getIsPaused());
	}

	@Test
	void testGetIsPaused() {
		assertNotNull(gt.getIsPaused());
	}

}
