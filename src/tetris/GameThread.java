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
	private static final int linePerItem = 1; // �� ������ ���� ������ ����
	
	// ���� ��� 
	private int userID;
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
	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba, AttackLineArea ala, int userNum) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;
		this.ala = ala;
		this.userID = userNum;
		
		gf.updateScore(score, userNum);
		gf.updateLevel(level, userNum);
		
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
			startDefaultMode_pvp(); // ����+�Ϲ�
			break;
		case 3:
			startItemMode_pvp(); 	// ����+������
			break;
		case 4:
			startTimeAttackMode_pvp(); // ����+�ð�����
			break;
		}
	}

	private void startDefaultMode() {
		while(true) {
			// nba ��ü ó�� ������ �� ������ nextBlock �������� 
			ga.spawnBlock(nba.getNextBlock());
			
			// ���� �� ������Ʈ 
			nba.updateNextBlock();
			
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
	
	
	// ���ʽ� ���� ȹ�� ���� 
	// 1. �� �� �̻� ������ ��� -> checkCL
	// 2. ������ ���� ���� �ӵ��� ����� ��� -> checkLevel

	private void checkCL() {
		int curCL = ga.clearLines();
		System.out.println("curCL: " + curCL);

		// ������ ���� ������ ���� ���� X

		if (curCL == 1) { // �� �� ����
			score += curCL;
			gf.updateScore(score);
			System.out.println("score after 1 line clear: " + score);

			totalClearedLine += curCL;

		} else if (curCL >= 2) { // �� �� �̻� ����
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
	
	// ����+�Ϲ�
	private void startDefaultMode_pvp() {
		while (true) {
			// --------------------------------------------------------- ���ο� �� ����
			
			ga.spawnBlock(nba.getNextBlock());
			nba.updateNextBlock(); 
			
			// --------------------------------------------------------- ��ĭ�� �� ������
			while (ga.moveBlockDown()) {
				score++;
				gf.updateScore(score, userID);

				try {
					Thread.sleep(interval);
				} catch (InterruptedException ex) {
					System.out.println("Thread Interrupted...");
					return;
				}
				
				// �������� ���� ���鼭 ���
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}

			// --------------------------------------------------------- ���� ���� Ȯ��
			if (ga.isBlockOutOfBounds()) {
				// ��� ������ ���� + ����
				gf.interrupt_Opp(userID);
				JOptionPane.showMessageDialog(null, (3 - userID) + " Player Win!");
				gf.setVisible(false);
				Tetris.showStartup();
				break;
			}
      
			// --------------------------------------------------------- ������� �� �̵� / �ϼ��� �� ���� / ���� �� ��������
			
			ga.saveBackground();
			ga.moveBlockToBackground();
			int curCL = ga.clearLines_pvp();
			gf.getAttackLines(userID);

			// --------------------------------------------------------- �� ������ ���� ����, ����, ���� �ӵ� ������Ʈ
			
			if (curCL == 1) { // �� �� ����
				score += curCL;
				gf.updateScore(score);
				totalClearedLine += curCL;

			} else if (curCL >= 2) { // �� �� �̻� ����
				score += (10 * curCL); // ���ʽ� ����
				gf.updateScore(score);
				totalClearedLine += curCL;
			}
			
			int lvl = totalClearedLine / linePerLevel + 1;
			if (lvl > level) {
				// ���� ����
				level = lvl;
				gf.updateScore(level, userID);
				
				// ������ ���� �ӵ� ��� 
				if (interval > 300) {
					interval -= speedupPerLevel;

					score += (10 * level); // ���ʽ� ����
					gf.updateScore(score);
				}
			}
		}
	}
	
	// ������ ��� 
	private void startItemMode() {
		while (true) {
			// ------------------------------------------------------- �� �� ����, ���� �� ���� 
			
			// �� ����� ������ �� ������, L���ڸ� ���̴� �� ���� �����ؾ� �Ѵ�. 
			ga.spawnBlock(nba.getNextBlock());
			
			// ���� �� ǥ�� 
			if (nextIsItem) {
				nba.setIsItem(true);
				nba.updateNextItem(); // ���������� (�� �ε��� ������Ʈ)
				
				// nba�� ga�� ������ L���ڰ� �ٴ� ��ġ�� �����ϵ���  
				if(nba.getBlockIndex() == 11) {
					ga.setRandomIndex(nba.getRandIndex());
				}
				
			}else {
				nba.updateNextBlock(); // �Ϲ� ������
			}
		
			// ------------------------------------------------------- ��ĭ�� ������ 
			
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
			
			// ------------------------------------------------------- ���� ���� �ٴ��̳� �ٸ� ���� ���� ��� 
			
			// ���� ���� �������̸�, �׿� ���� ��� ���� 
			if(curIsItem) {
				ga.twinkleItem();
				ga.execItemFunction();

				// ���� ���� �����ۿ� ���� ���� �ο� 
				if(ga.getOLCflag()) {
					score += 1;
					gf.updateScore(score);
					System.out.println("score after OLC item: " + score);
					totalClearedLine += 1;
					
					ga.setOLCflag(false);
				}

				ga.setIsItem(false);
				curIsItem = false; // �÷��� ������Ʈ
				
				continue; // �� ���� �����ۿ� ���ؼ��� ����, ����, �ӵ� ��ȭ ����.
			}
			else {
				// ���� ���� �⺻ ���̸�, ������� ��ȯ 
				ga.moveBlockToBackground();
				
				// ���� ���� ������� ��ȯ�� �Ŀ� ���� ���� �������̸� 
				if(nextIsItem) {
					// ���� ���� �ٽ� �⺻ ������ ǥ��! 
					nextIsItem = false;
					nba.setIsItem(false);
					
					// ���� ������ �� ����! 
					curIsItem = true;
					ga.setIsItem(true);
					
					// ���⼭ ���� ���� �������� �ε����� ��������� L���ڸ� �׸� �� ����.
					ga.setBlockIndex(nba.getBlockIndex());
				}
				
				// �� �κ��� �⺻ ���� ���ؼ��� ���� 
				checkCL(); // ������ �� ���� ���� ���� ���� 
				checkLevel(); // ������ ���� ���� �� �ӵ� ����
				
				// linePerItem �̻� �� �����ϸ� ���� ���� ���������� ������. 
				updateItemState(); 
			}
		}
	}

	private void updateItemState() {
		int temp = totalClearedLine / linePerItem;
		if(temp > itemCount) {
			itemCount = temp; // �������� ������ Ƚ�� ����
			System.out.println("itemCount: " + itemCount);
			
			// ���� ���� ���������� ���� 
			nextIsItem = true;
		}
	}

	// ����+������
	private void startItemMode_pvp() {
		while (true) {
			// --------------------------------------------------------- �� �� ����, ���� �� ���� 
			
			// �� ����� ������ �� ������, L���ڸ� ���̴� �� ���� �����ؾ� �Ѵ�. 
			ga.spawnBlock(nba.getNextBlock());
			
			// ���� �� ǥ�� 
			if (nextIsItem) {
				nba.setIsItem(true);
				nba.updateNextItem(); // ���������� (�� �ε��� ������Ʈ)
				
				// nba�� ga�� ������ L���ڰ� �ٴ� ��ġ�� �����ϵ���  
				if(nba.getBlockIndex() == 11) {
					ga.setRandomIndex(nba.getRandIndex());
				}
				
			}else {
				nba.updateNextBlock(); // �Ϲ� ������
			}

			// --------------------------------------------------------- ��ĭ�� ������
			while (ga.moveBlockDown()) {
				score++;
				gf.updateScore(score, userID);

				try {
					Thread.sleep(interval);
				} catch (InterruptedException ex) {
					System.out.println("Thread Interrupted...");
					return;
				}
				
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}

			// --------------------------------------------------------- ���� ���� Ȯ��
			if (ga.isBlockOutOfBounds()) {
				gf.interrupt_Opp(userID);
				JOptionPane.showMessageDialog(null, (3 - userID) + " Player Win!");
				gf.setVisible(false);
				Tetris.showStartup();
				break;
			}
	
			// ---------------------------------------------------------- ���� ���� �ٴ��̳� �ٸ� ���� ���� ��� 
			
			// ------------------------------------------ ���� ���� �������� ��� 
			if (curIsItem) {
				ga.twinkleItem();
				ga.execItemFunction();

				// ���� ���� �����ۿ� ���� ���� �ο� 
				if(ga.getOLCflag()) {
					score += 1;
					gf.updateScore(score);
					System.out.println("score after OLC item: " + score);
					totalClearedLine += 1;
					
					ga.setOLCflag(false);
				}

				ga.setIsItem(false);
				curIsItem = false; // �÷��� ������Ʈ
				
				continue; // �����ۿ� ���ؼ��� ����, ����, �ӵ�, ���� �� ��ȭ ����.
			} 
			// ------------------------------------------ ���� ���� �⺻ ���� ��� 
			else { 
				ga.moveBlockToBackground();
				
				// ���� ���� ������� ��ȯ�� �Ŀ� ���� ���� �������̸� 
				if(nextIsItem) {
					// ���� ���� �ٽ� �⺻ ������ ǥ��! 
					nextIsItem = false;
					nba.setIsItem(false);
					
					// ���� ������ �� ����! 
					curIsItem = true;
					ga.setIsItem(true);
					
					// ���⼭ ���� ���� �������� �ε����� ��������� L���ڸ� �׸� �� ����.
					ga.setBlockIndex(nba.getBlockIndex());
				}
				
				// --------------------------------------------------------- �ϼ��� �� ����, ���� �� ��������
				int curCL = ga.clearLines_pvp();
				gf.getAttackLines(userID);

				// --------------------------------------------------------- �� ������ ���� ����, ����, ���� �ӵ�
				
				if (curCL == 1) { // �� �� ����
					score += curCL;
					gf.updateScore(score);
					totalClearedLine += curCL;

				} else if (curCL >= 2) { // �� �� �̻� ����
					score += (10 * curCL); // ���ʽ� ����
					gf.updateScore(score);
					totalClearedLine += curCL;
				}
				
				int lvl = totalClearedLine / linePerLevel + 1;
				if (lvl > level) {
					// ���� ����
					level = lvl;
					gf.updateScore(level, userID);
					
					// ������ ���� �ӵ� ��� 
					if (interval > 300) {
						interval -= speedupPerLevel;

						score += (10 * level); // ���ʽ� ����
						gf.updateScore(score);
					}
				}
				
				// �� ������ ���� ������ �� ���� 
				updateItemState();
			}
		}
	}
	
	// ����+�ð�����
	private void startTimeAttackMode_pvp() {
		gf.displayTime(userID);
		
		while (true) {
			// --------------------------------------------------------- ���ο� �� ����
			ga.spawnBlock(nba.getNextBlock());
			nba.updateNextBlock();

			// --------------------------------------------------------- ��ĭ�� �� ������
			while (ga.moveBlockDown() && time > 0) {
				score++; // ���� ���� 
				gf.updateScore(score, userID);
				
				time--; // �ð� ���� 
				gf.updateTime(time, userID);

				try {
					Thread.sleep(interval);
				} catch (InterruptedException ex) {
					System.out.println("Thread Interrupted...");
					return;
				}
				
				// �ߴ�Ű �������� ���� ���鼭 ���
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}

			// --------------------------------------------------------- ���� ���� Ȯ��
			
			// TODO: ���ǹ��� ���ϸ� userID�� 1�� ���� ������ٵ�??? 
			if (ga.isBlockOutOfBounds() || (time <= 0 && userID == 1)) {
				gf.interrupt_Opp(userID);
				JOptionPane.showMessageDialog(null, (3 - userID) + " Player Win!");
				gf.setVisible(false);
				Tetris.showStartup();
				break;
			}
			
			// --------------------------------------------------------- ������� ���̵� / �ϼ��� �� ���� / ���� �� ��������
			ga.saveBackground();
			ga.moveBlockToBackground();
			
			int curCL = ga.clearLines_pvp();
			gf.getAttackLines(userID);
		
			// --------------------------------------------------------- �� ������ ���� ����, ����, ���� �ӵ� ������Ʈ
			
			if (curCL == 1) { // �� �� ����
				score += curCL;
				gf.updateScore(score);
				totalClearedLine += curCL;

			} else if (curCL >= 2) { // �� �� �̻� ����
				score += (10 * curCL); // ���ʽ� ����
				gf.updateScore(score);
				totalClearedLine += curCL;
			}
			
			int lvl = totalClearedLine / linePerLevel + 1;
			if (lvl > level) {
				// ���� ����
				level = lvl;
				gf.updateScore(level, userID); // �� �κи� �ٸ�. 
				
				// ������ ���� �ӵ� ��� 
				if (interval > 300) {
					interval -= speedupPerLevel;

					score += (10 * level); // ���ʽ� ����
					gf.updateScore(score);
				}
			}
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
		gf.updateScore(score, userID);
	}

	public void scorePlus15_pvp() {
		score += 15;
		gf.updateScore(score, userID);
	}
}