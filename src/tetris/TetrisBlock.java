package tetris;

import java.awt.Color;
import java.util.Random;

public class TetrisBlock {
	protected int[][] shape;
	private Color color;
	private int x, y;
	protected int[][][] shapes;
	private int currentRotation;
	
	// 블럭 색 지정에 사용할 변수들
	private Color[][] allColors;
	private Color[] availableColors;
	
	public TetrisBlock(int[][] shape) {
		this.shape = shape;
		initColors();
		initShapes();
	}
	
	private void initColors() {
		allColors = new Color[2][];
		// 일반 모드 블럭색
		allColors[0] = new Color[] { Color.green, Color.red, Color.blue, Color.orange, Color.yellow,
				Color.magenta, Color.pink };
		// 색맹 모드 블럭색
		allColors[1] = new Color[] { new Color(230,160,40), new Color(90,180,230), new Color(20,160,120), new Color(240,230,90), new Color(30,30,30),
				new Color(210, 90, 20), new Color(200, 120, 170) };
		
		availableColors = allColors[Tetris.getColorMode()];
	}

	protected void initShapes() {
		shapes = new int[4][][];
		
		for(int i = 0; i < 4; i++) {
			int r = shape[0].length; // col -> row
			int c = shape.length; // row -> col
			shapes[i] = new int[r][c];
			
			for(int y = 0; y < r; y++) {
				for(int x = 0; x < c; x++) {
					shapes[i][y][x] = shape[c - x - 1][y];
				}
			}
			shape = shapes[i];
		}
	}
	
	// 블럭의 초기 낙하 위치를 랜덤으로 
	public void spawn(int gridWidth) {
		Random r = new Random();
		x = r.nextInt(gridWidth - getWidth());
		y = -getHeight();
	}
	
	// 블럭의 초기 방향을 랜덤으로 
	public void setShape() {
		Random r = new Random();
		currentRotation = r.nextInt(shapes.length);
		shape = shapes[currentRotation];
	}

	public int[][] getShape() { return shape; }
	public Color getColor() { return color; }
	
	// 블럭 색 설정
	public void setColor(int colorNum) { this.color = availableColors[colorNum]; }
	public void setColor(Color color) {	this.color = color; }
	
	public int getHeight() { return shape.length; }
	public int getWidth() { return shape[0].length; }
	
	public int getX() { return x; }
	public void setX(int newX) { x = newX; }
	public int getY() { return y; }
	public void setY(int newY) { y = newY; }
	
	// 회전 확인을 위해 추가한 함수
	public int getCurrentRotation() { return currentRotation; }
	public void setCurrentRotation(int newCurrentRotation) { currentRotation = newCurrentRotation; }
	
	public void moveDown() { y++; }
	public void moveLeft() { x--; }
	public void moveRight() { x++; }
	
	public void rotate() { // 시계 방향으로 회전
		currentRotation++;
		if(currentRotation > 3) currentRotation = 0;
		
		// 방향 바꿔서 2차원 배열 업데이트 
		shape = shapes[currentRotation]; 
	}
	
	public int getBottomEdge() { return y + getHeight(); }
	public int getLeftEdge() { return x; } // getX()와 똑같지만 코드의 가독성을 위해 함수 따로 정의하기
	public int getRightEdge() { return x + getWidth(); }
}