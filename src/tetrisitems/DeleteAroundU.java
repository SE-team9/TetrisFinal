package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class DeleteAroundU extends TetrisBlock {

	public DeleteAroundU() {
		super(new int[][] {{1, 0, 1}, {1, 1, 1}});
		setColor(Color.black);
	}
}