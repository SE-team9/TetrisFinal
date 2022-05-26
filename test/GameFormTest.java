import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import form.GameForm;

class GameFormTest {
	

	@Test
	void testStartGame() { // 비기능적 요구사항: 5초 안에 게임 시작
		final GameForm gf = new GameForm(600,450);
		assertTimeout(Duration.ofMillis(5000),() ->{
			gf.startGame();
			Thread.sleep(300);
		});
	}
	
	@Test
	void testStartGame_pvp() { // 비기능적 요구사항: 10초 안에 대전 모드 게 시작
		final GameForm gf = new GameForm(600,450);
		gf.initComponents_pvp(600, 450);
		assertTimeout(Duration.ofMillis(10000),() ->{
			gf.startGame_pvp();
			Thread.sleep(300);
		});
	}
	
	@Test
	void testUpdateTime() {
		final GameForm gf = new GameForm(600,450);
		gf.initComponents_pvp(600, 450);
		gf.displayTime(1);
		gf.updateTime(100, 1);
		assertNotNull(gf.getLblTime());		
	}
	
	@Test
	void testGetNBA() {
		final GameForm gf = new GameForm(600,450);
		gf.initComponents_pvp(600, 450);
		assertNotNull(gf.getNBA());
	}

	@Test
	void testUpdateScore() {
		final GameForm gf = new GameForm(600,450);
		gf.initComponents_pvp(600, 450);
		gf.updateScore(100,1);
		assertNotNull(gf.getLblScore());
		gf.updateScore(100,2);
		assertNotNull(gf.getLblScore2());
		
	}

	@Test
	void testUpdateLevel() {
		final GameForm gf = new GameForm(600,450);
		gf.initComponents_pvp(600, 450);
		gf.updateLevel(2, 1);
		assertNotNull(gf.getLblLevel());
		gf.updateLevel(2, 2);
		assertNotNull(gf.getLblLevel2());
	}
	
//	@Test 
//	void testInterrupt_Opp() {
//		final GameForm gf = new GameForm(600,450);
//		gf.initComponents_pvp(600, 450);
//		gf.interrupt_Opp(1);
//		
//	}

}