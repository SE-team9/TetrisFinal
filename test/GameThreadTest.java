//package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import form.GameForm;
import tetris.AttackLineArea;
import tetris.GameArea;
import tetris.GameThread;
import tetris.NextBlockArea;
import tetris.Tetris;

class GameThreadTest {

	final GameArea ga = new GameArea(600, 450, 10);
	final GameArea ga2 = new GameArea(600, 450, 10);
	final AttackLineArea ala = new AttackLineArea(50, 60, 50);
	private NextBlockArea nba = new NextBlockArea(600, 450);
	private NextBlockArea nba2 = new NextBlockArea(600, 450);

	final GameForm gf = new GameForm(600,450);
	final GameForm gf2 = new GameForm(600,450);
	final GameThread gt = new GameThread(gf, ga, nba);
	final GameThread gt2 = new GameThread(gf2, ga2, nba2, ala, 0);
	
	@Test
	void testRun() {
		gt.run();
		int gm = gt.getGameMode();
		assertNotNull(gm);
	}
	
	@Test
	void testGameThread() {
		gt.setLevelMode(0);
		int spl = gt.getSpeedUpPerLevel();
		assertTrue(spl == 80);
		gt.setLevelMode(1);
		spl = gt.getSpeedUpPerLevel();
		assertTrue(spl == 100);
		gt.setLevelMode(2);
		spl = gt.getSpeedUpPerLevel();
		assertTrue(spl == 120);
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