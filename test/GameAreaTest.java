//package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tetris.GameArea;
import tetris.NextBlockArea;
import tetris.TetrisBlock;

class GameAreaTest {
	GameArea ga = new GameArea(600, 460, 10);
	NextBlockArea nba = new NextBlockArea(600, 460);
	TetrisBlock tb = nba.getNextBlock();

	@Test
	@DisplayName("배경 초기화 테스트")
	public void testInitBackgroundArray() {
		ga.initBackgroundArray();
		Color[][] background = ga.getBackgroundArray();
		assertNotNull(background);
	}
	
	@Test
	@DisplayName("outOfBounds 테스트")
	void testIsBlockOutOfBounds() {
		assertNotNull(ga.isBlockOutOfBounds());
	}
}