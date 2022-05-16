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
	private int totalClearedLine; 	    // 삭제한 라인 수 누적해서 저장 
	private boolean nextIsItem = false; // 다음 블럭이 아이템인지 확인 
	private boolean curIsItem = false; 	// 현재 블럭이 아이템인지 확인 
	
	private int itemCount = 0; // 아이템이 등장한 횟수 카운팅 
	private static final int linePerItem = 2; // 줄 삭제에 따른 아이템 등장 

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
	
	// 아이템 모드 
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
			repeatBlockDown(); // 한 단위씩 점수 증가 
	
			// 경계를 넘으면 게임 종료 
			if (ga.isBlockOutOfBounds()) {
				// 현재 유저 정보를 파일에 저장하고 스코어보드 띄우기 
				Tetris.gameOver(gameMode, score, levelMode);
				break;
			}
	
			// 현재 블럭 종류에 따라 다른 동작 수행 
			checkBlockState();
			
			checkCL(); // 삭제된 줄 수에 따라 점수 갱신 
			checkLevel(); // 레벨에 따라 점수 및 속도 갱신 
			
			// linePerItem 이상 줄 삭제하면, 다음 블럭을 아이템으로 설정해두기 
			checkItem();
		}
	}
	
	private void checkBlockState() {
		if(!curIsItem) { // 일반 블럭 
			ga.moveBlockToBackground();
		}else { 
			// 아이템 
			ga.twinkleItem();
			ga.execItemFunction();

			// 현재 블럭에 대한 플래그 초기화    
			ga.setIsItem(false);
			curIsItem = false;
		}
		
		// 3줄 이상 삭제한 뒤 설정된 nextIsItem 값을 현재 블럭에 적용! 
		if (nextIsItem) {
			nextIsItem = false;
			nba.setIsItem(false);
			
			curIsItem = true; 
			ga.setIsItem(true); // 이제 아이템 등장 
		}
	}

	// linePerItem 이상 줄 삭제하면, 다음 블럭을 아이템으로 설정
	private void checkItem() {
		int temp = totalClearedLine / linePerItem;
		if(temp > itemCount) {
			itemCount = temp; // 아이템이 등장한 횟수 갱신 
			
			System.out.println("itemCount: " + itemCount);
			nextIsItem = true; // 다음 블럭으로 아이템 설정 
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

	// 블럭이 위쪽 경계를 넘지 않으면 interval 마다 한칸씩 계속 낙하 
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