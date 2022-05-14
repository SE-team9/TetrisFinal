package tetris;

import javax.swing.JOptionPane;

import form.GameForm;

public class GameThread extends Thread {
	private GameForm gf;
	private GameArea ga;
	private NextBlockArea nba;

	private int score = 0; // 한 단위씩 계속 증가
	private int level = 1;
	private int linePerLevel = 7; // 줄 삭제에 따른 레벨 상승

	private int interval = 1000;
	private int speedupPerLevel; // 레벨에 따른 속도 상승

	private boolean isPaused = false;
	private int levelMode; // 설정 화면에서 정한 게임 난이도
	private int gameMode; // 시작 화면에서 정한 게임 모드 (일반, 아이템)

	// 아이템과 관련된 변수들
	private int clearedLineNum; // 현재 삭제된 줄 수
	private int totalClearedLine; // 삭제된 줄 수 누적해서 저장
	private boolean nextIsItem = false; // 다음 블럭이 아이템인지 확인
	private boolean isItem = false; // 현재 블럭이 아이템인지 확인
	private int linePerItem = 10; // 10개 줄이 삭제될 때마다 아이템 생성

	// ------------------------------ 대전모드용 변수들
	private int userNum;
	private AttackLineArea ala;

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		initVariables();

		// 점수, 레벨 텍스트 초기화
		gf.updateScore(score);
		gf.updateLevel(level);
	}

	// 대전모드
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
	public void run() { // 게임 스레드 실행

		switch (gameMode) {
		case 0:
			startDefaultMode(); // 일반+일반
			break;
		case 1:
			startItemMode(); // 일반+아이템
			break;
		case 2:
			startDefaultMode_pvp(); // 대전+일반
			break;
		case 3:
			startItemMode_pvp(); // 대전+아이템
			break;
		case 4:
			// 타임어택모드
			break;
		}
	}

	// 일반 모드
	private void startDefaultMode() {
		while (true) {
			ga.spawnBlock(); // 새 블럭 생성
			ga.updateNextBlock(); // 난이도에 따라 I형 블럭의 생성 확률 조절
			nba.updateNBA(ga.getNextBlock()); // 다음 블럭 표시

			// 블럭이 위쪽 경계를 넘지 않으면 계속 낙하
			// 함수로 대체하면 스레드가 완전히 종료되지 않는 것 같아요 ㅠㅠ 
			// 게임 실행 후 esc로 게임을 중단하고 재시작하면 점수 부분이 이전 스레드에도 영향을 받아서 변경돼요..
			while (ga.moveBlockDown()) {
				try {
					score++; // 한 단위씩 계속 증가
					gf.updateScore(score);

					// ----------------------------------------------------- 코드 설명 부탁
					// 0.1초마다 중단 키가 눌렸는지 확인?:
					// 전에는 키 입력에 대한 지연이 있다고 느꼈는데 0.1초마다 확인하지 않아도 문제 없네요! 지웠습니다.

					Thread.sleep(interval);

					// 눌렸으면 루프 돌면서 대기
					while (isPaused) {
						if (!isPaused) {
							break;
						}
					}

					// ------------------------------------------------------

				} catch (InterruptedException ex) {
					return; // 게임 스레드 종료
				}
			}

			// 경계를 넘으면 게임 종료
			if (ga.isBlockOutOfBounds()) {
				// 현재 유저 정보를 파일에 저장하고 스코어보드 띄우기
				Tetris.gameOver(gameMode, score, levelMode);
				break;
			}

			// 바닥까지 내려온 블럭은 배경으로 전환
			ga.moveBlockToBackground();

			// 삭제된 줄 수에 따라 점수 갱신
			checkCL();

			// 레벨에 따라 점수 및 속도 갱신
			checkLevel();
		}
	}

	// 보너스 점수 획득 기준
	// 1. 두 줄 이상 삭제한 경우 -> checkCL
	// 2. 레벨에 따라 낙하 속도가 상승한 경우 -> checkLevel

	private void checkCL() {
		int curCL = ga.clearLines();
		System.out.println("curCL: " + curCL);

		// 삭제된 줄이 없으면 점수 갱신 X

		if (curCL == 1) { // 한 줄 삭제
			score += curCL;
			gf.updateScore(score);
			System.out.println("score after 1 line clear: " + score);

			totalClearedLine += curCL;

		} else if (curCL >= 2) { // 두 줄 이상 삭제
			score += (10 * curCL); // 보너스 점수
			gf.updateScore(score);
			System.out.println("score after 2 line clear: " + score);

			totalClearedLine += curCL;
		}
	}

	// 각 레벨마다 삭제한 줄 수의 총합이 linePerLevel 이상인 경우 레벨 증가!
	private void checkLevel() {
		int lvl = totalClearedLine / linePerLevel + 1;
		if (lvl > level) {
			System.out.println("total CL: " + totalClearedLine);

			level = lvl;
			gf.updateLevel(level); // 레벨 갱신

			// 레벨에 따른 속도 상승
			if (interval > 300) {
				interval -= speedupPerLevel;

				score += (10 * level); // 보너스 점수
				gf.updateScore(score);
				System.out.println("score after level update: " + score);
			}
		}
	}

	// 블럭이 위쪽 경계를 넘지 않으면 interval 마다 한칸씩 계속해서 낙하
	private void repeatBlockDown() {
		while (ga.moveBlockDown()) {
			try {
				score++; // 한 단위씩 계속 증가
				gf.updateScore(score);

				// ----------------------------------------------------- 코드 설명 부탁
				// 0.1초마다 중단 키가 눌렸는지 확인?:
				// 전에는 키 입력에 대한 지연이 있다고 느꼈는데 0.1초마다 확인하지 않아도 문제 없네요! 지웠습니다.

				Thread.sleep(interval);

				// 눌렸으면 루프 돌면서 대기
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}

				// ------------------------------------------------------

			} catch (InterruptedException ex) {
				return; // 게임 스레드 종료
			}
		}
	}

	// 아이템 모드
	private void startItemMode() {
		while (true) {
			ga.spawnBlock();

			if (nextIsItem) { // 다음 블럭이 아이템
				ga.updateNextItem(); // 다음 아이템 블럭 설정
				nba.setIsItem(true); // 아이템은 원형으로 표시하기 위해 아이템 블럭임을 알려주는 용도
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

			// 게임 종료 확인
			if (ga.isBlockOutOfBounds()) {
				Tetris.gameOver(gameMode, score, levelMode);

				break; // 루프 탈출
			}

			// 현재 블럭이 아이템이면 아이템을 반짝거리고 해당 아이템의 동작을 수행한다.
			if (isItem) {
				ga.twinkleItem();
				ga.itemFunction();

				// 이제 현재 블럭이 기본 블럭임을 나타내기 위해 불린값 조정
				ga.setIsItem(false);
				isItem = false;

			} else { // 현재 블럭이 아이템이 아니면 현재 블럭을 배경으로 옮긴다.
				ga.moveBlockToBackground();

				// 다음 블럭이 아이템이었다면 이제 현재 블럭이 아이템이 되고, 다음 블럭은 기본 블럭이 되어야 하므로,
				// 현재 블럭은 원형으로, 다음 블럭은 사각형으로 표시하기 위해 각 불린값들을 조정해준다.
				if (nextIsItem) {
					nextIsItem = false; // 다음 블럭은 기본 블럭
					isItem = true; // 현재 블럭은 아이템
					nba.setIsItem(false); // 다음 블럭은 아이템이 아님
					ga.setIsItem(true); // 현재블럭은 아이템
				}
			}

			// 현재 블럭이 바닥에 닿았을 때, 완성된 줄을 삭제하고, 삭제된 줄 수 저장
			// clearedLineNum = ga.clearLines() + ga.oneLineDelte();
			clearedLineNum = ga.clearLines();

			// 줄이 특정 횟수 삭제되면 아이템 생성
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

	// 대전+일반
	private void startDefaultMode_pvp() {
		while (true) {
			// --------------------------------------------------------- 새로운 블럭생성
			ga.spawnBlock();
			ga.updateNextBlock();
			nba.updateNBA(ga.getNextBlock());

			// --------------------------------------------------------- 한 칸씩 블럭 내리기
			while (ga.moveBlockDown()) {
				score++;
				gf.updateScore(score, userNum);

				try {

					Thread.sleep(interval);

					// 눌렸으면 루프 돌면서 대기
					while (isPaused) {
						if (!isPaused) {
							break;
						}

					}
				} catch (InterruptedException ex) {
					return;
				}
			}

			// --------------------------------------------------------- 게임종료확인
			if (ga.isBlockOutOfBounds()) {
				JOptionPane.showMessageDialog(null, "Game Over!");
				gf.setVisible(false);
				Tetris.showStartup();
				gf.interrupt_Opp(userNum);
				break;
			}
			// --------------------------------------------------------- 배경으로 블럭이동 / 완성된 줄
			// 삭제

			ga.saveBackground();
			ga.moveBlockToBackground();
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- 상대보드로 삭제된 줄 이동

			// --------------------------------------------------------- 줄 삭제에 따른 점수,레벨,낙하속도
			// 업데이트
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

	// 대전+아이템
	private void startItemMode_pvp() {
		while (true) {
			// --------------------------------------------------------- 새로운 블럭생성
			ga.spawnBlock();

			if (nextIsItem) {
				ga.updateNextItem();
				nba.setIsItem(true);
			} else {
				ga.updateNextBlock();
			}
			nba.updateNBA(ga.getNextBlock());

			// --------------------------------------------------------- 한 칸씩 블럭 내리기
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

			// --------------------------------------------------------- 게임종료확인
			if (ga.isBlockOutOfBounds()) {
				JOptionPane.showMessageDialog(null, "Game Over!");
				gf.setVisible(false);
				Tetris.showStartup();
				gf.interrupt_Opp(userNum);
				break;
			}

			if (isItem) {
				// --------------------------------------------------------- 현재 블럭이 아이템블럭: 아이템
				// 동작 수행
				ga.twinkleItem();
				ga.itemFunction();
				ga.setIsItem(false);
				isItem = false;

			} else {
				// --------------------------------------------------------- 현재 블럭이 기본블럭 : 배경으로
				// 블럭이동 / 다음이 아이템 블럭인지 확인
				ga.saveBackground();
				ga.moveBlockToBackground();

				// 다음 블럭이 아이템이었다면 이제 현재 블럭이 아이템이 되고, 다음 블럭은 기본 블럭이 되어야 하므로,
				// 현재 블럭은 원형으로, 다음 블럭은 사각형으로 표시하기 위해 각 불린값들을 조정해준다.
				if (nextIsItem) {
					nextIsItem = false;
					nba.setIsItem(false);
					isItem = true;
					ga.setIsItem(true);
				}
			}

			// --------------------------------------------------------- 완성된 줄 삭제
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- 줄 삭제에 따른 아이템 발생여부
			// 업데이트
			if (totalClearedLine / linePerItem != (totalClearedLine + clearedLineNum) / linePerItem) {
				nextIsItem = true;
			}
			totalClearedLine += clearedLineNum;

			// --------------------------------------------------------- 줄 삭제에 따른 점수,레벨,낙하속도
			// 업데이트
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