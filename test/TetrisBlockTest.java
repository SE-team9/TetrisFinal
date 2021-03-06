//package test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import form.GameForm;
import tetris.GameArea;
import tetris.NextBlockArea;
import tetris.Tetris;
import tetris.TetrisBlock;

class TetrisBlockTest {
	private static GameForm gf;
	private static Tetris tetris;
	private static TetrisBlock tb;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		final NextBlockArea nba = new NextBlockArea(600, 450);
		final GameArea ga = new GameArea(600, 450, 10);
		//ga.initBlocks();
		nba.initNextBlockArea();
	}

	@Test
	void testGetColor() {
		Color c = tb.getColor();
		assertNotNull(c);
	}



	@Test
	void testGetX() {
		final GameArea ga = new GameArea(600, 450, 10);
		final NextBlockArea nba = new NextBlockArea(600, 450);
		//ga.initBlocks();
		nba.initNextBlockArea();
		tb.setX(0);
		assertNotNull(tb.getX());
	}

	@Test
	void testSetX() {
		final NextBlockArea nba = new NextBlockArea(600, 450);
		nba.initNextBlockArea();
		tb.setX(0);
		assertTrue(tb.getX()==0);
	}


	@Test
	void testSetY() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCurrentRotation() {
		fail("Not yet implemented");
	}

	@Test
	void testSetCurrentRotation() {
		fail("Not yet implemented");
	}

	@Test
	void testMoveDown() {
		fail("Not yet implemented");
	}

	@Test
	void testMoveLeft() {
		fail("Not yet implemented");
		
	}

	@Test
	void testMoveRight() {
		fail("Not yet implemented");
	}

	@Test
	void testRotate() {
		fail("Not yet implemented");
	}

	@Test
	void testGetBottomEdge() {
		fail("Not yet implemented");
	}

	@Test
	void testGetLeftEdge() {
		fail("Not yet implemented");
	}

	@Test
	void testGetRightEdge() {
		fail("Not yet implemented");
	}

}