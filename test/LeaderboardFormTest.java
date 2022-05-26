//package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import form.GameForm;
import form.LeaderboardForm;

class LeaderboardFormTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testLeaderboardForm() {
		final LeaderboardForm lf = new LeaderboardForm(600,450);
		final GameForm gf = new GameForm(600,450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			lf.initComponents(600, 450);
		});
	}

	@Test
	void testMoveRight() {
		final LeaderboardForm lf = new LeaderboardForm(600,450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			lf.moveRight();
		});
		
	}
	
	@Test
	void testMoveLeft() {
		final LeaderboardForm lf = new LeaderboardForm(600,450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			lf.moveLeft();
		});
		
	}
	
	@Test
	void testAddPlayer() {
		final LeaderboardForm lf = new LeaderboardForm(600,450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			lf.addPlayer(1, "sunny", 333, "1");
		});
		
	}

	@Test
	void testMain() {
		final LeaderboardForm lf = new LeaderboardForm(600,450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			lf.main(null);
		});
	}

}