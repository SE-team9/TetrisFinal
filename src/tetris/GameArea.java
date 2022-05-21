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
 *  초기화 
 *  난이도 조절 
 *  블럭 생성
 *  블럭 조작, 경계 확인 
 *  아이템 
 *  배경, 줄 삭제 
 *  그리기 
 *  getter
 */

public class GameArea extends JPanel { 
	private int gridRows;
	private int gridColumns;
	private int gridCellSize;
	private boolean curIsItem; // 현재 블럭이 아이템 블럭인지 확인하기 위한 변수 
	
	private Color[][] background;
	private TetrisBlock[] blocks;
	private TetrisBlock[] items;
	private TetrisBlock block;
	private TetrisBlock nextBlock;
	
	// 대전모드용 변수들			
	private Color[][] opponent_bg; 		// 상대의 배경
	private Color[][] pre_background; 	// 블럭이 배경으로 이동하기 전 배경
	private Color[][] attackLines; 		// 공격에 사용될 줄들
	private int attackLinesNum; 		// 공격한 줄 수
	private int grayLinesNum; 			// 이미 공격한 줄 수

	// 2차원 배열 생성 시, 칼럼 크기 지정은 필수 (그래서 인자로 전달 받음)
	public GameArea(int gfW, int gfH, int columns) {
		// GameForm 크기에 맞춰 GameArea의 (x, y) 위치 변경 
		initThisPanel(gfW, gfH);
		initBlocks();
				
		// 20행 10열이지만, 설정에 따라 셀의 크기가 바뀜. 
		gridColumns = columns; // 10열
		gridCellSize = this.getBounds().width / gridColumns; // 20 -> 25 -> 30 
		gridRows = this.getBounds().height / gridCellSize; // 20행 
		
		// 게임 스레드 시작할 때마다 업데이트 해줘야 함. 
		curIsItem = false;
		updateNextBlock();
	}
	
	// 대전 모드 
	public GameArea(int gfW, int gfH, int columns, int xGap) {
		initThisPanel(gfW, gfH, xGap);
		initBlocks();
		
		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
		
		curIsItem = false;
		updateNextBlock();
	}

	// --------------------------------------------------------------- 초기화 

	private void initThisPanel(int gfW, int gfH) {
		this.setBounds(gfW / 3, gfH / 60, gfW / 3, gfH - 60); // GameArea 크기 변경    
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// 대전 모드 
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
	
	// 게임 스레드 시작할 때마다 상태 업데이트 
	public void initGameArea() {
		curIsItem = false;
		
		initBackgroundArray(); // 시작할 때마다 배경 초기화
		updateNextBlock();
	}
	
	// 대전모드
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

	// --------------------------------------------------------------- 블럭 생성
	
	// 다음 블럭 랜덤으로 
	public void updateNextBlock() {
		// 레벨에 따라 i형 블럭의 생성 확률 조절 
		int level = Tetris.getGameLevel();
		int r = makeRandom(level); 
		
		nextBlock = blocks[r];
		nextBlock.setShape();
	}

	// 다음 아이템 랜덤으로 
	public void updateNextItem() {
		//Random r = new Random();
		//nextBlock = items[r.nextInt(items.length)];
		
		nextBlock = items[4]; // 모든 블럭 삭제 
		
		nextBlock.setShape(); // 여기서 블럭 방향이 랜덤으로 결정됨. 
	}

	// 다음 블럭 모양을 참조하여, 현재 새 블럭 생성 
	public void spawnBlock() {
		block = nextBlock;
		block.spawn(gridColumns); // 랜덤한 위치에서 떨어지도록 
	}
	
	// --------------------------------------------------------------- 난이도 조절  

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

	// 난이도에 따라 I형 블럭의 생성 확률을 다르게 한다. 
	public int makeRandom(int level) {
		Map<String, Double> w = new HashMap<String, Double>();
		Random r = new Random();

		double weight, iWeight;
		int blockNum; // 블럭 종류 

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

		} else { // normal 
			blockNum = r.nextInt(blocks.length);
		}

		return blockNum;
	}

	// ------------------------------------------------------------- 블럭 조작 / 경계 확인
	
	// 블럭이 위쪽 경계를 넘어갔는지 확인한다.
	public boolean isBlockOutOfBounds() {
		if (block.getY() < 0) {
			block = null; // 키 입력해도 블록이 반응하지 않도록
			return true;
		}
		return false;
	}

	public boolean moveBlockDown() {
		// 현재 블럭이 바닥이나 다른 블럭에 닿으면 false
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
		if(block.getBottomEdge() == gridRows) {
			return false; // stop
		}
		
		int[][] shape = block.getShape();
		int w = block.getWidth();
		int h = block.getHeight();
		
		for(int col = 0; col < w; col++) {
			// 특정 열의 맨 밑에서 위쪽으로 올라가다가 
			for(int row = h - 1; row >= 0; row--) {
				// colored cell을 발견했고 
				if(shape[row][col] != 0) { 
					int x = col + block.getX();
					int y = row + block.getY() + 1; // 해당 블록 바로 아래에!
					
					// 보드판에 포함되지 않은 블록은 무시하고 다음 열로 이동 
					if(y < 0) break; 
					
					if(background[y][x] != null) { // 백그라운드 블록이 있으면!
						return false; // stop
					}
					
					break; // 현재 열은 더이상 검사할 필요 없음.
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
					int x = col + block.getX() - 1; // 바로 왼쪽에!
					int y = row + block.getY();
					
					if(y < 0) break; 
					
					if(background[y][x] != null) { // 백그라운드 블록이 있으면!
						return false; // stop
					}
					
					break; // 현재 행은 더이상 검사할 필요 없음.
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
					int x = col + block.getX() + 1; // 바로 오른쪽에!
					int y = row + block.getY(); 
					
					if(y < 0) break;
					
					if(background[y][x] != null) { // 백그라운드 블록이 있으면!
						return false; // stop
					}
					
					break; // 현재 행은 더이상 검사할 필요 없음.
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

	// ------------------------------------------------------------- 아이템 
	
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
	
	// 현재 아이템의 기능을 수행한다.
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

	// 한 줄 삭제 아이템 (블럭 회전에 따라 모양 달라짐)
	public void deleteOneLine() {
		// 경계 체크는 앞에서 이미 한 상태 
		int yPos = block.getY();
		
		clearLine(yPos); 
		shiftDown(yPos);
		clearLine(0);    

		repaint();
	}

	// 두 줄 삭제 아이템 (회전해도 모양 그대로) 
	public void deleteTwoLine() {
		int yPos = block.getY();
		
		// 두 줄 삭제 
		for (int i = 0; i < gridColumns; i++) {
			background[yPos][i] = null;
			background[yPos + 1][i] = null;
		}
		
		// 두칸씩 내리기 
		for (int row = yPos; row >= 2; row--) { // 0, 1행은 제외 
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 2][col];
			}
		}
		
		// 제일 위의 2행 삭제 
		for (int i = 0; i < gridColumns; i++) {
			background[0][i] = null;
			background[1][i] = null;
		}
		
		repaint();
	}

	// 무게추 아이템 (회전해도 모양 그대로) 
	public void deleteWeight() {
		// 밑으로 한칸씩 내려가면서 
		for (int row = block.getBottomEdge(); row < gridRows; row++) {
			// 블럭의 너비만큼 삭제 
			for (int col = block.getLeftEdge(); col < block.getRightEdge(); col++) {
				background[row][col] = null;
			}
			
			block.moveDown();
			repaint();
		}
	}

	// 좌우아래를 모두 삭제하는 바구니 아이템 (회전해도 모양 그대로)
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
	
	// 모든 블럭을 삭제하는 아이템 
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

	// ------------------------------------------------------------------ 배경, 줄 삭제  
	
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
				if (shape[r][c] == 1) { // 해당 위치가 현재 블럭이 차지하는 공간이라면
					background[r + yPos][c + xPos] = color;
				}
			}
		}
		
		block = null; // 이 부분이 왜 필요한 걸까! 
	}

	// 줄 삭제하면서 그 개수를 리턴
	public int clearLines() {
		boolean lineFilled;
		int linesCleared = 0;

		for (int r = gridRows - 1; r >= 0; r--) {
			lineFilled = true; // 행마다 상태 업데이트

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

				r++; // 줄이 삭제된 바로 그 행부터 다시 검사
			}
		}

		return linesCleared;
	}
	
	// 대전모드
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
				
				// 이미 공격한 줄 수를 센다. 
				setGrayLinesNum();
				if (grayLinesNum + attackLinesNum < 10) {
					
					// 공격에 사용될 줄 생성! 
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
		clearLine(r); // r행 삭제 
		shiftDown(r); // 0행 제외하고 한칸씩 내리기 
		clearLine(0); // 0행 삭제 

		repaint();
	}

	// 배경에서 r행을 지운다.
	private void clearLine(int r) {
		for (int i = 0; i < gridColumns; i++) {
			background[r][i] = null;
		}
	}

	// r행 위의 모든 줄을 한칸씩 내려준다.
	private void shiftDown(int r) {
		for (int row = r; row > 0; row--) { // 0행은 제외 
			for (int col = 0; col < gridColumns; col++) {
				background[row][col] = background[row - 1][col];
			}
		}
	}
  
  	// -------------------------------------------------------------------------------- 대전모드를 위한 동작
	
	// 상대의 배경을 인자로 받아서 변수로 저장한다.
	public void setOpponent_bg(Color[][] oppbg) {
		this.opponent_bg = oppbg;
	}
	
	// 블럭을 배경으로 옮기기 전의 배경상태를 저장해 두기 위해 사용한다.
	public void saveBackground() {
		
		pre_background = new Color[gridRows][gridColumns];
		Color[][] cur_background = getBackgroundArray();


		for (int row = 0; row < gridRows; row++) {
			for (int col = 0; col < gridColumns; col++) {
				pre_background[row][col] = cur_background[row][col];
			}
		}
	}
	
	// 블럭이 배경으로 옮겨지기 전의 배경에서 공격에 사용될 줄을 저장한다.
	public void makeAttackLine(int r) {
		attackLines[attackLinesNum] = pre_background[r];
		attackLinesNum++;
	}

	// 상대 배경의 아래 10줄 중 gray 블럭이 포함된 줄의 수를 세서 공격한 줄 수를 구한다. 
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
	
	// 상대 배경 아래에 공격 줄을 추가하기 전에 상대의 배경을 위로 올려준다.
	public void shiftUp_oppBg() {
		
		for (int row = 0; row < gridRows - attackLinesNum; row++) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[row][col] = opponent_bg[row + attackLinesNum][col];
			}
		}

		// 아래 줄들은 null로 채워준다.
		for (int r = attackLinesNum; r > 0; r-- ) {
			for (int col = 0; col < gridColumns; col++) {
				opponent_bg[gridRows - r][col] = null;
			}
		}
	}

	// 공격할 줄에서 채워진 부분만 gray로 상대의 배경에 채워넣는다.
	public void attack() {
		
		// 두 줄 이상이 삭제되었을 때만
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
	
	// ------------------------------------------------------------- 그리기 
	
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

					if (this.block instanceof DeleteOneLine) {
						drawGridL(g, c, x, y);
					}
					// 현재 블럭이 아이템이면 원으로, 기본 블럭이면 사각형으로 그린다. 
					else if (curIsItem) {
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
				// 백그라운드 블록에 대한 참조
				color = background[r][c];

				// moveBlockToBackground 함수에서 컬러가 설정되면 not null
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
		return gridCellSize; // NBA에 블럭 그릴 때 필요 
	}
}