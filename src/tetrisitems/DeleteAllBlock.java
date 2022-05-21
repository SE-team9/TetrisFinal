package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class DeleteAllBlock extends TetrisBlock {
	public DeleteAllBlock() {
		super(new int[][] {{1}});
		setColor(Color.CYAN);
	}
}