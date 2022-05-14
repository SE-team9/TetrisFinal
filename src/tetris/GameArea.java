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
	private boolean isItem = false; // 현재 블럭이 아이템 블럭인지 확인하기 위한 변수
  
	//------------------------------ 대전모드용 변수들
	private int locate; 				// 오른쪽 플레이어의 game area를 locate만큼 오른쪽으로 이동시켜주기 위한 변수
	private Color[][] opponent_bg; 		// 상대의 배경
	private Color[][] pre_background; 	// 블럭이 배경으로 이동하기 전 배경
	private Color[] attackLine; 		// 공격에 사용될 줄
	private int attackLinesNum; 		// 공격한 줄 수

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
	
	// 대전모드
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

  // --------------------------------------------------------------------- 초기화관련동작
	// TODO: 프레임 크기에 따라 GameArea의 x, y 위치가 바뀌어야 함. 
	private void initThisPanel() {
		this.setBounds(gfW / 3, gfH / 60, gfW / 3, gfH - 60);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// 대전모드
	private void initThisPanel(int locate) {
		this.setBounds(gfW / 3 + locate, gfH / 60, gfW / 3, gfH - 60);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}

	// 배경초기화
	public void initBackgroundArray() {
		background = new Color[gridRows][gridColumns];
	}
	
	public Color[][] getBackgroundArray() {
		return background;
	}

	// 블럭초기화
	public void initBlocks() {
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), new ZShape(),
				new SShape() };
	}
	
	public TetrisBlock[] getBlocks() {
		return blocks;
	}

	// 아이템블럭초기화
	public void initItems() {
		items = new TetrisBlock[] { new FillEmpty(), new Weight(), new DeleteAroundU(), new TwoLineDelete(), new OneLineDelete()};
	}
	
	public TetrisBlock[] getItems() {
		return items;
	}

	// 격자 크기 반환
	public int getGridCellSize() {
		return gridCellSize;
	}
	
	public int getGridColumns() {
		return gridColumns;
	}
	
	// 현재 블럭이 아이템이면 변수값을 true로 설정 아니면 false로 설정
	public void setIsItem(boolean answer) {
		isItem = answer;
	}
	
	public void initGameArea() {
		initThisPanel();
		
		this.isItem = false;
		
		initBackgroundArray(); 	// 시작할 때마다 배경 초기화
		initBlocks(); 			// 다음 블럭 초기화
		updateNextBlock(); 		// 모든 아이템 초기화
		initItems(); 			// 모든 블럭 초기화
	}
	
	// 대전모드
	public void initGameArea_pvp() {
		this.attackLinesNum = 0;
		initThisPanel(locate);

		setIsItem(false);
		initBackgroundArray(); // 시작할 때마다 배경 초기화
		initBlocks(); // 다음 블럭 초기화
		updateNextBlock(); // 모든 아이템 초기화
		initItems(); // 모든 블럭 초기화
	}
	
	// --------------------------------------------------------------------- 난이도조절관련동작
	// 가중치 랜덤 함수 생성
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

	// level에 따른 가중치 부여
	public int makeRandom() {
		Map<String, Double> w = new HashMap<String, Double>();
		Random r = new Random();

		int level = Tetris.getGameLevel();
		double weight, iWeight;
		int blockNum; // 블럭 번호 (인덱스) 

		if (level == 0) { // easy
			weight = 14.0;
			iWeight = 16.0; // I형 블럭의 등장 확률 20% 증가
			w.put("0", iWeight);
			
			for (int i = 1; i < blocks.length; i++) {
				w.put(Integer.toString(i), weight);
			}
			
			blockNum = Integer.parseInt(getWeightedRandom(w, r));
			
		} else if (level == 2) { // hard 
			weight = 15.0;
			iWeight = 10.0; // I형 블럭의 등장 확률 20% 감소 
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

	// --------------------------------------------------------------------- 블럭생성관련동작
	// 난이도에 따라 다음 블럭을 정한다.
	public void updateNextBlock() {
		int r = makeRandom();
		nextBlock = blocks[r];
		nextBlock.setShape();
	}

	// 다음 블럭 아이템을 정한다.
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

	// 다음 블럭을 현재 블럭으로 받아서 스폰한다.
	public void spawnBlock() {
		block = nextBlock;
		block.spawn(gridColumns);
	}

	// --------------------------------------------------------------------- 블럭조작/경계확인 관련동작
	// 블럭이 위쪽 경계를 넘어갔는지 확인한다. (게임 종료 확인)
	public boolean isBlockOutOfBounds() {
		if (block.getY() < 0) {
			block = null;
			return true;
		}
		return false;
	}

	public boolean moveBlockDown() {
		// 현재 블럭에 의해 한 행의 높이가 다 채워진 경우 false 리턴 
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

		// 회전 시 경계를 넘어가지 않도록 위치 조정
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
				if (shape[row][col] != 0) {			// 해당 위치가 현재 블럭이 차지하는 공간이라면
					int x = col + block.getX();
					int y = row + block.getY() + 1; // 현재 블럭 위치의 한 칸 아래 확인

					if (y < 0)
						break; 						// 현재 블럭이 위쪽 경계를 넘었으면 확인 종료

					if (background[y][x] != null) { // 아래에 블럭이 존재하면
						return false; // stop
					}
					break; 							// 한 칸 아래만 확인하면 되므로 바로 다음 열을 확인한다.
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
					int x = col + block.getX() - 1; // 현재 블럭 위치의 한 칸 왼쪽 확인
					int y = row + block.getY();

					if (y < 0)
						break; 

					if (background[y][x] != null) { // 왼쪽에 블럭이 존재하면
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
					int x = col + block.getX() + 1; // 현재 블럭 위치의 한 칸 오른쪽 확인
					int y = row + block.getY();

					if (y < 0)
						break; 

					if (background[y][x] != null) { // 오른쪽에 블럭이 존재하면
						return false; // stop
					}
					break; 
				}
			}
		}
		return true; // keep going
	}

	// 현재 블럭을 회전시켰을 때의 객체를 생성하여 경계를 넘는지 확인한다. 
	// 블럭이 완전히 바닥에 닿음과 동시에 회전시키면 배경과 현재 블럭이 겹치는 등 기능이 완전하지 않으므로 수정 필요 
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

	// --------------------------------------------------------------------- 아이템관련동작
	// 빈칸을 메워준다.
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
  
	//한 줄을 삭제한다.
	public void oneLineDelte() {
		int yPos = block.getY();

		clearLine(yPos);
		shiftDown(yPos);
		repaint();
	}
  
	// 두 줄을 삭제한다.
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
	
	// 무게추 아이템 기능을 수행한다.
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
		
		// 아이템의 좌우아래를 삭제한다.
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

	// 현재 블럭 아이템의 기능을 수행한다.
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

	// 아이템을 반짝거린다.
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

	// --------------------------------------------------------------------- 배경관련동작
	// 현재 블럭을 배경에 추가한다.
	public void moveBlockToBackground() {
		
		int[][] shape = block.getShape();
		int h = block.getHeight();
		int w = block.getWidth();

		int xPos = block.getX();
		int yPos = block.getY();

		Color color = block.getColor();

		for (int r = 0; r < h; r++) {
			for (int c = 0; c < w; c++) {
				if (shape[r][c] == 1) {		// 해당 위치가 현재 블럭이 차지하는 공간이라면
					background[r + yPos][c + xPos] = color;
				}
			}
		}
		block = null;
	}


	// 삭제된 줄 수 리턴 
	public int clearLines() {

		boolean lineFilled;
		int linesCleared = 0;

		// 맨 아래 줄부터
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

				// 아래로 한 줄 씩 내려왔으므로 지워진 줄 위치에서부터 다시 시작한다.
				r++;
				repaint();
			}
		}
		return linesCleared;
	}
	
	// 대전모드
	public int clearLines_pvp() {
		boolean lineFilled; // 한 줄이 채워졌는지 확인
		int clearedLineNum = 0; // 삭제된 줄의 수

		int num = 0;

		// 맨 아래 줄부터
		for (int r = gridRows - 1; r >= 0; r--) {

			lineFilled = true; // 행마다 상태가 업데이트 되는 변수

			for (int c = 0; c < gridColumns; c++) {
				if (background[r][c] == null) {
					lineFilled = false;
					break;
				}
			}

			if (lineFilled) {
				clearedLineNum++;
				makeAttackLine(r-num); // 지워지는 줄이 있던 위치의 줄을 저장 (블럭이 배경으로 옮겨지기 전의 배경에서 가져온다.)
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
	
	// 삭제된 행 검정색으로 깜빡이기
	public void animateLineClear(int r) {
		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.white; // 흰색
			repaint();
		}
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int c = 0; c < gridColumns; c++) {
			background[r][c] = Color.black; // 검정색
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
		clearLine(0); // 첫번째 행이 null이 되므로 따로 지워주기

		repaint();
	}

	// 배경에서 r행 줄을 지운다.
	private void clearLine(int r) {
		for (int i = 0; i < gridColumns; i++) {
			background[r][i] = null;
		}
	}

	// r행 위의 모든 줄을 한칸씩 내려준다.
	private void shiftDown(int r) {
		for (int row = r; row > 0; row--) {
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 1][col];
			}
		}
	}
	
	// --------------------------------------------------------------------- 대전모드를 위한 동작
	
	// 상대의 배경을 인자로 받아서 변수로 저장한다.
	public void setOpponent_bg(Color[][] oppbg) {
		this.opponent_bg = oppbg;
	}
	
	// 블럭을 배경으로 옮기기 전의 배경상태를 저장해 두기 위해 사용한다.
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
	
	// 블럭이 배경으로 옮겨지기 전의 배경에서 공격에 사용될 한 줄을 저장한다. 
	public void makeAttackLine(int r) {
		attackLine = pre_background[r];
	}
	
	// 상대 배경의 아래 10줄 중 gray 블럭이 포함된 줄의 수를 세서 공격한 줄 수를 구한다. 
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
	
	// 상대 배경 아래에 공격 줄을 추가하기전에 상대의 배경을 한 칸 씩 위로 올려준다.
	public void shiftUp_oppBg() {
		for (int row = 0; row < gridRows - 1; row++) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[row][col] = opponent_bg[row + 1][col];
			}
		}
		
		// 맨 아래 줄은 null로 채워준다.
		for (int col = 0; col < gridColumns; col++) {
			opponent_bg[gridRows - 1][col] = null;
		}
	}
	
	// 공격할 줄에서 채워진 부분만 gray로 상대의 배경에 채워넣는다.
	public void attack() {
		for (int col = 0; col < gridColumns; col++) {
			if (attackLine[col] != null) {
				opponent_bg[gridRows - 1][col] = Color.gray;
			}
		}
	}

	// --------------------------------------------------------------------- 그리기
	// 현재 블럭을 그려준다.
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
          			// 현재 블럭이 아이템블럭이면 원으로, 기본블럭이면 사각형으로 그려준다.
					else if (isItem) {
						drawGridOval(g, c, x, y);
					} else {
						drawGridSquare(g, c, x, y);
					}
				}
			}
		}
	}

	// 배경을 그려준다.
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
	
	// 문자 L을 포함한 블럭을 그려준다.
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