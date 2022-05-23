package tetris;

import java.awt.Color;
import java.util.Random;

public class TetrisBlock {
	protected int[][] shape; // �� ��� 
	private int currentRotation;
	protected int[][][] shapes; // �� ������� ���� 
	private Color color;
	private int x, y;

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
		
		// �Ϲ� ���
		allColors[0] = new Color[] { Color.green, Color.red, Color.blue, Color.orange, Color.yellow, Color.magenta,
				Color.pink };
		
		// ���� ���
		allColors[1] = new Color[] { new Color(230, 160, 40), new Color(90, 180, 230), new Color(20, 160, 120),
				new Color(240, 230, 90), new Color(30, 30, 30), new Color(210, 90, 20), new Color(200, 120, 170) };

		// �������� ���� ��忡 ���� ���� �� ���� 
		availableColors = allColors[Tetris.getColorMode()];
	}

	// �� ȸ���� ���� ��� ���� 
	protected void initShapes() {
		shapes = new int[4][][];

		for (int i = 0; i < 4; i++) {
			int r = shape[0].length; // col -> row
			int c = shape.length; // row -> col
			shapes[i] = new int[r][c];

			for (int y = 0; y < r; y++) {
				for (int x = 0; x < c; x++) {
					shapes[i][y][x] = shape[c - x - 1][y];
				}
			}
			
			// ���� ȸ�� ����Ͽ� �� ��� ������Ʈ 
			shape = shapes[i];
		}
	}

	// �� ���� ��ġ ��������
	public void spawn(int gridWidth) {
		Random r = new Random();
		x = r.nextInt(gridWidth - getWidth());
		y = -getHeight();
	}
	
	// ---------------------------------------------------------- ���� ����� ��� 
	public Color getColor() { return color; }
	public void setColor(Color color) { this.color = color; }
	
	// �ε����� ���� �⺻ ���� ���� �ʱ�ȭ  
	public void setColor(int i) { this.color = availableColors[i]; }
	
	public int[][] getShape() { return shape; }
	
	// ȸ���� ���� �޶����� �� ��� �ʱ�ȭ 
	public void setShape() {
		Random r = new Random();
		currentRotation = r.nextInt(shapes.length);
		shape = shapes[currentRotation];  
	}
	
	// ---------------------------------------------------------- �� ������ ���� 

	public int getHeight() { return shape.length; }
	public int getWidth() {	return shape[0].length; }
	
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y;}
	public void setY(int y) { this.y = y; }

	public void moveDown() { y++; }
	public void moveLeft() { x--; }
	public void moveRight() { x++; }

	public void rotate() { // �ð� �������� ȸ��
		currentRotation++;
		if (currentRotation > 3) currentRotation = 0;

		// ���� ȸ�� ����Ͽ� �� ��� ������Ʈ 
		shape = shapes[currentRotation];
	}

	public int getBottomEdge() { return y + getHeight(); }
	public int getLeftEdge() { return x; } 
	public int getRightEdge() { return x + getWidth(); }
	
	// ȸ�� Ȯ���� ���� �߰��� �Լ�
	public int getCurrentRotation() {
		return currentRotation;
	}

	public void setCurrentRotation(int newCurrentRotation) {
		currentRotation = newCurrentRotation;
	}
}