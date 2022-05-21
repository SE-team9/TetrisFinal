package tetrisitems;

import java.awt.Color;

import tetris.TetrisBlock;

public class DeleteAroundU extends TetrisBlock {
	public DeleteAroundU() {
		super(new int[][] {{1, 0, 1}, {1, 1, 1}});
		setColor(Color.CYAN);
	}
	
	// 블럭을 회전시킨 모양이 모두 동일하도록 오버라이딩 
	@ Override
	protected void initShapes() {
		shapes = new int[4][][];
		
		for(int i = 0; i < 4; i++) {
			int r = shape.length;
			int c = shape[0].length;
			shapes[i] = new int[r][c];
			
			for(int y = 0; y < r; y++) {
				for(int x = 0; x < c; x++) {
					shapes[i][y][x] = shape[y][x];
				}
			}
			
			shape = shapes[i];
		}
	}
}