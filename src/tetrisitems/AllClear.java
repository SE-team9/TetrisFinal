package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class AllClear extends TetrisBlock {
	public AllClear() {
		super(new int[][] {{ 1 }});
		setColor(Color.CYAN);
	}
}