package tetris;

import javax.swing.JOptionPane;

import form.GameForm;

public class GameThread extends Thread {
	private GameForm gf;
	private GameArea ga;
	private NextBlockArea nba;
	
	private int score = 0; // 한 단위씩 계속 증가 
	private int level = 1;  
	private int linePerLevel = 3; // 줄 삭제에 따른 레벨 상승 
	private int interval = 1000;
	private int speedupPerLevel; // 레벨에 따른 속도 상승 
  
	private boolean isPaused = false;
	private int levelMode; // 설정 화면에서 정한 게임 난이도
	private int gameMode; // 시작 화면에서 정한 게임 모드 (일반, 아이템)

	// 아이템 모드 
	private int totalClearedLine; 	    // 삭제한 라인 수 누적해서 저장 
	private boolean nextIsItem = false; // 다음 블럭이 아이템인지 확인 
	private boolean curIsItem = false; 	// 현재 블럭이 아이템인지 확인 
	
	private int itemCount = 0; // 아이템이 등장한 횟수 카운팅 
	private static final int linePerItem = 2; // 줄 삭제에 따른 아이템 등장
	
	// 대전 모드 
	private int userNum;
	private int time = 100;
	private AttackLineArea ala;

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		initVariables();

		// 점수, 레벨 텍스트 초기화
		gf.updateScore(score);
		gf.updateLevel(level);
		
		setGameMode();
	}

	// 대전 모드
	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba, AttackLineArea ala, int userNum) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;
		this.ala = ala;
		this.userNum = userNum;
		
		gf.updateScore(score, userNum);
		gf.updateLevel(level, userNum);
		
		setGameMode();
	}
	
	private void setGameMode() {
		// 일반 모드, 아이템 모드
		gameMode = Tetris.getGameMode();
		
		// 설정에 저장된 난이도에 따라 블럭 낙하 속도 조절 
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
	public void run() { // 게임 스레드 실행
		switch (gameMode) {
		case 0:
			startDefaultMode(); 	// 일반+일반
			break;
		case 1:
			startItemMode(); 		// 일반+아이템
			break;
		case 2:
			startDefaultMode_pvp(); // 대전+일반
			break;
		case 3:
			startItemMode_pvp(); 	// 대전+아이템
			break;
		case 4:
			startTimeAttackMode_pvp(); // 대전+시간제한
			break;
		}
	}

	private void startDefaultMode() {
		while(true) {
			ga.spawnBlock(); // 새 블럭 생성 
			ga.updateNextBlock(); // 난이도에 따라 I형 블럭의 생성 확률 조절 
			nba.updateNBA(ga.getNextBlock()); // 다음 블럭 표시 
			
			// 블럭이 위쪽 경계를 넘지 않으면 계속 낙하 
			while (ga.moveBlockDown()) {
				score++; // 한 단위씩 계속 증가 
				gf.updateScore(score);
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted...");
					return; // 게임 스레드 종료 
				}
				
				// 눌렸으면 루프 돌면서 대기
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}
			
			// 경계를 넘으면 게임 종료 
			if (ga.isBlockOutOfBounds()) {
				// 현재 유저 정보를 파일에 저장하고 스코어보드 띄우기
				Tetris.gameOver(gameMode, score, levelMode);
				
				System.out.println("Thread Interrupted...");
				return; // 게임 스레드 종료 
			}

			// 바닥까지 내려온 블럭은 배경으로 전환
			ga.moveBlockToBackground();

			// 삭제된 줄 수에 따라 점수 갱신
			checkCL();

			// 레벨에 따라 점수 및 속도 갱신
			checkLevel();
		}
	}
	
	private void startItemMode() {
		while (true) {
			ga.spawnBlock(); // 새 블럭 생성 
			
			// 다음 블럭의 모양 설정 
			if (nextIsItem) {
				nba.setIsItem(true);
				ga.updateNextItem(); // 아이템으로 
			} else {
				ga.updateNextBlock(); // 일반 블럭으로
			}
			nba.updateNBA(ga.getNextBlock()); // 다음 블럭 표시
	
			// 블럭이 위쪽 경계를 넘지 않으면 계속 낙하 
			while (ga.moveBlockDown()) { 
				score++; // 한 단위씩 계속 증가 
				gf.updateScore(score);
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted...");
					return; // 게임 스레드 종료 
				}
				
				// 눌렸으면 루프 돌면서 대기
				while (isPaused) {
					if (!isPaused) {
						break;
					}
				}
			}
	
			// 경계를 넘으면 게임 종료 
			if (ga.isBlockOutOfBounds()) {
				// 현재 유저 정보를 파일에 저장하고 스코어보드 띄우기 
				Tetris.gameOver(gameMode, score, levelMode);
				
				System.out.println("Thread Interrupted...");
				return; // 게임 스레드 종료 
			}
	
			// 현재 블럭 종류에 따라 다른 동작 수행 
			checkBlockState();
			
			checkCL(); // 삭제된 줄 수에 따라 점수 갱신 
			checkLevel(); // 레벨에 따라 점수 및 속도 갱신 
			
			// linePerItem 이상 줄 삭제하면, 다음 블럭을 아이템으로 설정하기  
			checkItem();
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

	// 각 레벨마다 linePerLevel 이상 줄 삭제하면 레벨 증가
	private void checkLevel() {
		int lvl = totalClearedLine / linePerLevel + 1;
		if (lvl > level) {
			System.out.println("total CL: " + totalClearedLine);
			
			// 레벨 갱신
			level = lvl;
			gf.updateLevel(level);
			
			// 레벨에 따른 속도 상승 
			if (interval > 300) {
				interval -= speedupPerLevel;

				score += (10 * level); // 보너스 점수
				gf.updateScore(score);
				System.out.println("score after level update: " + score);
			}
		}
	}

	private void checkBlockState() {
		// 현재 블럭이 아이템인 경우 
		if (curIsItem) {
			ga.twinkleItem();
			ga.execItemFunction();

			// 기본 블럭으로 초기화 
			ga.setItemFlag(false);
			curIsItem = false;
		} else { 
			// 현재 블럭이 기본 블럭인 경우 
			ga.moveBlockToBackground();

			// 3줄 이상 삭제해서 nextIsItem이 true가 된 경우  
			if (nextIsItem) {
				nextIsItem = false;
				nba.setIsItem(false);
				
				curIsItem = true; 
				ga.setItemFlag(true); // 이제 아이템 등장 
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
				gf.interrupt_Opp(userNum);
				Tetris.showStartup();
				break;
			}
      
			// --------------------------------------------------------- 배경으로 블럭이동 / 완성된 줄 삭제 / 상대보드로 삭제된 줄 이동
			ga.saveBackground();
			ga.moveBlockToBackground();
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- 줄 삭제에 따른 점수,레벨,낙하속도 업데이트
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
				gf.interrupt_Opp(userNum);
				Tetris.showStartup();
				break;
			}

			if (isItem) {
				// --------------------------------------------------------- 현재 블럭이 아이템블럭: 아이템 동작 수행
				ga.twinkleItem();
				ga.itemFunction();
				ga.setIsItem(false);
				isItem = false;

			} else {
				// --------------------------------------------------------- 현재 블럭이 기본블럭 : 배경으로 블럭이동 / 다음이 아이템 블럭인지 확인
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
	
	// 대전+시간제한
	private void startTimeAttackMode_pvp() {
		gf.displayTime(userNum);
		
		while (true) {
			// --------------------------------------------------------- 새로운 블럭생성
			ga.spawnBlock();
			ga.updateNextBlock();
			nba.updateNBA(ga.getNextBlock());

			// --------------------------------------------------------- 한 칸씩 블럭 내리기
			while (ga.moveBlockDown() && time>0) {
				score++;
				gf.updateScore(score, userNum);
				time--;
				gf.updateTime(time, userNum);

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
			if (ga.isBlockOutOfBounds() || (time <= 0 && userNum == 1)) {
				JOptionPane.showMessageDialog(null, "Game Over!");
				gf.setVisible(false);
				gf.interrupt_Opp(userNum);
				Tetris.showStartup();
				break;
			}
			
			// --------------------------------------------------------- 배경으로 블럭이동 / 완성된 줄 삭제 / 상대보드로 삭제된 줄 이동
			ga.saveBackground();
			ga.moveBlockToBackground();
			clearedLineNum = ga.clearLines_pvp();
			gf.repaint();
			gf.repaint_attackLines(userNum);

			// --------------------------------------------------------- 줄 삭제에 따른 점수,레벨,낙하속도 업데이트
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

	// linePerItem 이상 줄 삭제하면, 다음 블럭을 아이템으로 설정
	private void checkItem() {
		int temp = totalClearedLine / linePerItem;
		if(temp > itemCount) {
			itemCount = temp; // 아이템이 등장한 횟수 갱신 
			
			System.out.println("itemCount: " + itemCount);
			nextIsItem = true; // 다음 블럭을 아이템으로 설정 
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