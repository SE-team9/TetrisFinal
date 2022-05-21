package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class DeleteTwoLine extends TetrisBlock {
	public DeleteTwoLine() {
		super(new int[][] { { 1, 1 }, { 1, 1 } });
		setColor(Color.CYAN);
	}
}