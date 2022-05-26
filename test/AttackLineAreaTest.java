import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tetris.AttackLineArea;
import tetris.Tetris;

class AttackLineAreaTest {
	
	private final AttackLineArea ala = new AttackLineArea(10, 10, 10);
	
	int width;
	
	@Test
	@Disabled
	void testAttackLineArea() {
		fail("Not yet implemented");
	}
	
	@Test
	@DisplayName("프레임 사이즈에 따른 패널 사이즈 업데이트")
	void testInitThisPanel() {
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
	void testset_bg() {
		AttackLineArea ala2 = new AttackLineArea(10, 10, 10);
		ala2.set_bg(ala.get_bg());
		Color[][] bg = ala.get_bg();
		Color[][] bg2 = ala2.get_bg();
		assertEquals(bg, bg2);
	}
	
	@Test
	void testDrawBackGround() {
		fail("Not yet implemented");
	}
	
	@Test
	void testDrawGridSquare() {
		fail("Not yet implemented");
	}

}
