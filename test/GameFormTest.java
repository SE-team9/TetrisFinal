import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.GameForm;

class GameFormTest {

	@Test
	void testGameForm() {
		fail("Not yet implemented");
	}

	@Test
	void testInitComponents() {
		fail("Not yet implemented");
	}

	@Test
	void testInitControls() {
		fail("Not yet implemented");
	}

	@Test
	void testStartGame() { // 비기능적 요구사항: 5초 안에 게임 시작
		final GameForm gf = new GameForm(600,450);
		assertTimeout(Duration.ofMillis(5000),() ->{
			gf.startGame();
			Thread.sleep(300);
		});
	}

	@Test
	void testUpdateScore() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateLevel() {
		fail("Not yet implemented");
	}

}