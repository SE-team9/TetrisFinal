package tetris;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class NextBlockArea extends JPanel {
	private static int gfW = 600, gfH = 450;
	private GameArea ga;
	private TetrisBlock nextBlock;
	private int gridCellSize;
	private boolean isItem = false;  // 아이템 블럭인지 확인하는 변수

	public NextBlockArea(int w, int h, GameArea ga) {
		this.gfW = w;
		this.gfH = h;
		
		initThisPanel();

		this.ga = ga;
		nextBlock = ga.getNextBlock();
		gridCellSize = ga.getGridCellSize();
	}

	private void initThisPanel() {
		this.setBounds(gfW / 60, gfH / 60, 120, 120);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	public void initNextBlockArea() {
		this.isItem = false;
	}

	public void updateNBA(TetrisBlock nextblock) {
		this.nextBlock = nextblock;
		repaint();
	}
	
	public void setIsItem(boolean answer) {
		isItem = answer;
	}

	private void drawBlock(Graphics g) {
		int h = nextBlock.getHeight();
		int w = nextBlock.getWidth();
		Color c = nextBlock.getColor();
		int[][] shape = nextBlock.getShape();

		int centerX = (this.getWidth() - w * gridCellSize) / 2;
		int centerY = (this.getHeight() - h * gridCellSize) / 2;

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				if (shape[row][col] == 1) {

					int x = centerX + col * gridCellSize;
					int y = centerY + row * gridCellSize;

					if (isItem) {
						drawGridOval(g, c, x, y);
					} else {	
						drawGridSquare(g, c, x, y);
					}
				}
			}
		}
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
		return this.isItem;
	}
}