package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tetrisblocks.*;
import tetrisitems.*;

public class GameArea extends JPanel {
	private static int gfW, gfH;
	private int gridRows;
	private int gridColumns;
	private int gridCellSize;
	private Color[][] background;
	private TetrisBlock[] blocks;
	private TetrisBlock block;
	private TetrisBlock nextBlock;
	private TetrisBlock[] items;
	private boolean isItem = false; // ���� ���� ������ ������ Ȯ���ϱ� ���� ����
  
	//------------------------------ �������� ������
	private int locate; 				// ������ �÷��̾��� game area�� locate��ŭ ���������� �̵������ֱ� ���� ����
	private Color[][] opponent_bg; 		// ����� ���
	private Color[][] pre_background; 	// ���� ������� �̵��ϱ� �� ���
	private Color[] attackLine; 		// ���ݿ� ���� ��
	private int attackLinesNum; 		// ������ �� ��

	public GameArea(int w, int h, int columns) {
		this.gfW = w;
		this.gfH = h;
		
		initThisPanel();
		initBlocks();
		initItems();
		updateNextBlock();

		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
	}
	
	// �������
	public GameArea(int w, int h, int columns, int locate) {
		this.gfW = w;
		this.gfH = h;
		this.locate = locate;

		initThisPanel(locate);
		initBlocks();
		initItems();
		updateNextBlock();
		
		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
	}

  // --------------------------------------------------------------------- �ʱ�ȭ���õ���
	// TODO: ������ ũ�⿡ ���� GameArea�� x, y ��ġ�� �ٲ��� ��. 
	private void initThisPanel() {
		this.setBounds(gfW / 3, gfH / 60, gfW / 3, gfH - 60);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// �������
	private void initThisPanel(int locate) {
		this.setBounds(gfW / 3 + locate, gfH / 60, gfW / 3, gfH - 60);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}

	// ����ʱ�ȭ
	public void initBackgroundArray() {
		background = new Color[gridRows][gridColumns];
	}
	
	public Color[][] getBackgroundArray() {
		return background;
	}

	// ���ʱ�ȭ
	public void initBlocks() {
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), new ZShape(),
				new SShape() };
	}
	
	public TetrisBlock[] getBlocks() {
		return blocks;
	}

	// �����ۺ��ʱ�ȭ
	public void initItems() {
		items = new TetrisBlock[] { new FillEmpty(), new Weight(), new DeleteAroundU(), new TwoLineDelete(), new OneLineDelete()};
	}
	
	public TetrisBlock[] getItems() {
		return items;
	}

	// ���� ũ�� ��ȯ
	public int getGridCellSize() {
		return gridCellSize;
	}
	
	public int getGridColumns() {
		return gridColumns;
	}
	
	// ���� ���� �������̸� �������� true�� ���� �ƴϸ� false�� ����
	public void setIsItem(boolean answer) {
		isItem = answer;
	}
	
	public void initGameArea() {
		initThisPanel();
		
		this.isItem = false;
		
		initBackgroundArray(); 	// ������ ������ ��� �ʱ�ȭ
		initBlocks(); 			// ���� �� �ʱ�ȭ
		updateNextBlock(); 		// ��� ������ �ʱ�ȭ
		initItems(); 			// ��� �� �ʱ�ȭ
	}
	
	// �������
	public void initGameArea_pvp() {
		this.attackLinesNum = 0;
		initThisPanel(locate);

		setIsItem(false);
		initBackgroundArray(); // ������ ������ ��� �ʱ�ȭ
		initBlocks(); // ���� �� �ʱ�ȭ
		updateNextBlock(); // ��� ������ �ʱ�ȭ
		initItems(); // ��� �� �ʱ�ȭ
	}
	
	// --------------------------------------------------------------------- ���̵��������õ���
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

	// level�� ���� ����ġ �ο�
	public int makeRandom() {
		Map<String, Double> w = new HashMap<String, Double>();
		Random r = new Random();

		int level = Tetris.getGameLevel();
		double weight, iWeight;
		int blockNum; // �� ��ȣ (�ε���) 

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
			
		} else {
			blockNum = r.nextInt(blocks.length);
		}
		
		return blockNum;
	}

	// --------------------------------------------------------------------- ���������õ���
	// ���̵��� ���� ���� ���� ���Ѵ�.
	public void updateNextBlock() {
		int r = makeRandom();
		nextBlock = blocks[r];
		nextBlock.setShape();
	}

	// ���� �� �������� ���Ѵ�.
	public void updateNextItem() {
		Random r = new Random();
		nextBlock = items[r.nextInt(items.length)];
		nextBlock.setShape();
	}

	public TetrisBlock getNextBlock() {
		return nextBlock;
	}
	
	public TetrisBlock getBlock() {
		return block;
	}

	// ���� ���� ���� ������ �޾Ƽ� �����Ѵ�.
	public void spawnBlock() {
		block = nextBlock;
		block.spawn(gridColumns);
	}

	// --------------------------------------------------------------------- ������/���Ȯ�� ���õ���
	// ���� ���� ��踦 �Ѿ���� Ȯ���Ѵ�. (���� ���� Ȯ��)
	public boolean isBlockOutOfBounds() {
		if (block.getY() < 0) {
			block = null;
			return true;
		}
		return false;
	}

	public boolean moveBlockDown() {
		// ���� ���� ���� �� ���� ���̰� �� ä���� ��� false ���� 
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
		if (block.getBottomEdge() == gridRows) {
			return false; // stop
		}

		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();

		for (int col = 0; col < w; col++) {
			for (int row = h - 1; row >= 0; row--) {
				if (shape[row][col] != 0) {			// �ش� ��ġ�� ���� ���� �����ϴ� �����̶��
					int x = col + block.getX();
					int y = row + block.getY() + 1; // ���� �� ��ġ�� �� ĭ �Ʒ� Ȯ��

					if (y < 0)
						break; 						// ���� ���� ���� ��踦 �Ѿ����� Ȯ�� ����

					if (background[y][x] != null) { // �Ʒ��� ���� �����ϸ�
						return false; // stop
					}
					break; 							// �� ĭ �Ʒ��� Ȯ���ϸ� �ǹǷ� �ٷ� ���� ���� Ȯ���Ѵ�.
				}
			}
		}
		return true; // keep going
	}

	public boolean checkLeft() {
		if (block.getLeftEdge() == 0) {
			return false; // stop
		}

		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (shape[row][col] != 0) { 
					int x = col + block.getX() - 1; // ���� �� ��ġ�� �� ĭ ���� Ȯ��
					int y = row + block.getY();

					if (y < 0)
						break; 

					if (background[y][x] != null) { // ���ʿ� ���� �����ϸ�
						return false; // stop
					}
					break; 
				}
			}
		}
		return true; // keep going
	}

	public boolean checkRight() {
		if (block.getRightEdge() == gridColumns) {
			return false; // stop
		}

		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();

		for (int row = 0; row < h; row++) {
			for (int col = w - 1; col >= 0; col--) {
				if (shape[row][col] != 0) {
					int x = col + block.getX() + 1; // ���� �� ��ġ�� �� ĭ ������ Ȯ��
					int y = row + block.getY();

					if (y < 0)
						break; 

					if (background[y][x] != null) { // �����ʿ� ���� �����ϸ�
						return false; // stop
					}
					break; 
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

	// --------------------------------------------------------------------- �����۰��õ���
	// ��ĭ�� �޿��ش�.
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
  
	//�� ���� �����Ѵ�.
	public void oneLineDelte() {
		int yPos = block.getY();

		clearLine(yPos);
		shiftDown(yPos);
		repaint();
	}
  
	// �� ���� �����Ѵ�.
	public void twoLineDelete() {
		int yPos = block.getY();
		int time = 0;

		for (int r = yPos + 2; r >= yPos+1 && time < 2; r--) {
			clearLine(r);
			shiftDown(r);
			r++;
			time++;

			repaint();
		}
	}
	
	// ������ ������ ����� �����Ѵ�.
		public void Weight() {
			for(int row = block.getBottomEdge(); row < gridRows; row++) {
				for(int col = block.getLeftEdge(); col < block.getRightEdge(); col++) {
					background[row][col] = null;
				}
				block.moveDown();
				repaint();
			}
			moveBlockToBackground();
		}
		
		// �������� �¿�Ʒ��� �����Ѵ�.
		public void DeleteAroundU() {
			/*int leftX = block.getX() - 1;
			int leftY = block.getY() - 1;
			int rightX = block.getRightEdge();
			int rightY = block.getY() - 1;*/
			for(int y = block.getY(); y <= block.getBottomEdge(); y++) {
				if(y >= gridRows) break;
				for(int x = block.getX() - 1; x <= block.getRightEdge(); x++) {
					if(x < 0) continue;
					if(x >= gridColumns) break;
					background[y][x] = null;
				}
			}
			
			/*if(leftX >= 0 && background[leftY][leftX] != null) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int xPos = leftX;
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
			if(rightX < gridColumns && background[rightY][rightX] != null) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int xPos = rightX;
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
			}*/
		}

	// ���� �� �������� ����� �����Ѵ�.
	public void itemFunction() {

		if (this.block instanceof FillEmpty) {
			fillEmpty();
		} else if(this.block instanceof TwoLineDelete) {
			twoLineDelete();
		}
		else if(this.block instanceof Weight) {
			Weight();
		}
		else if(this.block instanceof DeleteAroundU) {
			DeleteAroundU();
		}
		else if(this.block instanceof OneLineDelete) {
			oneLineDelte();
		}
	}

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

	// --------------------------------------------------------------------- �����õ���
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
				if (shape[r][c] == 1) {		// �ش� ��ġ�� ���� ���� �����ϴ� �����̶��
					background[r + yPos][c + xPos] = color;
				}
			}
		}
		block = null;
	}


	// ������ �� �� ���� 
	public int clearLines() {

		boolean lineFilled;
		int linesCleared = 0;

		// �� �Ʒ� �ٺ���
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
				animateLineClear(r);
				updateGameArea(r);

				// �Ʒ��� �� �� �� ���������Ƿ� ������ �� ��ġ�������� �ٽ� �����Ѵ�.
				r++;
				repaint();
			}
		}
		return linesCleared;
	}
	
	// �������
	public int clearLines_pvp() {
		boolean lineFilled; // �� ���� ä�������� Ȯ��
		int clearedLineNum = 0; // ������ ���� ��

		int num = 0;

		// �� �Ʒ� �ٺ���
		for (int r = gridRows - 1; r >= 0; r--) {

			lineFilled = true; // �ึ�� ���°� ������Ʈ �Ǵ� ����

			for (int c = 0; c < gridColumns; c++) {
				if (background[r][c] == null) {
					lineFilled = false;
					break;
				}
			}

			if (lineFilled) {
				clearedLineNum++;
				makeAttackLine(r-num); // �������� ���� �ִ� ��ġ�� ���� ���� (���� ������� �Ű����� ���� ��濡�� �����´�.)
				animateLineClear(r);
				updateGameArea(r);

				setAttackLinesNum();

				if (attackLinesNum < 10) {
					shiftUp_oppBg();
					attack();
				}
				r++;
				num++;
			}
		}
		return clearedLineNum;
	}
	
	// ������ �� ���������� �����̱�
	public void animateLineClear(int r) {
		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.white; // ���
			repaint();
		}
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.black; // ������
			repaint();
		}

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateGameArea(int r) {
		clearLine(r);
		shiftDown(r);
		clearLine(0); // ù��° ���� null�� �ǹǷ� ���� �����ֱ�

		repaint();
	}

	// ��濡�� r�� ���� �����.
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
	
	// --------------------------------------------------------------------- ������带 ���� ����
	
	// ����� ����� ���ڷ� �޾Ƽ� ������ �����Ѵ�.
	public void setOpponent_bg(Color[][] oppbg) {
		this.opponent_bg = oppbg;
	}
	
	// ���� ������� �ű�� ���� �����¸� ������ �α� ���� ����Ѵ�.
	public void saveBackground() {
		int r = gridRows;
		int c = gridColumns;
		Color[][] cur_background = getBackgroundArray();

		pre_background = new Color[r][c];

		for (int row = 0; row < r; row++) {
			for (int col = 0; col < c; col++) {
				pre_background[row][col] = cur_background[row][col];
			}
		}
	}
	
	// ���� ������� �Ű����� ���� ��濡�� ���ݿ� ���� �� ���� �����Ѵ�. 
	public void makeAttackLine(int r) {
		attackLine = pre_background[r];
	}
	
	// ��� ����� �Ʒ� 10�� �� gray ���� ���Ե� ���� ���� ���� ������ �� ���� ���Ѵ�. 
	public void setAttackLinesNum() {
		attackLinesNum = 0;
		for (int row = 10; row < 20; row++) {
			for (int col = 0; col < gridColumns; col++) {
				if (opponent_bg[row][col] == Color.gray) {
					attackLinesNum++;
					break;
				}
			}
		}
	}
	
	// ��� ��� �Ʒ��� ���� ���� �߰��ϱ����� ����� ����� �� ĭ �� ���� �÷��ش�.
	public void shiftUp_oppBg() {
		for (int row = 0; row < gridRows - 1; row++) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[row][col] = opponent_bg[row + 1][col];
			}
		}
		
		// �� �Ʒ� ���� null�� ä���ش�.
		for (int col = 0; col < gridColumns; col++) {
			opponent_bg[gridRows - 1][col] = null;
		}
	}
	
	// ������ �ٿ��� ä���� �κи� gray�� ����� ��濡 ä���ִ´�.
	public void attack() {
		for (int col = 0; col < gridColumns; col++) {
			if (attackLine[col] != null) {
				opponent_bg[gridRows - 1][col] = Color.gray;
			}
		}
	}

	// --------------------------------------------------------------------- �׸���
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

          if(this.block instanceof OneLineDelete) {
						drawGridL(g, c, x, y);
					}
          			// ���� ���� �����ۺ��̸� ������, �⺻���̸� �簢������ �׷��ش�.
					else if (isItem) {
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
				color = background[r][c];

				
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
}