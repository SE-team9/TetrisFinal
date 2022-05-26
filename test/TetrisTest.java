package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.GameForm;
import form.LeaderboardForm;
import tetris.Tetris;

class TetrisTest {
	private static GameForm gf;
	private static Tetris tetris;
	private static LeaderboardForm lf;

	@Test 
	void tetrisTimeout() { // ������ �䱸����
		assertTimeout(Duration.ofMillis(1000), () -> {
			new Tetris();
			Thread.sleep(300);
		});
	}

	@Test
	void testShowStartup() {
		fail("Not yet implemented");
	}

	@Test
	void testStart() {
		fail("Not yet implemented");
	}

	@Test
	void testShowOption() {
		fail("Not yet implemented");
	}

	@Test
	void testShowLeaderboard() {
		fail("Not yet implemented");
	}

	@Test
	void testGetGameMode() { // ���� ��� �߰��� ���� �ʿ�
		int m = tetris.getGameMode();
		assertTrue(m == 0 || m == 1);
	}

	@Test
	void testGetKeyMode() {
		fail("Not yet implemented");
	}

	@Test
	void testGetGameLevel() {
		int l = tetris.getGameLevel();
		assertTrue(l == 0 || l == 1 || l == 2);
	}

	@Test
	void testGetColorMode() {
		int r = tetris.getColorMode();
		assertTrue(r == 0 || r == 1);
	}

	@Test
	void testGameOver() {
		fail("Not yet implemented");
	}

	@Test
	void testMain() {
		fail("Not yet implemented");
	}

}