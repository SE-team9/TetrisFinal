package tetris;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class AttackLineArea extends JPanel{
	private static int gfW = 600, gfH = 460;
	private Color[][] bg;

	private int gridCellSize;

	public AttackLineArea(int w, int h, int locate) {
		this.gfW = w;
		this.gfH = h;

		initThisPanel(locate);

		gridCellSize = gfW / 50;
	}

	private void initThisPanel(int locate) {
		this.setBounds(gfW * 11 / 15  +locate, gfH / 60, gfW / 50 * 10, gfW / 50 * 10);
		this.setBackground(new Color(238, 238, 238));
		this.setBorder(LineBorder.createBlackLineBorder());
	}

	public void updateAttackLines() {
		repaint();
	}

	public void set_bg(Color[][] bg) {
		this.bg = bg;
	}

	// 배경을 그려준다.
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
}
