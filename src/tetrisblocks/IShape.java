package tetrisblocks;

import tetris.TetrisBlock;

public class IShape extends TetrisBlock {

	public IShape() {
		// 블럭의 형태 결정
		super(new int[][] { { 1, 1, 1, 1 } });
		
		// 블럭의 색 결정
		setColor(0);
	}

	@Override
	public void rotate() {
		super.rotate();

		if (this.getWidth() == 1) {
			this.setX(this.getX() + 1);
			this.setY(this.getY() - 1);
		}else {
			this.setX(this.getX() - 1);
			this.setY(this.getY() + 1);
		}
	}
}