import static org.junit.jupiter.api.Assertions.*;

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
	}

}
