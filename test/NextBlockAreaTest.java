//package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import tetris.GameArea;
import tetris.NextBlockArea;
import tetris.Tetris;
import tetris.TetrisBlock;

class NextBlockAreaTest {

	final GameArea ga = new GameArea(600, 450, 10);
	private NextBlockArea nba = new NextBlockArea(600, 450);
	private TetrisBlock nextBlock = ga.getBlock();


	@Test
	void testInitNextBlockArea() {
		nba.initNextBlockArea();
		assertFalse(nba.getIsItem());
	}

	@Test
	void testUpdateNBA() {
		nba.updateNextBlock();
		assertNotNull(nextBlock);
	}

	@Test
	void testSetIsItem() {
		nba.setIsItem(true);
		assertTrue(nba.getIsItem() == true );
	}

	@Test
	void testPaintComponentGraphics() {
		assertNotNull(nba.getIsItem());
	}
	
	@Test
	void testUpdateNextItem() {
		nba.updateNextBlock();
		assertNotNull(nba.getRandIndex());
		assertNotNull(nba.getBlockIndex());
	}
	
	@Test
	void testGetGridCellSize() {
		assertNotNull(nba.getGridCellSize());
	}
	
	@Test
	void testUpdatePanelSize() {
		Tetris.setFrameSize(1);
		NextBlockArea nba2 = new NextBlockArea(600, 450);
		int w = nba2.getWidth();
		assertEquals(w, 140);
		
		Tetris.setFrameSize(2);
		NextBlockArea nba3 = new NextBlockArea(600, 450);
		w = nba3.getWidth();
		assertEquals(w, 160);
		
		Tetris.setFrameSize(0);
		NextBlockArea nba4 = new NextBlockArea(600, 450);
		w = nba4.getWidth();
		assertEquals(w, 120);
		
	}
	
}