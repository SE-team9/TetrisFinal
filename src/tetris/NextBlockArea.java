package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class NextBlockArea extends JPanel {
	private int width; // NextBlockArea의 크기 
	private TetrisBlock nextBlock;
	private int gridCellSize;
	private boolean curIsItem = false;  // 아이템 블럭인지 확인하는 변수

	public NextBlockArea(int gfW, int gfH, GameArea ga) {
		initThisPanel(gfW, gfH);
		
		nextBlock = ga.getNextBlock();
		gridCellSize = ga.getGridCellSize();
	}
	
	private void updatePanelSize() {
		try {
			File file = new File("settings.txt");
			if(!file.exists()) { 
				file.createNewFile(); 
				System.out.println("Create new file.");
			};
			
			FileInputStream fis = new FileInputStream(file);
			int data = fis.read();
			
			// 파일에 저장된 값에 따라 크기 조절
			if(data == 0) {
				this.width = 120;
			}else if(data == 1) {
				this.width = 140;
			}else {
				this.width = 160;
			}
			
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initThisPanel(int gfW, int gfH) {
		updatePanelSize(); // width 초기화 
		
		this.setBounds(gfW / 60, gfH / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	public void initNextBlockArea() {
		this.curIsItem = false;
	}

	public void updateNBA(TetrisBlock nextblock) {
		this.nextBlock = nextblock;
		repaint();
	}
	
	public void setIsItem(boolean answer) {
		curIsItem = answer;
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

					if (curIsItem) {
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
		return this.curIsItem;
	}
}