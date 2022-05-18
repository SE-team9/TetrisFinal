package tetris;
import javax.swing.JOptionPane;

import form.GameForm;

public class GameThread extends Thread {
	private GameForm gf;
	private GameArea ga;
	private NextBlockArea nba;
	
	private int score = 0; // �� ������ ��� ���� 
	private int level = 1;  
	private int linePerLevel = 3; // �� ������ ���� ���� ��� 
	private int interval = 1000;
	private int speedupPerLevel; // ������ ���� �ӵ� ��� 
	private boolean isPaused = false;
	private int levelMode; // ���� ȭ�鿡�� ���� ���� ���̵�
	private int gameMode; // ���� ȭ�鿡�� ���� ���� ��� (�Ϲ�, ������)

	// ������ ��� 
	private int totalClearedLine; 	    // ������ ���� �� �����ؼ� ���� 
	private boolean nextIsItem = false; // ���� ���� ���������� Ȯ�� 
	private boolean curIsItem = false; 	// ���� ���� ���������� Ȯ�� 
	
	private int itemCount = 0; // �������� ������ Ƚ�� ī���� 
	private static final int linePerItem = 2; // �� ������ ���� ������ ����
	
	// ���� ��� 
	private int userId;
	private int time = 100;
	private AttackLineArea ala;

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		// ����, ���� �ؽ�Ʈ �ʱ�ȭ 
		gf.updateScore(score);
		gf.updateLevel(level);
		
		setGameMode();
	}

	// ���� ���
	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba, AttackLineArea ala, int userId) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;
		this.ala = ala;
		this.userId = userId;
		
		gf.updateScore(score, userId);
		gf.updateLevel(level, userId);
		
		setGameMode();
	}
	
	private void setGameMode() {
		// �Ϲ� ���, ������ ���
		gameMode = Tetris.getGameMode();
		
		// ������ ����� ���̵��� ���� �� ���� �ӵ� ���� 
		levelMode = Tetris.getGameLevel();
		switch (levelMode) {
		case 0:
			speedupPerLevel = 80;
			break;
		case 1:
			speedupPerLevel = 100;
			break;
		case 2:
			speedupPerLevel = 120;
			break;
		}
	}
	
	@Override
	public void run() { // ���� ������ ����
		switch (gameMode) {
		case 0:
			startDefaultMode(); 	// �Ϲ�+�Ϲ�
			break;
		case 1:
			startItemMode(); 		// �Ϲ�+������
			break;
		case 2:
			//startDefaultMode_pvp(); // ����+�Ϲ�
			break;
		case 3:
			//startItemMode_pvp(); 	// ����+������
			break;
		case 4:
			//startTimeAttackMode_pvp(); // ����+�ð�����
			break;
		}
	}

	private void startDefaultMode() {
		while(true) {
			ga.spawnBlock(); // �� �� ���� 
			ga.updateNextBlock(); // ���̵��� ���� I�� ���� ���� Ȯ�� ���� 
			nba.updateNBA(ga.getNextBlock()); // ���� �� ǥ�� 
			
			// ���� ���� ��踦 ���� ������ ��� ���� 
			while (ga.moveBlockDown()) {
				score++; // �� ������ ��� ���� 
				gf.updateScore(score);
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted...");
					return; // ���� ������ ���� 
				}
				
				// �������� ���� ���鼭 ���
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}
			
			// ��踦 ������ ���� ���� 
			if (ga.isBlockOutOfBounds()) {
				// ���� ���� ������ ���Ͽ� �����ϰ� ���ھ�� ���� 
				Tetris.gameOver(gameMode, score, levelMode);
				
				System.out.println("Thread Interrupted...");
				return; // ���� ������ ���� 
			}

			// �ٴڱ��� ������ ���� ������� ��ȯ 
			ga.moveBlockToBackground();
			
			// ������ �� ���� ���� ���� ���� 
			checkCL();
			
			// ������ ���� ���� �� �ӵ� ���� 
			checkLevel();
		}
	}
	
	private void startItemMode() {
		while (true) {
			ga.spawnBlock(); // �� �� ���� 
			
			// ���� ���� ��� ���� 
			if (nextIsItem) {
				nba.setIsItem(true);
				ga.updateNextItem(); // ���������� 
			} else {
				ga.updateNextBlock(); // �Ϲ� ������
			}
			nba.updateNBA(ga.getNextBlock()); // ���� �� ǥ��
	
			// ���� ���� ��踦 ���� ������ ��� ���� 
			while (ga.moveBlockDown()) { 
				score++; // �� ������ ��� ���� 
				gf.updateScore(score);
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted...");
					return; // ���� ������ ���� 
				}
				
				// �������� ���� ���鼭 ���
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}
	
			// ��踦 ������ ���� ���� 
			if (ga.isBlockOutOfBounds()) {
				// ���� ���� ������ ���Ͽ� �����ϰ� ���ھ�� ���� 
				Tetris.gameOver(gameMode, score, levelMode);
				
				System.out.println("Thread Interrupted...");
				return; // ���� ������ ���� 
			}
	
			// ���� �� ������ ���� �ٸ� ���� ���� 
			checkBlockState();
			
			checkCL(); // ������ �� ���� ���� ���� ���� 
			checkLevel(); // ������ ���� ���� �� �ӵ� ���� 
			
			// linePerItem �̻� �� �����ϸ�, ���� ���� ���������� �����ϱ�  
			checkItem();
		}
	}
	
	// ���ʽ� ���� ȹ�� ���� 
	// 1. �� �� �̻� ������ ��� -> checkCL
	// 2. ������ ���� ���� �ӵ��� ����� ��� -> checkLevel
	
	private void checkCL() {
		int curCL = ga.clearLines();
		System.out.println("curCL: " + curCL);
		
		// ������ ���� ������ ���� ���� X
		
		if(curCL == 1) { // �� �� ���� 
			score += curCL;
			gf.updateScore(score);
			System.out.println("score after 1 line clear: " + score);
			
			totalClearedLine += curCL;
			
		}else if(curCL >= 2) { // �� �� �̻� ���� 
			score += (10 * curCL); // ���ʽ� ����
			gf.updateScore(score);
			System.out.println("score after 2 line clear: " + score);
			
			totalClearedLine += curCL;
		}
	}

	// �� �������� linePerLevel �̻� �� �����ϸ� ���� ����
	private void checkLevel() {
		int lvl = totalClearedLine / linePerLevel + 1;
		if (lvl > level) {
			System.out.println("total CL: " + totalClearedLine);
			
			// ���� ����
			level = lvl;
			gf.updateLevel(level);
			
			// ������ ���� �ӵ� ��� 
			if (interval > 300) {
				interval -= speedupPerLevel;
				
				score += (10 * level); // ���ʽ� ���� 
				gf.updateScore(score);
				System.out.println("score after level update: " + score);
			}
		}
	}

	private void checkBlockState() {
		// ���� ���� �������� ��� 
		if (curIsItem) {
			ga.twinkleItem();
			ga.execItemFunction();

			// �⺻ ������ �ʱ�ȭ 
			ga.setItemFlag(false);
			curIsItem = false;
		} else { 
			// ���� ���� �⺻ ���� ��� 
			ga.moveBlockToBackground();

			// 3�� �̻� �����ؼ� nextIsItem�� true�� �� ���  
			if (nextIsItem) {
				nextIsItem = false;
				nba.setIsItem(false);
				
				curIsItem = true; 
				ga.setItemFlag(true); // ���� ������ ���� 
			}
		}
	}

	// linePerItem �̻� �� �����ϸ�, ���� ���� ���������� ����
	private void checkItem() {
		int temp = totalClearedLine / linePerItem;
		if(temp > itemCount) {
			itemCount = temp; // �������� ������ Ƚ�� ���� 
			
			System.out.println("itemCount: " + itemCount);
			nextIsItem = true; // ���� ���� ���������� ���� 
		}
	}
	
	public void pause() {
		this.isPaused = true;
	}

	public void reStart() {
		this.isPaused = false;
	}
	
	public boolean getIsPaused() {
		return this.isPaused;
	}
	
	public void scorePlus1() {
		score++;
		gf.updateScore(score);
	}

	public void scorePlus15() {
		score += 15;
		gf.updateScore(score);
	}

	public void scorePlus1_pvp() {
		score++;
		gf.updateScore(score, userId);
	}

	public void scorePlus15_pvp() {
		score += 15;
		gf.updateScore(score, userId);
	}
}