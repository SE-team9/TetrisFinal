package tetris;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class AttackLineArea extends JPanel{
	private int width;
	private Color[][] bg;
	private int gridCellSize;

	public AttackLineArea(int w, int h, int xGap) {
		initThisPanel(w, h, xGap);
		gridCellSize = w / 50;
	}
	
	private void initThisPanel(int gfW, int gfH, int xGap) {
		updatePanelSize();
		this.setBounds(gfW * 11 / 15 + xGap, gfH / 60, width, width);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}
	
	public void updatePanelSize() {
		int data = Tetris.getFrameSize();
		if(data == 0) {
			this.width = 120;
		}else if(data == 1) {
			this.width = 140;
		}else {
			this.width = 160;
		}
	}

	public void set_bg(Color[][] bg) {
		this.bg = bg;
	}

	private void drawBackground(Graphics g) {
		for (int r = 19; r > 9; r--) {
			for (int c = 0; c < 10; c++) {
				if (bg[r][c] == Color.gray) {
					int x = c * gridCellSize;
					int y = (r - 10) * gridCellSize;
					drawGridSquare(g, Color.gray, x, y);
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

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBackground(g);
	}
	
	public int getWidth() {
		return width;
	}
	
	public Color[][] get_bg(){
		return bg;
	}
	
}