import static org.junit.jupiter.api.Assertions.*;

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
	}
}
