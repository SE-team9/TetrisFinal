package tetris;

import java.awt.Color;
import java.util.Random;

public class TetrisBlock {
	protected int[][] shape;
	private Color color;
	private int x, y;
	protected int[][][] shapes;
	private int currentRotation;
	
	// �� �� ������ ����� ������
	private Color[][] allColors;
	private Color[] availableColors;
	
	public TetrisBlock(int[][] shape) {
		this.shape = shape;
		initColors();
		initShapes();
	}
	
	private void initColors() {
		allColors = new Color[2][];
		// �Ϲ� ��� ����
		allColors[0] = new Color[] { Color.green, Color.red, Color.blue, Color.orange, Color.yellow,
				Color.magenta, Color.pink };
		// ���� ��� ����
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
	
	// ���� �ʱ� ���� ��ġ�� �������� 
	public void spawn(int gridWidth) {
		Random r = new Random();
		x = r.nextInt(gridWidth - getWidth());
		y = -getHeight();
	}
	
	// ���� �ʱ� ������ �������� 
	public void setShape() {
		Random r = new Random();
		currentRotation = r.nextInt(shapes.length);
		shape = shapes[currentRotation];
	}

	public int[][] getShape() { return shape; }
	public Color getColor() { return color; }
	
	// �� �� ����
	public void setColor(int colorNum) { this.color = availableColors[colorNum]; }
	public void setColor(Color color) {	this.color = color; }
	
	public int getHeight() { return shape.length; }
	public int getWidth() { return shape[0].length; }
	
	public int getX() { return x; }
	public void setX(int newX) { x = newX; }
	public int getY() { return y; }
	public void setY(int newY) { y = newY; }
	
	// ȸ�� Ȯ���� ���� �߰��� �Լ�
	public int getCurrentRotation() { return currentRotation; }
	public void setCurrentRotation(int newCurrentRotation) { currentRotation = newCurrentRotation; }
	
	public void moveDown() { y++; }
	public void moveLeft() { x--; }
	public void moveRight() { x++; }
	
	public void rotate() { // �ð� �������� ȸ��
		currentRotation++;
		if(currentRotation > 3) currentRotation = 0;
		
		// ���� �ٲ㼭 2���� �迭 ������Ʈ 
		shape = shapes[currentRotation]; 
	}
	
	public int getBottomEdge() { return y + getHeight(); }
	public int getLeftEdge() { return x; } // getX()�� �Ȱ����� �ڵ��� �������� ���� �Լ� ���� �����ϱ�
	public int getRightEdge() { return x + getWidth(); }
}