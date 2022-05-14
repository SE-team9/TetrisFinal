package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class FillEmpty extends TetrisBlock {

	public FillEmpty() {
		super(new int[][] {{1}});
		setColor(Color.orange);
	}
}