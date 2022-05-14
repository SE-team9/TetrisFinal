package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class OneLineDelete extends TetrisBlock {

	public OneLineDelete() {
		super(new int[][] { { 1 } });
		setColor(Color.orange);
	}
}