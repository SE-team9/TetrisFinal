package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class TwoLineClear extends TetrisBlock {
	public TwoLineClear() {
		super(new int[][] { { 1, 1 }, { 1, 1 } });
		setColor(Color.CYAN);
	}
}