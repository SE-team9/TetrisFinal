import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tetris.AttackLineArea;
import tetris.Tetris;

class AttackLineAreaTest {
	
	private final AttackLineArea ala = new AttackLineArea(10, 10, 10);
	
	int width;
	
	@Test
	@DisplayName("gridCellSize와 InitThisPanel 확인")
	void testArrackLineArea() {
		final AttackLineArea ala2 = new AttackLineArea(200, 200, 200);
		int size = ala2.getGridCellSize();
		assertEquals(size, 4);
	}
	
	@Test
	@DisplayName("프레임 사이즈에 따른 패널 사이즈 업데이트")
	void testUpdatePanelSize() {
		Tetris.setFrameSize(0);
		ala.updatePanelSize();
		assertEquals(120, ala.getWidth());
		
		Tetris.setFrameSize(1);
		ala.updatePanelSize();
		assertEquals(140, ala.getWidth());
		
		Tetris.setFrameSize(5);
		ala.updatePanelSize();
		assertEquals(160, ala.getWidth());
	}
	
	@Test
	@DisplayName("background 설정")
	void testSet_bg() {
		AttackLineArea ala2 = new AttackLineArea(10, 10, 10);
		ala2.set_bg(ala.get_bg());
		Color[][] bg = ala.get_bg();
		Color[][] bg2 = ala2.get_bg();
		assertEquals(bg, bg2);
		Graphics g = null;
	}
}
