import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.GameForm;
import form.OptionForm;

class OptionFormTest {

	final OptionForm of = new OptionForm(600,450);
	

	@Test
	void testGetCurrentKeyMode() {
		int km = of.getCurrentColorMode();
		assertNotNull(km);
	}

	@Test
	void testGetCurrentGameLevel() { // 대전모드 추가시 변경 필요
		int l = of.getCurrentColorMode();
		assertNotNull(l);
	}

	@Test
	void testGetCurrentColorMode() {
		int cm = of.getCurrentColorMode();
		assertTrue(cm == 0 || cm == 1);
	}

	@Test
	void testSaveAllSettings() {
		assertTimeout(Duration.ofMillis(10000),() ->{
			of.saveAllSettings();
			Thread.sleep(300);
		});
	}
	
	@Test
	void testInitScoreboard() {
		assertTimeout(Duration.ofMillis(10000),() ->{
			of.initScoreboard();
			Thread.sleep(300);
		});
	}
	
	@Test
	void testInitDefaultSettings() {
		assertTimeout(Duration.ofMillis(10000),() ->{
			of.initDefaultSettings();
			Thread.sleep(300);
		});
	}
	
	@Test
	void testMoveUp() {
		int r = of.getRow();
		of.moveUp();
		assertTrue(r != of.getRow());
		
	}
	
	@Test
	void testMoveDown() {
		int r = of.getRow();
		of.moveDown();
		assertTrue(r != of.getRow());
		
	}
	
	@Test
	void testMoveRight() {
		int r = of.getfocusColumn();
		of.moveRight();
		assertTrue(r != of.getfocusColumn());
		
	}
	
	@Test
	void testMoveLeft() {
		int r = of.getfocusColumn();
		of.moveLeft();
		assertTrue(r != of.getfocusColumn());
		
	}

	@Test
	void testMain() {
		assertTimeout(Duration.ofMillis(5000),() ->{
			of.main(null);
			Thread.sleep(300);
		});
	}

}