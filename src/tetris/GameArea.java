package tetris;

import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tetrisblocks.IShape;
import tetrisblocks.JShape;
import tetrisblocks.LShape;
import tetrisblocks.OShape;
import tetrisblocks.SShape;
import tetrisblocks.ZShape;
import tetrisitems.DeleteAroundU;
import tetrisitems.DeleteAllBlock;
import tetrisitems.DeleteOneLine;
import tetrisitems.DeleteTwoLine;
import tetrisitems.Weight;

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
	private int gridRows;
	private int gridColumns;
	private int gridCellSize;
	private boolean curIsItem; // ���� ���� ������ ������ Ȯ���ϱ� ���� ���� 
	
	private Color[][] background;
	private TetrisBlock[] blocks;
	private TetrisBlock[] items;
	private TetrisBlock block;
	private TetrisBlock nextBlock;
	
	// �������� ������			
	private Color[][] opponent_bg; 		// ����� ���
	private Color[][] pre_background; 	// ���� ������� �̵��ϱ� �� ���
	private Color[][] attackLines; 		// ���ݿ� ���� �ٵ�
	private int attackLinesNum; 		// ������ �� ��
	private int grayLinesNum; 			// �̹� ������ �� ��

	// 2���� �迭 ���� ��, Į�� ũ�� ������ �ʼ� (�׷��� ���ڷ� ���� ����)
	public GameArea(int gfW, int gfH, int columns) {
		// GameForm ũ�⿡ ���� GameArea�� (x, y) ��ġ ���� 
		initThisPanel(gfW, gfH);
		initBlocks();
				
		// 20�� 10��������, ������ ���� ���� ũ�Ⱑ �ٲ�. 
		gridColumns = columns; // 10��
		gridCellSize = this.getBounds().width / gridColumns; // 20 -> 25 -> 30 
		gridRows = this.getBounds().height / gridCellSize; // 20�� 
		
		// ���� ������ ������ ������ ������Ʈ ����� ��. 
		curIsItem = false;
		updateNextBlock();
	}
	
	// ���� ��� 
	public GameArea(int gfW, int gfH, int columns, int xGap) {
		initThisPanel(gfW, gfH, xGap);
		initBlocks();
		
		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
		
		curIsItem = false;
		updateNextBlock();
	}

	// --------------------------------------------------------------- �ʱ�ȭ 

	private void initThisPanel(int gfW, int gfH) {
		this.setBounds(gfW / 3, gfH / 60, gfW / 3, gfH - 60); // GameArea ũ�� ����    
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// ���� ��� 
	private void initThisPanel(int gfW, int gfH, int xGap) {
		this.setBounds(gfW / 3 + xGap, gfH / 60, gfW / 3, gfH - 60);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}

	public void initBackgroundArray() {
		background = new Color[gridRows][gridColumns];
	}

	public void initBlocks() {
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), new ZShape(),
				new SShape() };
		
		items = new TetrisBlock[] { new DeleteOneLine(), new DeleteTwoLine(), new Weight(), new DeleteAroundU(), 
				new DeleteAllBlock() };
	}
	
	// ���� ������ ������ ������ ���� ������Ʈ 
	public void initGameArea() {
		curIsItem = false;
		
		initBackgroundArray(); // ������ ������ ��� �ʱ�ȭ
		updateNextBlock();
	}
	
	// �������
	public void initGameArea_pvp() {
		curIsItem = false;
		grayLinesNum = 0;
		attackLines = new Color[10][gridColumns];
		
		initBackgroundArray(); 
		updateNextBlock(); 
	}
	
	public void setIsItem(boolean flag) {
		curIsItem = flag;
	}

	// --------------------------------------------------------------- �� ����
	
	// ���� �� �������� 
	public void updateNextBlock() {
		// ������ ���� i�� ���� ���� Ȯ�� ���� 
		int level = Tetris.getGameLevel();
		int r = makeRandom(level); 
		
		nextBlock = blocks[r];
		nextBlock.setShape();
	}

	// ���� ������ �������� 
	public void updateNextItem() {
		//Random r = new Random();
		//nextBlock = items[r.nextInt(items.length)];
		
		nextBlock = items[4]; // ��� �� ���� 
		
		nextBlock.setShape(); // ���⼭ �� ������ �������� ������. 
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

	public boolean checkBottom() {
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
		if(this.block instanceof DeleteOneLine) {
			deleteOneLine();
			
		}else if(this.block instanceof DeleteTwoLine) {
			deleteTwoLine(); 
			
		}else if(this.block instanceof Weight) {
			deleteWeight();
			
		}else if(this.block instanceof DeleteAroundU) {
			deleteAroundU();
			
		}else if(this.block instanceof DeleteAllBlock) {
			deleteAllBlock();
		}
	}

	// �� �� ���� ������ (�� ȸ���� ���� ��� �޶���)
	public void deleteOneLine() {
		// ��� üũ�� �տ��� �̹� �� ���� 
		int yPos = block.getY();
		
		clearLine(yPos); 
		shiftDown(yPos);
		clearLine(0);    

		repaint();
	}

	// �� �� ���� ������ (ȸ���ص� ��� �״��) 
	public void deleteTwoLine() {
		int yPos = block.getY();
		
		// �� �� ���� 
		for (int i = 0; i < gridColumns; i++) {
			background[yPos][i] = null;
			background[yPos + 1][i] = null;
		}
		
		// ��ĭ�� ������ 
		for (int row = yPos; row >= 2; row--) { // 0, 1���� ���� 
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 2][col];
			}
		}
		
		// ���� ���� 2�� ���� 
		for (int i = 0; i < gridColumns; i++) {
			background[0][i] = null;
			background[1][i] = null;
		}
		
		repaint();
	}

	// ������ ������ (ȸ���ص� ��� �״��) 
	public void deleteWeight() {
		// ������ ��ĭ�� �������鼭 
		for (int row = block.getBottomEdge(); row < gridRows; row++) {
			// ���� �ʺ�ŭ ���� 
			for (int col = block.getLeftEdge(); col < block.getRightEdge(); col++) {
				background[row][col] = null;
			}
			
			block.moveDown();
			repaint();
		}
	}

	// �¿�Ʒ��� ��� �����ϴ� �ٱ��� ������ (ȸ���ص� ��� �״��)
	public void deleteAroundU() {
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
		
		repaint();
	}
	
	// ��� ���� �����ϴ� ������ 
	public void deleteAllBlock() {
		for(int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridColumns; j++) {
				if(background[i][j] != null) {
					background[i][j] = null;
				}
			}
		}
		
		repaint();
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
			lineFilled = true; // �ึ�� ���� ������Ʈ

			for (int c = 0; c < gridColumns; c++) {
				if (background[r][c] == null) {
					lineFilled = false;
					break;
				}
			}

			if (lineFilled) {
				linesCleared++;
				
				twinkleCL(r);
				updateGameArea(r);

				r++; // ���� ������ �ٷ� �� ����� �ٽ� �˻�
			}
		}

		return linesCleared;
	}
	
	// �������
	public int clearLines_pvp() {
		boolean lineFilled;
		int linesCleared = 0;
		int num = 0;

		for (int r = gridRows - 1; r >= 0; r--) {
			lineFilled = true;  
			
			for (int c = 0; c < gridColumns; c++) {
				if (background[r][c] == null) {
					lineFilled = false;
					break;
				}
			}

			if (lineFilled) {
				linesCleared++;
				twinkleCL(r);
				updateGameArea(r);
				
				// �̹� ������ �� ���� ����. 
				setGrayLinesNum();
				if (grayLinesNum + attackLinesNum < 10) {
					
					// ���ݿ� ���� �� ����! 
					makeAttackLine(r - num); 
				}
				r++;
				num++;
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
	
	public void updateGameArea(int r) {
		clearLine(r); // r�� ���� 
		shiftDown(r); // 0�� �����ϰ� ��ĭ�� ������ 
		clearLine(0); // 0�� ���� 

		repaint();
	}

	// ��濡�� r���� �����.
	private void clearLine(int r) {
		for (int i = 0; i < gridColumns; i++) {
			background[r][i] = null;
		}
	}

	// r�� ���� ��� ���� ��ĭ�� �����ش�.
	private void shiftDown(int r) {
		for (int row = r; row > 0; row--) { // 0���� ���� 
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 1][col];
			}
		}
	}
  
  	// -------------------------------------------------------------------------------- ������带 ���� ����
	
	// ����� ����� ���ڷ� �޾Ƽ� ������ �����Ѵ�.
	public void setOpponent_bg(Color[][] oppbg) {
		this.opponent_bg = oppbg;
	}
	
	// ���� ������� �ű�� ���� �����¸� ������ �α� ���� ����Ѵ�.
	public void saveBackground() {
		
		pre_background = new Color[gridRows][gridColumns];
		Color[][] cur_background = getBackgroundArray();


		for (int row = 0; row < gridRows; row++) {
			for (int col = 0; col < gridColumns; col++) {
				pre_background[row][col] = cur_background[row][col];
			}
		}
	}
	
	// ���� ������� �Ű����� ���� ��濡�� ���ݿ� ���� ���� �����Ѵ�.
	public void makeAttackLine(int r) {
		attackLines[attackLinesNum] = pre_background[r];
		attackLinesNum++;
	}

	// ��� ����� �Ʒ� 10�� �� gray ���� ���Ե� ���� ���� ���� ������ �� ���� ���Ѵ�. 
	public void setGrayLinesNum() {
		grayLinesNum = 0;
		
		for (int row = 10; row < 20; row++) {
			for (int col = 0; col < gridColumns; col++) {
				if (opponent_bg[row][col] == Color.gray) {
					grayLinesNum++;
					break;
				}
			}
		}
	}
	
	// ��� ��� �Ʒ��� ���� ���� �߰��ϱ� ���� ����� ����� ���� �÷��ش�.
	public void shiftUp_oppBg() {
		
		for (int row = 0; row < gridRows - attackLinesNum; row++) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[row][col] = opponent_bg[row + attackLinesNum][col];
			}
		}

		// �Ʒ� �ٵ��� null�� ä���ش�.
		for (int r = attackLinesNum; r > 0; r-- ) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[gridRows - r][col] = null;
			}
		}
	}

	// ������ �ٿ��� ä���� �κи� gray�� ����� ��濡 ä���ִ´�.
	public void attack() {
		
		// �� �� �̻��� �����Ǿ��� ����
		if(attackLinesNum<2) {
			attackLinesNum = 0;
			return;
		}
		
		shiftUp_oppBg();
		
		for (int row = attackLinesNum; row > 0; row--) {
			System.out.print(attackLinesNum + "\n");
			for (int col = 0; col < gridColumns; col++) {
				if (attackLines[row - 1][col] != null) {
					opponent_bg[gridRows - row][col] = Color.gray;
				}
			}
		}

		attackLinesNum = 0;
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

					if (this.block instanceof DeleteOneLine) {
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