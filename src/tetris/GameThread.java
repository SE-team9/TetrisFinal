package tetris;
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

	// �����۰� ���õ� ������
	private int clearedLineNum; 	    // ���� ������ �� ��
	private int totalClearedLine; 	    // ������ �� �� �����ؼ� ���� 
	private boolean nextIsItem = false; // ���� ���� ���������� Ȯ�� 
	private boolean isItem = false; 	// ���� ���� ���������� Ȯ�� 

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		// ����, ���� �ؽ�Ʈ �ʱ�ȭ 
		gf.updateScore(score);
		gf.updateLevel(level);

		levelMode = Tetris.getGameLevel();
		gameMode = Tetris.getGameMode();
		
		// �������� ���� ���� ��忡 ���� �ð� ���� ���� 
		if (levelMode == 0) {        
			speedupPerLevel = 80;  // easy
		} else if (levelMode == 1) { 
			speedupPerLevel = 100; // normal
		}else {                      
			speedupPerLevel = 120; // hard
		}
	}

	@Override
	public void run() { // ���� ������ ���� 
		if(gameMode == 0) {
			startDefaultMode(); // �Ϲ� ���
		}else {
			startItemMode();    // ������ ���
		}
	}

	// �Ϲ� ��� 
	private void startDefaultMode() {
		while (true) {
			ga.spawnBlock(); // �� �� ���� 
			ga.updateNextBlock(); // ���̵��� ���� I�� ���� ���� Ȯ�� ���� 
			nba.updateNBA(ga.getNextBlock()); // ���� �� ǥ�� 

			// ���� ���� ��踦 ���� ������ ��� ���� 
			repeatBlockDown(); // �� ������ ���� ���� 
			
			// ��踦 ������ ���� ���� 
			if (ga.isBlockOutOfBounds()) {
				// ���� ���� ������ ���Ͽ� �����ϰ� ���ھ�� ���� 
				Tetris.gameOver(gameMode, score, levelMode);
				break;
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

	// �� �������� ������ �� ���� ������ linePerLevel �̻��� ��� ���� ����!
	private void checkLevel() {
		int lvl = totalClearedLine / linePerLevel + 1;
		if (lvl > level) {
			System.out.println("total CL: " + totalClearedLine);
			
			level = lvl;
			gf.updateLevel(level); // ���� ���� 
			
			// ������ ���� �ӵ� ��� 
			if (interval > 300) {
				interval -= speedupPerLevel;
				
				score += (10 * level); // ���ʽ� ���� 
				gf.updateScore(score);
				System.out.println("score after level update: " + score);
			}
		}
	}

	// ���� ���� ��踦 ���� ������ interval ���� ��ĭ�� ����ؼ� ���� 
	private void repeatBlockDown() {
		while (ga.moveBlockDown()) {
			try {
				score++; // �� ������ ��� ���� 
				gf.updateScore(score);
				
				// ----------------------------------------------------- �ڵ� ���� ��Ź 
				int i = 0;
				while (i < interval / 100) { // interval ���� ��ĭ�� ���ϸ� �ϴµ� 
					Thread.sleep(100); // �׿� ���ÿ� 0.1�ʸ��� �ߴ� Ű�� ���ȴ��� Ȯ��? 
					i++;
					
					// �������� ���� ���鼭 ���
					while (isPaused) {
						if (!isPaused) {
							break;
						}
					}
				}
				// ------------------------------------------------------

			} catch (InterruptedException ex) {
				return; // ���� ������ ����
			}
		}
	}

	// ������ ��� 
	private void startItemMode() {
		while (true) {
			ga.spawnBlock();

			if (nextIsItem) { 		// ���� ���� ������
				ga.updateNextItem(); 	// ���� ������ �� ����
				nba.setIsItem(true); 	// �������� �������� ǥ���ϱ� ���� ������ ������ �˷��ִ� �뵵
			} else {
				ga.updateNextBlock(); 	
			}

			nba.updateNBA(ga.getNextBlock()); 

			while (ga.moveBlockDown()) {

				try {
					score++;
					gf.updateScore(score);

					int i = 0;
					while (i < interval / 100) {
						Thread.sleep(100);
						i++;
						while (isPaused) {
							if (!isPaused) {
								break;
							}
						}
					}

				} catch (InterruptedException ex) {
					return;
				}
			}

			// ���� ���� Ȯ��
			if (ga.isBlockOutOfBounds()) {
				Tetris.gameOver(gameMode, score, levelMode);
				
				break; // ���� Ż��
			}

			// ���� ���� �������̸� �������� ��¦�Ÿ��� �ش� �������� ������ �����Ѵ�.
			if (isItem) {
				ga.twinkleItem();
				ga.itemFunction();

				// ���� ���� ���� �⺻ ������ ��Ÿ���� ���� �Ҹ��� ����
				ga.setIsItem(false);
				isItem = false;

			} else { // ���� ���� �������� �ƴϸ� ���� ���� ������� �ű��.
				ga.moveBlockToBackground();

				// ���� ���� �������̾��ٸ� ���� ���� ���� �������� �ǰ�, ���� ���� �⺻ ���� �Ǿ�� �ϹǷ�,
				// ���� ���� ��������, ���� ���� �簢������ ǥ���ϱ� ���� �� �Ҹ������� �������ش�.
				if (nextIsItem) {
					nextIsItem = false; 	// ���� ���� �⺻ ��
					isItem = true; 			// ���� ���� ������
					nba.setIsItem(false);		// ���� ���� �������� �ƴ�
					ga.setIsItem(true); 		// ������� ������
				}
			}

			// ���� ���� �ٴڿ� ����� ��, �ϼ��� ���� �����ϰ�, ������ �� �� ����
			//clearedLineNum = ga.clearLines() + ga.oneLineDelte();
			clearedLineNum = ga.clearLines();

			// ���� Ư�� Ƚ�� �����Ǹ� ������ ����
			// 3�� 10���� ��ġ�� 10���� ������ ������ �������� �����˴ϴ�.
			// ������ ���� Ȯ���ϱ� ���� 3�� ���� �������� �������� 3���� �����ص׽��ϴ�.
			if (totalClearedLine / 3 != (totalClearedLine + clearedLineNum) / 3) {
				nextIsItem = true;
			}

			totalClearedLine += clearedLineNum;

			if(clearedLineNum > 1) {
				score += 2* clearedLineNum + level;
			}
			else {
				score += clearedLineNum + level;
			}
			gf.updateScore(score);

			int lvl = totalClearedLine / linePerLevel + 1;
			if (lvl > level) {
				level = lvl;
				gf.updateLevel(level);
				if (interval > 300) {
					interval -= speedupPerLevel;
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
	
//	public void scorePlus1() {
//		score++;
//		gf.updateScore(score);
//	}
//	
//	public void scorePlus15() {
//		score+=15;
//		gf.updateScore(score);
//	}
}