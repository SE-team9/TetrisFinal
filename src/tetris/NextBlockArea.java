package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tetris.GameArea.Pair;
import tetrisblocks.IShape;
import tetrisblocks.JShape;
import tetrisblocks.LShape;
import tetrisblocks.OShape;
import tetrisblocks.SShape;
import tetrisblocks.TShape;
import tetrisblocks.ZShape;
import tetrisitems.AllClear;
import tetrisitems.DeleteAroundU;
import tetrisitems.TwoLineClear;
import tetrisitems.Weight;

public class NextBlockArea extends JPanel {
	private TetrisBlock nextBlock;
	private int width; // 이 패널의 크기 
	private int gridCellSize;
	private boolean isItem; // 아이템 블럭인지 확인하는 변수

	private TetrisBlock[] blocks;
	private static final int defaultBlockNum = 7;
	private static final int itemBlockNum = 5;
	
	private ArrayList<Pair> coloredCells = new ArrayList<>(); // colored cell 4개의 좌표 저장 
	private int randIdx; // L문자가 랜덤으로 붙는 위치 
	private int blockIdx; // 현재 블럭의 인덱스 
	
	public NextBlockArea(int w, int h) {
		initThisPanel(w, h);
		initBlocks();
		
		// 제일 처음에 객체 생성할 때는 일반 블럭으로 보여주기 
		updateNextBlock();
	}
	
	// 대전 모드
	public NextBlockArea(int w, int h, int xGap) {
		initThisPanel(w, h, xGap);
		initBlocks();
		
		updateNextBlock();
	}
	
	// 블럭 초기화 
	public void initBlocks() {
		// 인덱스 0~6까지는 기본 블럭, 인덱스 7~10은 아이템, 11은 한줄 삭제 
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), 
				new TShape(), new ZShape(), new SShape(), 
				
				new TwoLineClear(), new Weight(), new DeleteAroundU(), new AllClear() };
	}
	
	// 다음 블럭 랜덤으로 설정 
	public void updateNextBlock() {
		// 레벨에 따라 i형 블럭의 생성 확률 조절 
		int level = Tetris.getGameLevel();
		blockIdx = makeRandom(level); // 인덱스 0~6
		
		// 다음 블럭 업데이트 
		nextBlock = blocks[blockIdx];
		
		// 회전에 따라 달라지는 블럭 모양 초기화
		nextBlock.setShape();
	}
	
	// 다음 아이템 랜덤으로 설정 
	public void updateNextItem() {
		
		// 인덱스 7 8 9 10 11 중에서 랜덤으로 11이 나오면 한줄 삭제 아이템이라고 생각하자. 
		Random r = new Random();
		//blockIdx = 7 + r.nextInt(itemBlockNum);
		blockIdx = 11;
		
		if(blockIdx == 11) { // 한줄 삭제 아이템 
			
			//--------------------------------------------
			// 기본 블럭으로 (0~6)
			nextBlock = blocks[r.nextInt(defaultBlockNum)];
			
			// L 문자가 붙을 랜덤 위치 결정
			randIdx = r.nextInt(4);

			//--------------------------------------------
			
		}else {
			// 아이템 블럭으로 (7~10)
			nextBlock = blocks[blockIdx];
		}
		
		nextBlock.setShape();
	}
	
	// ------------------------------------ GameThread를 통해 GameArea에 넘겨줘야 하는 정보들 
	
	public TetrisBlock getNextBlock() {
		return nextBlock;
	}
	
	public int getBlockIndex() {
		return blockIdx;
	}
	
	public int getRandIndex() {
		return randIdx;
	}
	
	// ------------------------------------- 
	
	// 게임이 다시 시작될 때마다 초기화
	public void initNextBlockArea() {
		this.isItem = false;
		updateNextBlock();
	}
	
	public boolean getIsItem() {
		return this.isItem;
	}
	public void setIsItem(boolean b) {
		this.isItem = b;
	}

	// -------------------------------------------------------------- 그리기 
	
	class Pair{
		int x, y;
		Pair(int x, int y){
			this.x = x;
			this.y = y;
		}
	}

	// nextBlock이 moveDown을 하지 않기 때문에 y좌표가 고정되어 있다. 
	// 초기 위치와 동일하게 보여주면 된다.
	private void drawBlock(Graphics g) {
		if(isItem && blockIdx == 11) { // 한줄 삭제 아이템
			drawRandomL(g);
		}
		else {
			int h = nextBlock.getHeight();
			int w = nextBlock.getWidth();
			Color c = nextBlock.getColor();
			int[][] shape = nextBlock.getShape();

			int centerX = (this.getWidth() - w * gridCellSize) / 2;
			int centerY = (this.getHeight() - h * gridCellSize) / 2;

			for (int row = 0; row < h; row++) {
				for (int col = 0; col < w; col++) {
					if (shape[row][col] == 1) {
						
						int x = centerX + (col * gridCellSize);
						int y = centerY + (row * gridCellSize);

						if (isItem) {
							drawGridCircle(g, c, x, y);
						} else {	
							drawGridSquare(g, c, x, y);
						}
					}
				}
			}
		}
		
		// 스레드에 의해 한칸씩 떨어질 때마다 블럭 다시 그리기 
		repaint();
	}
	
	private void drawRandomL(Graphics g) {
		int h = nextBlock.getHeight();
		int w = nextBlock.getWidth();
		Color c = nextBlock.getColor();
		int[][] shape = nextBlock.getShape();
		
		int centerX = (this.getWidth() - w * gridCellSize) / 2;
		int centerY = (this.getHeight() - h * gridCellSize) / 2;
		
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (shape[row][col] == 1) { 
					int x = centerX + (col * gridCellSize);
					int y = centerY + (row * gridCellSize);
					coloredCells.add(new Pair(x, y));
				}
			}
		}
		
		// 처음 블럭이 생성될 때 결정된 랜덤 위치에 L문자를 그린다. 
		Pair Lpos = coloredCells.get(randIdx);
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

	private void drawGridSquare(Graphics g, Color color, int x, int y) {
		g.setColor(color);
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
		drawBlock(g);
	}
	
	// ---------------------------------------------------------- 난이도에 따라 블럭 생성 확률 다르게 

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

			for (int i = 1; i < defaultBlockNum; i++) {
				w.put(Integer.toString(i), weight);
			}

			blockNum = Integer.parseInt(getWeightedRandom(w, r));
		} else if (level == 2) { // hard
			weight = 15.0;
			iWeight = 10.0; // I형 블럭의 등장 확률 20% 감소
			w.put("0", iWeight);

			for (int i = 1; i < defaultBlockNum; i++) {
				w.put(Integer.toString(i), weight);
			}

			blockNum = Integer.parseInt(getWeightedRandom(w, r));

		} else { // normal 
			blockNum = r.nextInt(defaultBlockNum);
		}

		return blockNum;
	}
	
	// ------------------------------------------------------------ 화면 초기화 
	
	private void initThisPanel(int w, int h) {
		updatePanelSize(); // 컴포넌트 크기 조절 
		this.setBounds(w / 60, h / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// 대전 모드 
	private void initThisPanel(int w, int h, int xGap) {
		updatePanelSize(); 
		this.setBounds(w / 15 + xGap, h / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	private void updatePanelSize() {
		int data = Tetris.getFrameSize();
		switch(data) {
		case 0:
			width = 120;
			gridCellSize = 20;
			break;
		case 1:
			width = 140;
			gridCellSize = 24;
			break;
		case 2:
			width = 160;
			gridCellSize = 28;
			break;
		}
	}
}