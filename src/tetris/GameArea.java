package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tetrisitems.*;

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
	
	private Color[][] background;
	private TetrisBlock block; // 현재 블럭 
	private boolean isItem;
	
	private ArrayList<Pair> coloredCells = new ArrayList<>(); // colored cell 4개의 좌표 저장 
	private Pair Lpos; // L문자의 최종 좌표 
	private int randIdx; // L문자가 랜덤으로 붙는 위치 
	private int blockIdx; // 한줄 삭제 아이템 구분을 위한 인덱스 
	private boolean OLCflag; // 한줄 삭제 아이템에 대한 점수 부여 
	
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
				
		// 20행 10열이지만, 설정에 따라 셀의 크기가 바뀜. 
		gridColumns = columns; // 10열
		gridCellSize = this.getBounds().width / gridColumns; // 20 24 28
		gridRows = this.getBounds().height / gridCellSize; // 20행 
		
		// 게임 스레드 시작할 때마다 상태 업데이트 
		isItem = false;
	}
	
	// 대전 모드 
	public GameArea(int gfW, int gfH, int columns, int xGap) {
		initThisPanel(gfW, gfH, xGap);
		
		gridColumns = columns;
		gridCellSize = this.getBounds().width / gridColumns;
		gridRows = this.getBounds().height / gridCellSize;
		
		isItem = false;
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
	
	// 게임 스레드 시작할 때마다 상태 업데이트 
	public void initGameArea() {
		isItem = false;
		initBackgroundArray();
	}
	
	// 대전모드
	public void initGameArea_pvp() {
		isItem = false;
		grayLinesNum = 0;
		attackLines = new Color[10][gridColumns];
		initBackgroundArray();
	}
	
	public void setIsItem(boolean b) {
		isItem = b;
	}

	// --------------------------------------------------------------- 블럭 생성

	// 새 블럭 생성 
	public void spawnBlock(TetrisBlock nextBlock) {
		// 다음 블럭의 모양을 그대로 가져온다. 
		block = nextBlock;
		
		// 랜덤한 위치에서 떨어지도록
		block.spawn(gridColumns);
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

	// 한 줄 삭제 아이템 
	public void clearOneLine() {
		int row = Lpos.y / gridCellSize;
		
		clearLine(row); 
		shiftDown(row); 
		clearLine(0);    
		
		// 한 줄 삭제에 대한 점수 부여를 위해 플래그 설정 
		OLCflag = true;

		repaint();
	}

	// 두 줄 삭제 아이템 (회전해도 모양 그대로) 
	public void clearTwoLine() {
		int y = block.getY();
		clearLine(y);
		shiftDown(y);
		
		y++;
		clearLine(y);
		shiftDown(y);
		
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

	// ------------------------------------------------------------------ 줄 삭제, 배경으로 전환   
	
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
	// 여기서 NPE 발생함 ----------------------------------------------
	public void makeAttackLine(int r) {
		attackLines[attackLinesNum] = pre_background[r];
		attackLinesNum++;
	}
	// ----------------------------------------------------------------

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
	
	// ------------------------------------------------------------- 그리기 
	
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
	
	// 현재 블럭을 그려준다.
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
		
		// 스레드에 의해 한칸씩 떨어질 때마다 블럭 다시 그리기 
		repaint();
	}

	// repaint 될 때마다 바뀌는 블럭 좌표에 따라 그림을 다시 그린다. 
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
		
		// 처음 블럭이 생성될 때 결정된 랜덤 위치에 L문자를 그린다.
		Lpos = coloredCells.get(randIdx);
		
		g.setFont(new Font("TimesRoman", Font.BOLD, 25));
	    g.setColor(Color.black);	    
		g.drawString("L", Lpos.x, Lpos.y + gridCellSize);
		
		// 다른 셀에는 사각형 그리기 
		for(int i = 0; i < 4; i++) {
			if(i != randIdx) {
				Pair p = coloredCells.get(i);
				drawGridSquare(g, c, p.x, p.y);
			}
		}
		
		coloredCells.clear(); // 그 다음 repaint에 대한 좌표 저장을 위해 클리어!!
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

	// ------------------------------------------------------------------- 테스트 코드에 쓰이는 getter
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
//		return gridCellSize; // NBA에 블럭 그릴 때 필요 
//	}
}