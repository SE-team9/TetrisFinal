package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class TwoLineDelete extends TetrisBlock {

	public TwoLineDelete() {
		super(new int[][] { { 1, 1 }, { 1, 1 } });
		setColor(Color.lightGray);
	}
}