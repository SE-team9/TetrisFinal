package tetris;
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

	// 아이템과 관련된 변수들
	private int clearedLineNum; 	    // 현재 삭제된 줄 수
	private int totalClearedLine; 	    // 삭제된 줄 수 누적해서 저장 
	private boolean nextIsItem = false; // 다음 블럭이 아이템인지 확인 
	private boolean isItem = false; 	// 현재 블럭이 아이템인지 확인 

	public GameThread(GameForm gf, GameArea ga, NextBlockArea nba) {
		this.gf = gf;
		this.ga = ga;
		this.nba = nba;

		// 점수, 레벨 텍스트 초기화 
		gf.updateScore(score);
		gf.updateLevel(level);

		levelMode = Tetris.getGameLevel();
		gameMode = Tetris.getGameMode();
		
		// 설정에서 정한 레벨 모드에 따라 시간 간격 조절 
		if (levelMode == 0) {        
			speedupPerLevel = 80;  // easy
		} else if (levelMode == 1) { 
			speedupPerLevel = 100; // normal
		}else {                      
			speedupPerLevel = 120; // hard
		}
	}

	@Override
	public void run() { // 게임 스레드 실행 
		if(gameMode == 0) {
			startDefaultMode(); // 일반 모드
		}else {
			startItemMode();    // 아이템 모드
		}
	}

	// 일반 모드 
	private void startDefaultMode() {
		while (true) {
			ga.spawnBlock(); // 새 블럭 생성 
			ga.updateNextBlock(); // 난이도에 따라 I형 블럭의 생성 확률 조절 
			nba.updateNBA(ga.getNextBlock()); // 다음 블럭 표시 

			// 블럭이 위쪽 경계를 넘지 않으면 계속 낙하 
			repeatBlockDown(); // 한 단위씩 점수 증가 
			
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
		
		if(curCL == 1) { // 한 줄 삭제 
			score += curCL;
			gf.updateScore(score);
			System.out.println("score after 1 line clear: " + score);
			
			totalClearedLine += curCL;
			
		}else if(curCL >= 2) { // 두 줄 이상 삭제 
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
				int i = 0;
				while (i < interval / 100) { // interval 마다 한칸씩 낙하를 하는데 
					Thread.sleep(100); // 그와 동시에 0.1초마다 중단 키가 눌렸는지 확인? 
					i++;
					
					// 눌렸으면 루프 돌면서 대기
					while (isPaused) {
						if (!isPaused) {
							break;
						}
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

			if (nextIsItem) { 		// 다음 블럭이 아이템
				ga.updateNextItem(); 	// 다음 아이템 블럭 설정
				nba.setIsItem(true); 	// 아이템은 원형으로 표시하기 위해 아이템 블럭임을 알려주는 용도
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
					nextIsItem = false; 	// 다음 블럭은 기본 블럭
					isItem = true; 			// 현재 블럭은 아이템
					nba.setIsItem(false);		// 다음 블럭은 아이템이 아님
					ga.setIsItem(true); 		// 현재블럭은 아이템
				}
			}

			// 현재 블럭이 바닥에 닿았을 때, 완성된 줄을 삭제하고, 삭제된 줄 수 저장
			//clearedLineNum = ga.clearLines() + ga.oneLineDelte();
			clearedLineNum = ga.clearLines();

			// 줄이 특정 횟수 삭제되면 아이템 생성
			// 3을 10으로 고치면 10줄이 삭제될 때마다 아이템이 생성됩니다.
			// 동작을 쉽게 확인하기 위해 3줄 마다 아이템이 나오도록 3으로 설정해뒀습니다.
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