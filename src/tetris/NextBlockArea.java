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

	private TetrisBlock[] blocks;
	private TetrisBlock nextBlock;
	private int width; // �� �г��� ũ�� 
	private int gridCellSize;
	private boolean curIsItem; // ������ ������ Ȯ���ϴ� ����
	
	private static final int defaultBlockNum = 7;
	private static final int itemBlockNum = 5;
	
	public boolean isOLC; // ���� ���� ������ ����
	private ArrayList<Pair> coloredCells = new ArrayList<>();
	private int randIdx; 
	private Pair Lpos; // �������� ������ ��ġ

	public NextBlockArea(int w, int h, GameArea ga) {
		initThisPanel(w, h);
		
		nextBlock = ga.getNextBlock();
		gridCellSize = ga.getGridCellSize();
	}
	
	// ���� ���
	public NextBlockArea(int w, int h, GameArea ga, int xGap) {
		initThisPanel(w, h, xGap);
		
		nextBlock = ga.getNextBlock();
		gridCellSize = ga.getGridCellSize();
	}

	private void initThisPanel(int w, int h) {
		updatePanelSize(); // width �ʱ�ȭ 
		this.setBounds(w / 60, h / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	// ���� ��� 
	private void initThisPanel(int w, int h, int xGap) {
		updatePanelSize(); 
		this.setBounds(w / 15 + xGap, h / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	private void updatePanelSize() {
		int data = Tetris.getFrameSize();
		if(data == 0) {
			this.width = 120;
		}else if(data == 1) {
			this.width = 140;
		}else {
			this.width = 160;
		}
	}
	
	public void initBlocks() {
		// �ε��� 0~6������ �⺻ ��, �ε��� 7~10�� ������ 
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), 
				new TShape(), new ZShape(), new SShape(), 
				
				new TwoLineClear(), new Weight(), new DeleteAroundU(), new AllClear() };
	}
	
	// ���� �� �������� 
	public void updateNextBlock() {
		// ������ ���� i�� ���� ���� Ȯ�� ���� 
		int level = Tetris.getGameLevel();
		int r = makeRandom(level);
		nextBlock = blocks[r]; // �⺻ ��
		nextBlock.setShape();
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

				for (int i = 1; i < defaultBlockNum; i++) {
					w.put(Integer.toString(i), weight);
				}

				blockNum = Integer.parseInt(getWeightedRandom(w, r));
			} else if (level == 2) { // hard
				weight = 15.0;
				iWeight = 10.0; // I�� ���� ���� Ȯ�� 20% ����
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
	
	public void initNextBlockArea() {
		this.curIsItem = false;
	}
	
	public void setIsItem(boolean answer) {
		curIsItem = answer;
	}

	public void updateNBA(TetrisBlock nextblock) {
		this.nextBlock = nextblock;
		repaint();
	}

	private void drawBlock(Graphics g) {
		if(curIsItem && isOLC) { // ���� ���� ������
			drawOLCItem(g);
		}
		
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

					if (curIsItem) {
						drawGridOval(g, c, x, y);
					} else {	
						drawGridSquare(g, c, x, y);
					}
				}
			}
		}
	}

	// repaint �� ������ �ٲ�� �� ��ǥ�� ���� �׸��� �ٽ� �׸���. 
	private void drawOLCItem(Graphics g) {
		
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
					
				}
			}
		}
		System.out.println();
		
		// ó�� ���� ������ �� ������ ���� ��ġ�� L���ڸ� �׸���. 
		Lpos = coloredCells.get(randIdx);
		g.setFont(new Font("TimesRoman", Font.BOLD, 25));
	    g.setColor(Color.black);
		g.drawString("L", Lpos.x, Lpos.y + gridCellSize);
		
		for(int i = 0; i < 4; i++) {
			if(i != randIdx) {
				Pair p = coloredCells.get(i);
				drawGridSquare(g, c, p.x, p.y);
			}
		}
		
		coloredCells.clear(); // �� ���� repaint�� ���� ��ǥ ������ ���� Ŭ����!! 
	}

	private void drawGridSquare(Graphics g, Color color, int x, int y) {
		g.setColor(color);
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBlock(g);
	}
	
	public boolean getIsItem() {
		return this.curIsItem;
	}
}