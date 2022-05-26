import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tetris.GameArea;
import tetris.TetrisBlock;

class GameAreaTest {

	private final GameArea ga = new GameArea(600,450,10);

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@Disabled
	void testGameArea() {
		fail("Not yet implemented");
	}
	
	@Test
	@Disabled
	void testInitThisPanel() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("��� �ʱ�ȭ")
	void testInitBackgroundArray() {
		ga.initBackgroundArray();
		Color[][] background = ga.getBackgroundArray();
		assertNotNull(background);
	}
	
//	@Test
//	@DisplayName("���� ������ �ʱ�ȭ")
//		ga.initBlocks();
//		
//		TetrisBlock[] blocks = ga.getBlocks();
//		assertNotNull(blocks);
//		assertEquals(blocks.length, 6);
//		
//		TetrisBlock[] items = ga.getItems();
//		assertNotNull(items);
//		assertEquals(items.length, 5);
//	}

	@Test
	@Disabled
	void testGetGridCellSize() {
		fail("Not yet implemented");
	}


	@Test
	@Disabled
	void testGetWeightedRandom() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testMakeRandom() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("���� �� ����")
	void testUpdateNextBlock() {
		TetrisBlock[] blocks = ga.getBlocks();

		ga.updateNextBlock();
		TetrisBlock nextblock = ga.getNextBlock();

		int same = 0;
		for (int i = 0; i < blocks.length; i++) {
			if (nextblock == blocks[i])
				same++;
		}
		assertEquals(same, 1);
	}

	@Test
	@DisplayName("���� ������ ����")
	void testUpdateNextItem() {
		TetrisBlock[] items = ga.getItems();

		ga.updateNextItem();
		TetrisBlock nextitem = ga.getNextBlock();

		int same = 0;
		for (int i = 0; i < items.length; i++) {
			if (nextitem == items[i])
				same++;
		}
		assertEquals(same, 1);
	}

	@Test
	@Disabled
	void testGetNextBlock() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testGetBlock() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("�� ����")
	void testSpawnBlock() {
		ga.updateNextBlock();
		TetrisBlock nextblock = ga.getNextBlock();

		ga.spawnBlock();
		TetrisBlock currentblock = ga.getBlock();

		assertEquals(currentblock, nextblock);
		assertEquals(currentblock.getY(), -currentblock.getHeight());

		int curX = currentblock.getX();
		int curBlockWidth = currentblock.getWidth();
		int gridColumns = ga.getGridColumns();

		assertTrue(curX > 0 && curX < gridColumns - curBlockWidth);
	}

	@Test
	void testIsBlockOutOfBounds() {
		assertNotNull(ga.isBlockOutOfBounds());
	}

	@Test
	@DisplayName("�� �Ʒ��� �̵�")
	void testMoveBlockDown() {

		ga.initBackgroundArray();
		ga.spawnBlock();

		int y = ga.getBlock().getY();

		if (ga.moveBlockDown()) {
			assertEquals(ga.getBlock().getY(), y + 1);
		} else {
			assertEquals(ga.getBlock().getY(), y);
		}
	}

	@Test
	@DisplayName("�� ���������� �̵�")
	void testMoveBlockRight() {

		ga.initBackgroundArray();
		ga.spawnBlock();

		int x = ga.getBlock().getX();

		if (ga.checkRight()) {
			ga.moveBlockRight();
			assertEquals(ga.getBlock().getX(), x + 1);
		} else {
			ga.moveBlockRight();
			assertEquals(ga.getBlock().getX(), x);
		}
	}

	@Test
	@DisplayName("�� �������� �̵�")
	void testMoveBlockLeft() {

		ga.initBackgroundArray();
		ga.spawnBlock();

		int x = ga.getBlock().getX();

		if (ga.checkLeft()) {
			ga.moveBlockLeft();
			assertEquals(ga.getBlock().getX(), x - 1);
		} else {
			ga.moveBlockLeft();
			assertEquals(ga.getBlock().getX(), x);
		}
	}

	@Test
	@Disabled
	void testDropBlock() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("�� ȸ��")
	void testRotateBlock() {

		ga.initBackgroundArray();
		ga.spawnBlock();

		int[][] shape = ga.getBlock().getShape();

		int r = shape[0].length;
		int c = shape.length;

		int[][] rotatedShape = new int[r][c];

		for (int y = 0; y < r; y++) {
			for (int x = 0; x < c; x++) {
				rotatedShape[y][x] = shape[c - x - 1][y];
			}
		}

		if (ga.checkRotate()) {
			ga.rotateBlock();
			assertArrayEquals(ga.getBlock().getShape(), rotatedShape);
		} else {
			ga.rotateBlock();
			assertArrayEquals(ga.getBlock().getShape(), shape);
		}
	}

	@Test
	@Disabled
	void testFillEmpty() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testTwoLineDelete() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testDeleteAroundU() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testWeight() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testItemFunction() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testTwinkleItem() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testMoveBlockToBackground() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testClearLines() {
		fail("Not yet implemented");
	}

	@Test
	@Disabled
	void testPaintComponentGraphics() {
		fail("Not yet implemented");
	}

}