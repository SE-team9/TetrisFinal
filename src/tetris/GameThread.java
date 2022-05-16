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
	private int totalClearedLine; 	    // ������ ���� �� �����ؼ� ���� 
	private boolean nextIsItem = false; // ���� ���� ���������� Ȯ�� 
	private boolean curIsItem = false; 	// ���� ���� ���������� Ȯ�� 
	
	private int itemCount = 0; // �������� ������ Ƚ�� ī���� 
	private static final int linePerItem = 2; // �� ������ ���� ������ ���� 

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
	
	// ������ ��� 
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
			repeatBlockDown(); // �� ������ ���� ���� 
	
			// ��踦 ������ ���� ���� 
			if (ga.isBlockOutOfBounds()) {
				// ���� ���� ������ ���Ͽ� �����ϰ� ���ھ�� ���� 
				Tetris.gameOver(gameMode, score, levelMode);
				break;
			}
	
			// ���� �� ������ ���� �ٸ� ���� ���� 
			checkBlockState();
			
			checkCL(); // ������ �� ���� ���� ���� ���� 
			checkLevel(); // ������ ���� ���� �� �ӵ� ���� 
			
			// linePerItem �̻� �� �����ϸ�, ���� ���� ���������� �����صα� 
			checkItem();
		}
	}
	
	private void checkBlockState() {
		if(!curIsItem) { // �Ϲ� �� 
			ga.moveBlockToBackground();
		}else { 
			// ������ 
			ga.twinkleItem();
			ga.execItemFunction();

			// ���� ���� ���� �÷��� �ʱ�ȭ    
			ga.setIsItem(false);
			curIsItem = false;
		}
		
		// 3�� �̻� ������ �� ������ nextIsItem ���� ���� ���� ����! 
		if (nextIsItem) {
			nextIsItem = false;
			nba.setIsItem(false);
			
			curIsItem = true; 
			ga.setIsItem(true); // ���� ������ ���� 
		}
	}

	// linePerItem �̻� �� �����ϸ�, ���� ���� ���������� ����
	private void checkItem() {
		int temp = totalClearedLine / linePerItem;
		if(temp > itemCount) {
			itemCount = temp; // �������� ������ Ƚ�� ���� 
			
			System.out.println("itemCount: " + itemCount);
			nextIsItem = true; // ���� ������ ������ ���� 
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

	// ���� ���� ��踦 ���� ������ interval ���� ��ĭ�� ��� ���� 
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
	
	public void scorePlus10() {
		score += 10;
		gf.updateScore(score);
	}
}