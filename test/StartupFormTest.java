import static org.junit.jupiter.api.Assertions.*;

<<<<<<< HEAD
import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.GameForm;
import form.StartupForm;

class StartupFormTest {

	
	@Test
	void testStartupForm() {
		final StartupForm sf = new StartupForm(600,450);
	}

	@Test
	void testInitComponents() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCurrentGameMode() {
		final StartupForm sf = new StartupForm(600,450);
		assertNotNull(sf.getCurrentGameMode());
	}

	@Test
	void testMain() {
		final StartupForm sf = new StartupForm(600,450);
		assertTimeout(Duration.ofMillis(5000),() ->{
			sf.main(null);
			Thread.sleep(300);
		});
=======
import org.junit.jupiter.api.Test;

import form.StartupForm;

class StartupFormTest {
	@Test
	void testStartupForm() {
		StartupForm sf = new StartupForm(60, 100);
		
		sf.initComponents(50, 100);
		int width = sf.getWidth();
		int height = sf.getHeight();
		assertEquals(50, width);
		assertEquals(100, height);
		sf.setCurrentGameMode(1);
		int curGame = sf.getCurrentGameMode();
		assertEquals(1, curGame);
>>>>>>> b7d357b1c33fa69e4c3d8710ce15527ec086714e
	}

}
