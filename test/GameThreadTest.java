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
		gt.run();
		int gm = gt.getGameMode();
		assertNotNull(gm);
	}

	@Test
	void testGameThread() {
		int spl = gt.getSpeedUpPerLevel();
		assertTrue(spl == 80 || spl == 100 || spl == 120);
	}
	
	@Test
	void testGetSpeedUpPerLevel() {
		int spl = gt.getSpeedUpPerLevel();
		assertTrue(spl == 80 || spl == 100 || spl == 120);
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