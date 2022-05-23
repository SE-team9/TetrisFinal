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
	private int width; // �� �г��� ũ�� 
	private int gridCellSize;
	private boolean isItem; // ������ ������ Ȯ���ϴ� ����

	private TetrisBlock[] blocks;
	private static final int defaultBlockNum = 7;
	private static final int itemBlockNum = 5;
	
	private ArrayList<Pair> coloredCells = new ArrayList<>(); // colored cell 4���� ��ǥ ���� 
	private int randIdx; // L���ڰ� �������� �ٴ� ��ġ 
	private int blockIdx; // ���� ���� �ε��� 
	
	public NextBlockArea(int w, int h) {
		initThisPanel(w, h);
		initBlocks();
		
		// ���� ó���� ��ü ������ ���� �Ϲ� ������ �����ֱ� 
		updateNextBlock();
	}
	
	// ���� ���
	public NextBlockArea(int w, int h, int xGap) {
		initThisPanel(w, h, xGap);
		initBlocks();
		
		updateNextBlock();
	}
	
	// �� �ʱ�ȭ 
	public void initBlocks() {
		// �ε��� 0~6������ �⺻ ��, �ε��� 7~10�� ������, 11�� ���� ���� 
		blocks = new TetrisBlock[] { new IShape(), new JShape(), new LShape(), new OShape(), 
				new TShape(), new ZShape(), new SShape(), 
				
				new TwoLineClear(), new Weight(), new DeleteAroundU(), new AllClear() };
	}
	
	// ���� �� �������� ���� 
	public void updateNextBlock() {
		// ������ ���� i�� ���� ���� Ȯ�� ���� 
		int level = Tetris.getGameLevel();
		blockIdx = makeRandom(level); // �ε��� 0~6
		
		// ���� �� ������Ʈ 
		nextBlock = blocks[blockIdx];
		
		// ȸ���� ���� �޶����� �� ��� �ʱ�ȭ
		nextBlock.setShape();
	}
	
	// ���� ������ �������� ���� 
	public void updateNextItem() {
		
		// �ε��� 7 8 9 10 11 �߿��� �������� 11�� ������ ���� ���� �������̶�� ��������. 
		Random r = new Random();
		//blockIdx = 7 + r.nextInt(itemBlockNum);
		blockIdx = 11;
		
		if(blockIdx == 11) { // ���� ���� ������ 
			
			//--------------------------------------------
			// �⺻ ������ (0~6)
			nextBlock = blocks[r.nextInt(defaultBlockNum)];
			
			// L ���ڰ� ���� ���� ��ġ ����
			randIdx = r.nextInt(4);

			//--------------------------------------------
			
		}else {
			// ������ ������ (7~10)
			nextBlock = blocks[blockIdx];
		}
		
		nextBlock.setShape();
	}
	
	// ------------------------------------ GameThread�� ���� GameArea�� �Ѱ���� �ϴ� ������ 
	
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
	
	// ������ �ٽ� ���۵� ������ �ʱ�ȭ
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

	// -------------------------------------------------------------- �׸��� 
	
	class Pair{
		int x, y;
		Pair(int x, int y){
			this.x = x;
			this.y = y;
		}
	}

	// nextBlock�� moveDown�� ���� �ʱ� ������ y��ǥ�� �����Ǿ� �ִ�. 
	// �ʱ� ��ġ�� �����ϰ� �����ָ� �ȴ�.
	private void drawBlock(Graphics g) {
		if(isItem && blockIdx == 11) { // ���� ���� ������
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
		
		// �����忡 ���� ��ĭ�� ������ ������ �� �ٽ� �׸��� 
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
		
		// ó�� ���� ������ �� ������ ���� ��ġ�� L���ڸ� �׸���. 
		Pair Lpos = coloredCells.get(randIdx);
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
	
	// ---------------------------------------------------------- ���̵��� ���� �� ���� Ȯ�� �ٸ��� 

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
	
	// ------------------------------------------------------------ ȭ�� �ʱ�ȭ 
	
	private void initThisPanel(int w, int h) {
		updatePanelSize(); // ������Ʈ ũ�� ���� 
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