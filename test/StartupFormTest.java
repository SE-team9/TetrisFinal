package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.StartupForm;

class StartupFormTest {
	@Test
	void testStartupForm() {
		final StartupForm sf = new StartupForm(600,450);
	}
  
  @Test
	void testMoveUp() {
		final StartupForm sf = new StartupForm(600,450);
		sf.moveUp();
		assertNotNull(sf.getCurPos());
	}
  
	@Test
	void testMoveDown() {
		final StartupForm sf = new StartupForm(600,450);
		sf.moveDown();
		assertNotNull(sf.getCurPos());
	}
	@Test
	void testMoveRight() {
		final StartupForm sf = new StartupForm(600,450);
		sf.moveRight();
		assertNotNull(sf.getCurGameMode());
	}
	
	@Test
	void testMoveLeft() {
		final StartupForm sf = new StartupForm(600,450);
		sf.moveLeft();
		assertNotNull(sf.getCurGameMode());
	}
	

	@Test
	void testGetCurrentGameMode() {
		final StartupForm sf = new StartupForm(600,450);
		assertNotNull(sf.getCurrentGameMode());
	}

  @Test 
	void testSetCurrentGameMode() {
		final StartupForm sf = new StartupForm(600,450);
		sf.setCurrentGameMode(0);
		assertTrue(sf.getCurrentGameMode()==0);
	}
 
	@Test
	void testMain() {
		final StartupForm sf = new StartupForm(600,450);
		assertTimeout(Duration.ofMillis(5000),() ->{
			sf.main(null);
			Thread.sleep(300);
		});
	}
}
