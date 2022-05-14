package tetris;

import javax.swing.JOptionPane;

import form.GameForm;

public class GameThread extends Thread {
	private GameForm gf;
	private GameArea ga;
	private NextBlockArea nba;

	private int score = 0; // �� ������ ��� ����
	private int level = 1;
	private int linePerLevel = 7; // �� ������ ���� ���� ���

	private int interval = 1000;
	private int speedupPerLevel; // ������ ���� �ӵ� ���

	private boolean isPaused = false;
	private int levelMode; // ���� ȭ�鿡�� ���� ���� ���̵�
	private int gameMode; // ���� ȭ�鿡�� ���� ���� ��� (�Ϲ�, ������)

	// �����۰� ���õ� ������
	private int clearedLineNum; // ���� ������ �� ��
	private int totalClearedLine; // ������ �� �� �����ؼ� ����
	private boolean nextIsItem = false; // ���� ���� ���������� Ȯ��
	private boolean isItem = false; // ���� ���� ���������� Ȯ��
	private int linePerItem = 10; // 10�� ���� ������ ������ ������ ����

	// ------------------------------ �������� ������
	private int userNum;
	private AttackLineArea ala;

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		initVariables();

		// ����, ���� �ؽ�Ʈ �ʱ�ȭ
		gf.updateScore(score);
		gf.updateLevel(level);
	}

	// �������
	public GameThread(GameArea ga, GameForm gf, NextBlockArea nba, AttackLineArea ala, int userNum) {
		this.ga = ga;
		this.gf = gf;
		this.nba = nba;
		this.ala = ala;
		this.userNum = userNum;

		initVariables();

		gf.updateScore(score, userNum);
		gf.updateLevel(level, userNum);
	}

	public void initVariables() {
		score = 0;
		level = 1;
		linePerLevel = 7;
		interval = 1000;
		isPaused = false;

		levelMode = Tetris.getGameLevel();
		gameMode = Tetris.getGameMode();

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
			startDefaultMode(); // �Ϲ�+�Ϲ�
			break;
		case 1:
			startItemMode(); // �Ϲ�+������
			break;
		case 2:
			startDefaultMode_pvp(); // ����+�Ϲ�
			break;
		case 3:
			startItemMode_pvp(); // ����+������
			break;
		case 4:
			// Ÿ�Ӿ��ø��
			break;
		}
	}

	// �Ϲ� ���
	private void startDefaultMode() {
		while (true) {
			ga.spawnBlock(); // �� �� ����
			ga.updateNextBlock(); // ���̵��� ���� I�� ���� ���� Ȯ�� ����
			nba.updateNBA(ga.getNextBlock()); // ���� �� ǥ��

			// ���� ���� ��踦 ���� ������ ��� ����
			// �Լ��� ��ü�ϸ� �����尡 ������ ������� �ʴ� �� ���ƿ� �Ф� 
			// ���� ���� �� esc�� ������ �ߴ��ϰ� ������ϸ� ���� �κ��� ���� �����忡�� ������ �޾Ƽ� ����ſ�..
			while (ga.moveBlockDown()) {
				try {
					score++; // �� ������ ��� ����
					gf.updateScore(score);

					// ----------------------------------------------------- �ڵ� ���� ��Ź
					// 0.1�ʸ��� �ߴ� Ű�� ���ȴ��� Ȯ��?:
					// ������ Ű �Է¿� ���� ������ �ִٰ� �����µ� 0.1�ʸ��� Ȯ������ �ʾƵ� ���� ���׿�! �������ϴ�.

					Thread.sleep(interval);

					// �������� ���� ���鼭 ���
					while (isPaused) {
						if (!isPaused) {
							break;
						}
					}

					// ------------------------------------------------------

				} catch (InterruptedException ex) {
					return; // ���� ������ ����
				}
			}

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
				// 0.1�ʸ��� �ߴ� Ű�� ���ȴ��� Ȯ��?:
				// ������ Ű �Է¿� ���� ������ �ִٰ� �����µ� 0.1�ʸ��� Ȯ������ �ʾƵ� ���� ���׿�! �������ϴ�.

				Thread.sleep(interval);

				// �������� ���� ���鼭 ���
				while (isPaused) {
					if (!isPaused) {
						break;
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

			if (nextIsItem) { // ���� ���� ������
				ga.updateNextItem(); // ���� ������ �� ����
				nba.setIsItem(true); // �������� �������� ǥ���ϱ� ���� ������ ������ �˷��ִ� �뵵
			} else {
				ga.updateNextBlock();
			}

			nba.updateNBA(ga.getNextBlock());

			while (ga.moveBlockDown()) {

				try {
					score++;
					gf.updateScore(score);

					Thread.sleep(interval);

					while (isPaused) {
						if (!isPaused) {
							break;
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
					nextIsItem = false; // ���� ���� �⺻ ��
					isItem = true; // ���� ���� ������
					nba.setIsItem(false); // ���� ���� �������� �ƴ�
					ga.setIsItem(true); // ������� ������
				}
			}

			// ���� ���� �ٴڿ� ����� ��, �ϼ��� ���� �����ϰ�, ������ �� �� ����
			// clearedLineNum = ga.clearLines() + ga.oneLineDelte();
			clearedLineNum = ga.clearLines();

			// ���� Ư�� Ƚ�� �����Ǹ� ������ ����
			if (totalClearedLine / linePerItem != (totalClearedLine + clearedLineNum) / linePerItem) {
				nextIsItem = true;
			}

			totalClearedLine += clearedLineNum;

			if (clearedLineNum > 1) {
				score += 2 * clearedLineNum + level;
			} else {
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

	// ����+�Ϲ�
	private void startDefaultMode_pvp() {
		while (true) {
			// --------------------------------------------------------- ���ο� ������
			ga.spawnBlock();
			ga.updateNextBlock();
			nba.updateNBA(ga.getNextBlock());

			// --------------------------------------------------------- �� ĭ�� �� ������
			while (ga.moveBlockDown()) {
				score++;
				gf.updateScore(score, userNum);

				try {

					Thread.sleep(interval);

					// �������� ���� ���鼭 ���
					while (isPaused) {
						if (!isPaused) {
							break;
						}

					}
				} catch (InterruptedException ex) {
					return;
				}
			}

			// --------------------------------------------------------- ��������Ȯ��
			if (ga.isBlockOutOfBounds()) {
				JOptionPane.showMessageDialog(null, "Game Over!");
				gf.setVisible(false);
				Tetris.showStartup();
				gf.interrupt_Opp(userNum);
				break;
			}
			// --------------------------------------------------------- ������� ���̵� / �ϼ��� ��
			// ����

			ga.saveBackground();
			ga.moveBlockToBackground();
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- ��뺸��� ������ �� �̵�

			// --------------------------------------------------------- �� ������ ���� ����,����,���ϼӵ�
			// ������Ʈ
			if (clearedLineNum > 1) {
				score += 2 * clearedLineNum + level;
			} else {
				score += clearedLineNum + level;
			}
			gf.updateScore(score, userNum);

			int lvl = totalClearedLine / linePerLevel + 1;
			if (lvl > level) {
				level = lvl;
				gf.updateScore(level, userNum);

				if (interval > 300) {
					interval -= speedupPerLevel;
				}
			}
		}
	}

	// ����+������
	private void startItemMode_pvp() {
		while (true) {
			// --------------------------------------------------------- ���ο� ������
			ga.spawnBlock();

			if (nextIsItem) {
				ga.updateNextItem();
				nba.setIsItem(true);
			} else {
				ga.updateNextBlock();
			}
			nba.updateNBA(ga.getNextBlock());

			// --------------------------------------------------------- �� ĭ�� �� ������
			while (ga.moveBlockDown()) {
				score++;
				gf.updateScore(score, userNum);

				try {

					Thread.sleep(interval);

					while (isPaused) {
						if (!isPaused) {
							break;
						}

					}
				} catch (InterruptedException ex) {
					return;
				}
			}

			// --------------------------------------------------------- ��������Ȯ��
			if (ga.isBlockOutOfBounds()) {
				JOptionPane.showMessageDialog(null, "Game Over!");
				gf.setVisible(false);
				Tetris.showStartup();
				gf.interrupt_Opp(userNum);
				break;
			}

			if (isItem) {
				// --------------------------------------------------------- ���� ���� �����ۺ�: ������
				// ���� ����
				ga.twinkleItem();
				ga.itemFunction();
				ga.setIsItem(false);
				isItem = false;

			} else {
				// --------------------------------------------------------- ���� ���� �⺻�� : �������
				// ���̵� / ������ ������ ������ Ȯ��
				ga.saveBackground();
				ga.moveBlockToBackground();

				// ���� ���� �������̾��ٸ� ���� ���� ���� �������� �ǰ�, ���� ���� �⺻ ���� �Ǿ�� �ϹǷ�,
				// ���� ���� ��������, ���� ���� �簢������ ǥ���ϱ� ���� �� �Ҹ������� �������ش�.
				if (nextIsItem) {
					nextIsItem = false;
					nba.setIsItem(false);
					isItem = true;
					ga.setIsItem(true);
				}
			}

			// --------------------------------------------------------- �ϼ��� �� ����
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- �� ������ ���� ������ �߻�����
			// ������Ʈ
			if (totalClearedLine / linePerItem != (totalClearedLine + clearedLineNum) / linePerItem) {
				nextIsItem = true;
			}
			totalClearedLine += clearedLineNum;

			// --------------------------------------------------------- �� ������ ���� ����,����,���ϼӵ�
			// ������Ʈ
			if (clearedLineNum > 1) {
				score += 2 * clearedLineNum + level;
			} else {
				score += clearedLineNum + level;
			}
			gf.updateScore(score, userNum);

			int lvl = totalClearedLine / linePerLevel + 1;
			if (lvl > level) {
				level = lvl;
				gf.updateScore(level, userNum);

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
		gf.updateScore(score, userNum);
	}

	public void scorePlus15_pvp() {
		score += 15;
		gf.updateScore(score, userNum);
	}
}