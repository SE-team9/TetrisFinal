import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import form.GameForm;
import tetris.GameArea;
import tetris.Tetris;
import tetris.TetrisBlock;

class TetrisBlockTest {
	private static GameForm gf;
	private static Tetris tetris;
	private static TetrisBlock tb;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		final GameArea ga = new GameArea(600, 450, 10);
		ga.initBlocks();
	}

	@Test
	void testTetrisBlock() {
		fail("Not yet implemented");
	}

	@Test
	void testSpawn() {
		fail("Not yet implemented");
	}

	@Test
	void testSetShape() {
		fail("Not yet implemented");
	}

	@Test
	void testGetShape() {
		fail("Not yet implemented");
	}

	@Test
	void testGetColor() {
		fail("Not yet implemented");
	}

	@Test
	void testSetColorInt() {
		fail("Not yet implemented");
	}

	@Test
	void testSetColorColor() {
		fail("Not yet implemented");
	}

	@Test
	void testGetHeight() {
		assertNotNull(tb.getHeight());
	}

	@Test
	void testGetWidth() {
		fail("Not yet implemented");
	}

	@Test
	void testGetX() {
		final GameArea ga = new GameArea(600, 450, 10);
		ga.initBlocks();
		tb.setX(0);
		assertNotNull(tb.getX());
	}

	@Test
	void testSetX() {
		fail("Not yet implemented");
	}

	@Test
	void testGetY() {
		fail("Not yet implemented");
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