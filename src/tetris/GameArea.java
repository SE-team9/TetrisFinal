package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tetrisblocks.*;
import tetrisitems.*;

/*
 *  �ʱ�ȭ 
 *  ���̵� ���� 
 *  �� ����
 *  �� ����, ��� Ȯ�� 
 *  ������ 
 *  ���, �� ���� 
 *  �׸��� 
 *  getter
 */

public class GameArea extends JPanel {
	private int w, h; // GameArea�� ũ�� 
	
	private int gridRows;
	private int gridColumns;
	private int gridCellSize;
	private boolean curIsItem; // ���� ���� ������ ������ Ȯ���ϱ� ���� ���� 
	
	private Color[][] background;
	private TetrisBlock[] blocks;
	private TetrisBlock[] items;
	
	private TetrisBlock block;
	private TetrisBlock nextBlock;

	// 2���� �迭 ���� ��, Į�� ũ�� ������ �ʼ� (�׷��� ���ڷ� ���� ����)
	public GameArea(int gfW, int gfH, int columns) {
		// GameForm ũ�⿡ ���� GameArea�� (x, y) ��ġ ���� 
		initThisPanel(gfW, gfH);
		initBlocks();
				
		// 20�� 10��������, ������ ���� ���� ũ�Ⱑ �ٲ�. 
		gridColumns = columns; // 10��
		gridCellSize = w / gridColumns; // 20 -> 25 -> 30 
		gridRows = h / gridCellSize; // 20�� 
		
		// ���� ������ ������ ������ ������Ʈ ����� ��. 
		curIsItem = false;
		updateNextBlock();
	}
	
	// --------------------------------------------------------------- �ʱ�ȭ 
	
	// ������ ũ�⿡ ���� �г��� ũ�⵵ ���� (20�� 10���� �״������ ���� ũ�Ⱑ �ٲ��)
	private void updatePanelSize() {
		try {
			File file = new File("settings.txt");
			if(!file.exists()) { 
				file.createNewFile(); 
				System.out.println("Create new file.");
			};
			
			FileInputStream fis = new FileInputStream(file);
			int data = fis.read();
			
			// ���Ͽ� ����� ���� ���� ũ�� ���� 
			if(data == 0) {
				this.w = 200;
				this.h = 400;
			}else if(data == 1) {
				this.w = 250;
				this.h = 500;
			}else {
				this.w = 300;
				this.h = 600;
			}
			
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initThisPanel(int gfW, int gfH) {
		updatePanelSize(); // ������ ���� w, h �ʱ�ȭ 
		
		this.setBounds(gfW / 3, gfH / 60, w, h);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}

	public void initBackgroundArray() {
		background = new Color[gridRows][gridColumns];
	}

	public void initBlocks() {
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), new ZShape(),
				new SShape() };
		
		items = new TetrisBlock[] { new FillEmpty(), new Weight(), new DeleteAroundU(), new TwoLineDelete(),
				new OneLineDelete() };
	}
	
	// ���� ������ ������ ������ ���� ������Ʈ 
	public void initGameArea() {
		initBackgroundArray();
		
		curIsItem = false;
		updateNextBlock();
	}
	
	public void setItemFlag(boolean flag) {
		curIsItem = flag;
	}

	// --------------------------------------------------------------- �� ����
	
	// ���� �� �������� 
	public void updateNextBlock() {
		int level = Tetris.getGameLevel();
		int r = makeRandom(level);
		nextBlock = blocks[r];
		nextBlock.setShape();
	}

	// ���� ������ �������� 
	public void updateNextItem() {
		Random r = new Random();
		nextBlock = items[r.nextInt(items.length)];
		nextBlock.setShape();
	}

	// ���� �� ����� �����Ͽ�, ���� �� �� ���� 
	public void spawnBlock() {
		block = nextBlock;
		block.spawn(gridColumns); // ������ ��ġ���� ���������� 
	}
	
	// --------------------------------------------------------------- ���̵� ����  
	
	// ����ġ ���� �Լ� ����
	public static <E> E getWeightedRandom(Map<E, Double> weights, Random random) {
		E result = null;
		double bestValue = Double.MAX_VALUE;

		for (E element : weights.keySet()) {
			double value = -Math.log(random.nextDouble()) / weights.get(element);
			if (value < bestValue) {
				bestValue = value;
				result = element;
			}
		}
		
		return result;
	}

	// ���̵��� ���� I�� ���� ���� Ȯ���� �ٸ��� �Ѵ�. 
	public int makeRandom(int level) {
		Map<String, Double> w = new HashMap<String, Double>();
		Random r = new Random();

		double weight, iWeight;
		int blockNum; // �� ���� 

		if (level == 0) { // easy
			weight = 14.0;
			iWeight = 16.0; // I�� ���� ���� Ȯ�� 20% ����
			w.put("0", iWeight);

			for (int i = 1; i < blocks.length; i++) {
				w.put(Integer.toString(i), weight);
			}

			blockNum = Integer.parseInt(getWeightedRandom(w, r));
		} else if (level == 2) { // hard
			weight = 15.0;
			iWeight = 10.0; // I�� ���� ���� Ȯ�� 20% ����
			w.put("0", iWeight);

			for (int i = 1; i < blocks.length; i++) {
				w.put(Integer.toString(i), weight);
			}

			blockNum = Integer.parseInt(getWeightedRandom(w, r));

		} else { // normal 
			blockNum = r.nextInt(blocks.length);
		}

		return blockNum;
	}

	// ------------------------------------------------------------- �� ���� / ��� Ȯ��
	
	// ���� ���� ��踦 �Ѿ���� Ȯ���Ѵ�.
	public boolean isBlockOutOfBounds() {
		if (block.getY() < 0) {
			block = null; // Ű �Է��ص� ����� �������� �ʵ���
			return true;
		}
		return false;
	}

	public boolean moveBlockDown() {
		// ���� ���� �ٴ��̳� �ٸ� ���� ������ false
		if (!checkBottom()) { 
			return false;
		}

		block.moveDown();
		repaint();

		return true;
	}

	public void moveBlockRight() {
		if (block == null)
			return;

		if (!checkRight())
			return;

		block.moveRight();
		repaint();
	}

	public void moveBlockLeft() {
		if (block == null)
			return;

		if (!checkLeft())
			return;

		block.moveLeft();
		repaint();
	}

	public void dropBlock() {
		if (block == null)
			return;

		while (checkBottom()) {
			block.moveDown();
		}

		repaint();
	}

	public void rotateBlock() {
		if (block == null)
			return;

		if (!checkRotate())
			return;

		block.rotate();

		// ȸ�� �� ��踦 �Ѿ�� �ʵ��� ��ġ ����
		if (block.getLeftEdge() < 0)
			block.setX(0);

		if (block.getRightEdge() >= gridColumns)
			block.setX(gridColumns - block.getWidth());

		if (block.getBottomEdge() >= gridRows)
			block.setY(gridRows - block.getHeight());

		repaint();
	}

	private boolean checkBottom() {
		if(block.getBottomEdge() == gridRows) {
			return false; // stop
		}
		
		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();
		
		for(int col = 0; col < w; col++) {
			// Ư�� ���� �� �ؿ��� �������� �ö󰡴ٰ� 
			for(int row = h - 1; row >= 0; row--) {
				// colored cell�� �߰��߰� 
				if(shape[row][col] != 0) { 
					int x = col + block.getX();
					int y = row + block.getY() + 1; // �ش� ��� �ٷ� �Ʒ���!
					
					// �����ǿ� ���Ե��� ���� ����� �����ϰ� ���� ���� �̵� 
					if(y < 0) break; 
					
					if(background[y][x] != null) { // ��׶��� ����� ������!
						return false; // stop
					}
					
					break; // ���� ���� ���̻� �˻��� �ʿ� ����.
				}
			}
		}
		
		return true; // keep going
	}

	private boolean checkLeft() {
		if(block.getLeftEdge() == 0) {
			return false; // stop
		}
		
		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();
		
		for(int row = 0; row < h; row++) {
			for(int col = 0; col < w; col++) {
				if(shape[row][col] != 0) { // colored cell
					int x = col + block.getX() - 1; // �ٷ� ���ʿ�!
					int y = row + block.getY();
					
					if(y < 0) break; 
					
					if(background[y][x] != null) { // ��׶��� ����� ������!
						return false; // stop
					}
					
					break; // ���� ���� ���̻� �˻��� �ʿ� ����.
				}
			}
		}
		
		return true; // keep going
	}

	private boolean checkRight() {
		if(block.getRightEdge() == gridColumns) {
			return false; // stop
		}
		
		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();
		
		for(int row = 0; row < h; row++) {
			for(int col = w - 1; col >= 0; col--) {
				if(shape[row][col] != 0) { // colored cell
					int x = col + block.getX() + 1; // �ٷ� �����ʿ�!
					int y = row + block.getY(); 
					
					if(y < 0) break;
					
					if(background[y][x] != null) { // ��׶��� ����� ������!
						return false; // stop
					}
					
					break; // ���� ���� ���̻� �˻��� �ʿ� ����.
				}
			}
		}
		
		return true; // keep going
	}

	// ���� ���� ȸ�������� ���� ��ü�� �����Ͽ� ��踦 �Ѵ��� Ȯ���Ѵ�.
	// ���� ������ �ٴڿ� ������ ���ÿ� ȸ����Ű�� ���� ���� ���� ��ġ�� �� ����� �������� �����Ƿ� ���� �ʿ�
	public boolean checkRotate() {
		TetrisBlock rotated = new TetrisBlock(block.getShape());
		rotated.setCurrentRotation(block.getCurrentRotation());
		rotated.setX(block.getX());
		rotated.setY(block.getY());
		rotated.rotate();

		if (rotated.getLeftEdge() < 0)
			rotated.setX(0);
		if (rotated.getRightEdge() >= gridColumns)
			rotated.setX(gridColumns - rotated.getWidth());
		if (rotated.getBottomEdge() >= gridRows)
			rotated.setY(gridRows - rotated.getHeight());

		int[][] shape = rotated.getShape();
		int w = rotated.getWidth();
		int h = rotated.getHeight();

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (shape[row][col] != 0) {
					int x = col + rotated.getX();
					int y = row + rotated.getY();

					if (y < 0)
						break;

					if (background[y][x] != null)
						return false;
				}
			}
		}
		
		return true;
	}

	// ------------------------------------------------------------- ������ 
	
	// �������� ��¦�Ÿ���.
	public void twinkleItem() {
		Color originColor = block.getColor();

		try {
			block.setColor(Color.white);
			repaint();
			Thread.sleep(200);
			block.setColor(originColor);
			repaint();
			Thread.sleep(200);
			block.setColor(Color.white);
			repaint();
			Thread.sleep(200);
			block.setColor(originColor);
			repaint();
			Thread.sleep(200);
		} catch (InterruptedException ex) {
			return;
		}
	}
	
	// ���� �������� ����� �����Ѵ�.
	public void execItemFunction() {
		if (this.block instanceof FillEmpty) {
			fillEmpty();
		} else if (this.block instanceof TwoLineDelete) {
			twoLineDelete();
		} else if (this.block instanceof Weight) {
			Weight();
		} else if (this.block instanceof DeleteAroundU) {
			DeleteAroundU();
		} else if (this.block instanceof OneLineDelete) {
			oneLineDelte();
		}
	}
	
	// ��ĭ �޲ٴ� ������ 
	public void fillEmpty() {
		int xPos = block.getX();
		int emptyNum = 0;
		int currentR;
		int nextR;

		for (int r = gridRows - 1; r > 0; r--) {
			if (background[r][xPos] == null) {
				emptyNum++;
				nextR = r - 1;
				while (nextR >= 0 && background[nextR][xPos] == null) {
					nextR--;
				}
				if (nextR == -1) {
					return;
				} else {
					currentR = r;
					for (; nextR >= 0; nextR--, currentR--) {
						background[currentR][xPos] = background[nextR][xPos];
						repaint();
					}
				}
			}
		}
		while (emptyNum > 0) {
			block.moveDown();
		}
		repaint();
	}

	// �� �� ���� ������ 
	public void oneLineDelte() {
		int yPos = block.getY();

		clearLine(yPos);
		shiftDown(yPos);
		
		repaint();
	}

	// �� �� ���� ������ 
	public void twoLineDelete() {
		int yPos = block.getY();
		int time = 0;

		for (int r = yPos + 2; r >= yPos + 1 && time < 2; r--) {
			clearLine(r);
			shiftDown(r);
			r++;
			time++;

			repaint();
		}
	}

	// ������ ������
	public void Weight() {
		for (int row = block.getBottomEdge(); row < gridRows; row++) {
			for (int col = block.getLeftEdge(); col < block.getRightEdge(); col++) {
				background[row][col] = null;
			}
			block.moveDown();
			repaint();
		}
		moveBlockToBackground();
	}

	// �¿�Ʒ��� ��� �����ϴ� �ٱ��� ������ 
	public void DeleteAroundU() {
		for (int y = block.getY(); y <= block.getBottomEdge(); y++) {
			if (y >= gridRows)
				break;
			for (int x = block.getX() - 1; x <= block.getRightEdge(); x++) {
				if (x < 0)
					continue;
				if (x >= gridColumns)
					break;
				background[y][x] = null;
			}
		}
	}

	// ------------------------------------------------------------------ ���, �� ����  
	
	// ���� ���� ��濡 �߰��Ѵ�.
	public void moveBlockToBackground() {
		int[][] shape = block.getShape();
		int h = block.getHeight();
		int w = block.getWidth();

		int xPos = block.getX();
		int yPos = block.getY();

		Color color = block.getColor();

		for (int r = 0; r < h; r++) {
			for (int c = 0; c < w; c++) {
				if (shape[r][c] == 1) { // �ش� ��ġ�� ���� ���� �����ϴ� �����̶��
					background[r + yPos][c + xPos] = color;
				}
			}
		}
		
		block = null; // �� �κ��� �� �ʿ��� �ɱ�! 
	}

	// �� �����ϸ鼭 �� ������ ����
	public int clearLines() {
		boolean lineFilled;
		int linesCleared = 0;

		for (int r = gridRows - 1; r >= 0; r--) {
			lineFilled = true;

			for (int c = 0; c < gridColumns; c++) {
				if (background[r][c] == null) {
					lineFilled = false;
					break;
				}
			}

			if (lineFilled) {
				twinkleCL(r);

				linesCleared++;

				clearLine(r);
				shiftDown(r);
				clearLine(0);

				r++; // ���� ������ �ٷ� �� ����� �ٽ� �˻� 

				repaint();
			}
		}

		return linesCleared;
	}

	private void twinkleCL(int r) {
		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.white;
			repaint();
		}
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.black;
			repaint();
		}
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// ��濡�� r���� �����.
	private void clearLine(int r) {
		for (int i = 0; i < gridColumns; i++) {
			background[r][i] = null;
		}
	}

	// r�� ���� ��� ���� ��ĭ�� �����ش�.
	private void shiftDown(int r) {
		for (int row = r; row > 0; row--) {
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 1][col];
			}
		}
	}

	// ------------------------------------------------------------- �׸��� 
	
	// ���� ���� �׷��ش�.
	private void drawBlock(Graphics g) {
		if (block == null)
			return;

		int h = block.getHeight();
		int w = block.getWidth();
		Color c = block.getColor();
		int[][] shape = block.getShape();

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {

				if (shape[row][col] == 1) { // colored cell
					int x = (block.getX() + col) * gridCellSize;
					int y = (block.getY() + row) * gridCellSize;

					if (this.block instanceof OneLineDelete) {
						drawGridL(g, c, x, y);
					}
					// ���� ���� �������̸� ������, �⺻ ���̸� �簢������ �׸���. 
					else if (curIsItem) {
						drawGridOval(g, c, x, y);
					} else {
						drawGridSquare(g, c, x, y);
					}
				}
			}
		}
	}

	// ����� �׷��ش�.
	private void drawBackground(Graphics g) {
		Color color;

		for (int r = 0; r < gridRows; r++) {
			for (int c = 0; c < gridColumns; c++) {
				// ��׶��� ��Ͽ� ���� ����
				color = background[r][c];

				// moveBlockToBackground �Լ����� �÷��� �����Ǹ� not null
				if (color != null) {
					int x = c * gridCellSize;
					int y = r * gridCellSize;

					drawGridSquare(g, color, x, y);
				}
			}
		}
	}

	private void drawGridSquare(Graphics g, Color c, int x, int y) {
		g.setColor(c);
		g.fillRect(x, y, gridCellSize, gridCellSize);
		g.setColor(Color.black);
		g.drawRect(x, y, gridCellSize, gridCellSize);
	}

	private void drawGridOval(Graphics g, Color color, int x, int y) {
		g.setColor(color);
		g.fillOval(x, y, gridCellSize, gridCellSize);
		g.setColor(Color.black);
		g.drawOval(x, y, gridCellSize, gridCellSize);
	}

	// ���� L�� ������ ���� �׷��ش�. 
	private void drawGridL(Graphics g, Color color, int x, int y) {
		String letterL = "L";
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.setColor(Color.black);
		g.drawString(letterL, x, y);

		g.setColor(color);
		g.fillOval(x, y, gridCellSize, gridCellSize);
		g.setColor(Color.black);
		g.drawOval(x, y, gridCellSize, gridCellSize);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawBackground(g);
		drawBlock(g);
	}
	
	// ------------------------------------------------------------------- getter
	public Color[][] getBackgroundArray() {
		return background;
	}

	public TetrisBlock[] getBlocks() {
		return blocks;
	}
	
	public TetrisBlock[] getItems() {
		return items;
	}
	
	public TetrisBlock getNextBlock() {
		return nextBlock;
	}

	public TetrisBlock getBlock() {
		return block;
	}
	
	public int getGridColumns() {
		return gridColumns;
	}

	public int getGridCellSize() {
		return gridCellSize; // NBA�� �� �׸� �� �ʿ� 
	}
}