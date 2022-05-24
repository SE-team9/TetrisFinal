package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

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
	private int gridRows;
	private int gridColumns;
	private int gridCellSize;
	
	private Color[][] background;
	private TetrisBlock block; // ���� �� 
	private boolean isItem;
	
	private ArrayList<Pair> coloredCells = new ArrayList<>(); // colored cell 4���� ��ǥ ���� 
	private Pair Lpos; // L������ ���� ��ǥ 
	private int randIdx; // L���ڰ� �������� �ٴ� ��ġ 
	private int blockIdx; // ���� ���� ������ ������ ���� �ε��� 
	private boolean OLCflag; // ���� ���� �����ۿ� ���� ���� �ο� 
	
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
				
		// 20�� 10��������, ������ ���� ���� ũ�Ⱑ �ٲ�. 
		gridColumns = columns; // 10��
		gridCellSize = this.getBounds().width / gridColumns; // 20 24 28
		gridRows = this.getBounds().height / gridCellSize; // 20�� 
		
		// ���� ������ ������ ������ ���� ������Ʈ 
		isItem = false;
	}
	
	// ���� ��� 
	public GameArea(int gfW, int gfH, int columns, int xGap) {
		initThisPanel(gfW, gfH, xGap);
		
		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
		
		isItem = false;
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
	
	// ���� ������ ������ ������ ���� ������Ʈ 
	public void initGameArea() {
		isItem = false;
		initBackgroundArray();
	}
	
	// �������
	public void initGameArea_pvp() {
		isItem = false;
		grayLinesNum = 0;
		attackLines = new Color[10][gridColumns];
		initBackgroundArray();
	}
	
	public void setIsItem(boolean b) {
		isItem = b;
	}

	// --------------------------------------------------------------- �� ����

	// �� �� ���� 
	public void spawnBlock(TetrisBlock nextBlock) {
		// ���� ���� ����� �״�� �����´�. 
		block = nextBlock;
		
		// ������ ��ġ���� ����������
		block.spawn(gridColumns);
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
		switch(blockIdx) {
		case 7:
			clearTwoLine(); 
			break;
		case 8:
			deleteWeight();
			break;
		case 9:
			deleteAroundU();
			break;
		case 10:
			clearAll();
			break;
		case 11:
			clearOneLine();
			break;
		}
	}

	// �� �� ���� ������ 
	public void clearOneLine() {
		int row = Lpos.y / gridCellSize;
		
		clearLine(row); 
		shiftDown(row); 
		clearLine(0);    
		
		// �� �� ������ ���� ���� �ο��� ���� �÷��� ���� 
		OLCflag = true;

		repaint();
	}

	// �� �� ���� ������ (ȸ���ص� ��� �״��) 
	public void clearTwoLine() {
		int y = block.getY();
		clearLine(y);
		shiftDown(y);
		
		y++;
		clearLine(y);
		shiftDown(y);
		
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
	public void clearAll() {
		for(int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridColumns; j++) {
				if(background[i][j] != null) {
					background[i][j] = null;
				}
			}
		}
		
		repaint();
	}

	// ------------------------------------------------------------------ �� ����, ������� ��ȯ   
	
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
	// ���⼭ NPE �߻��� ----------------------------------------------
	public void makeAttackLine(int r) {
		attackLines[attackLinesNum] = pre_background[r];
		attackLinesNum++;
	}
	// ----------------------------------------------------------------

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
		if(attackLinesNum < 2) {
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
	
	public void setBlockIndex(int index) {
		this.blockIdx = index;
	}
	
	class Pair{
		int x, y;
		Pair(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	// ���� ���� �׷��ش�.
	private void drawBlock(Graphics g) {
		if (block == null)
			return;
		
		if(isItem && blockIdx == 11) {
			drawRandomL(g);
		}else {
			int h = block.getHeight();
			int w = block.getWidth();
			Color c = block.getColor();
			int[][] shape = block.getShape();
			
			for (int row = 0; row < h; row++) {
				for (int col = 0; col < w; col++) { 
					if (shape[row][col] == 1) { 
						int x = (block.getX() + col) * gridCellSize;
						int y = (block.getY() + row) * gridCellSize;
						
						if(isItem) {
							drawGridCircle(g, c, x, y); 
						}
						else {
							drawGridSquare(g, c, x, y);
						}
					}
				}
			}
		}
		
		// �����忡 ���� ��ĭ�� ������ ������ �� �ٽ� �׸��� 
		repaint();
	}

	// repaint �� ������ �ٲ�� �� ��ǥ�� ���� �׸��� �ٽ� �׸���. 
	private void drawRandomL(Graphics g) {
		int h = block.getHeight();
		int w = block.getWidth();
		Color c = block.getColor();
		int[][] shape = block.getShape();
		
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (shape[row][col] == 1) { 
					int x = (block.getX() + col) * gridCellSize;
					int y = (block.getY() + row) * gridCellSize;
					
					coloredCells.add(new Pair(x, y));
				}
			}
		}
		
		// ó�� ���� ������ �� ������ ���� ��ġ�� L���ڸ� �׸���.
		Lpos = coloredCells.get(randIdx);
		
		g.setFont(new Font("TimesRoman", Font.BOLD, 25));
	    g.setColor(Color.black);	    
		g.drawString("L", Lpos.x, Lpos.y + gridCellSize);
		
		// �ٸ� ������ �簢�� �׸��� 
		for(int i = 0; i < 4; i++) {
			if(i != randIdx) {
				Pair p = coloredCells.get(i);
				drawGridSquare(g, c, p.x, p.y);
			}
		}
		
		coloredCells.clear(); // �� ���� repaint�� ���� ��ǥ ������ ���� Ŭ����!!
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

	private void drawGridCircle(Graphics g, Color color, int x, int y) {
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

	// ------------------------------------------------------------------- �׽�Ʈ �ڵ忡 ���̴� getter
	public Color[][] getBackgroundArray() {
		return background;
	}
	
	public TetrisBlock getBlock() {
		return block;
	}
	
	public int getGridColumns() {
		return gridColumns;
	}

	public void setRandomIndex(int index) {
		this.randIdx = index;
	}
	
	public void setOLCflag(boolean b) {
		this.OLCflag = b;
	}
	
	public boolean getOLCflag() {
		return OLCflag;
	}

//	public int getGridCellSize() {
//		return gridCellSize; // NBA�� �� �׸� �� �ʿ� 
//	}
}