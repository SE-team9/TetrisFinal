package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class DeleteOneLine extends TetrisBlock {
	public DeleteOneLine() {
		super(new int[][] { { 1 } });
		setColor(Color.CYAN);
	}
}