package tetris;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import form.GameForm;
import form.LeaderboardForm;
import form.OptionForm;
import form.StartupForm;

public class Tetris {
	private static int w, h;
	private static StartupForm sf;
	private static GameForm gf;
	private static OptionForm of;
	private static LeaderboardForm lf;
	
	private static void updateFrameSize() {
		try {
			File file = new File("settings.txt");
			if(!file.exists()) { 
				file.createNewFile(); 
				System.out.println("Create new file.");
			};
			
			FileInputStream fis = new FileInputStream(file);
			int data = fis.read();
			
			// 파일에 저장된 값에 따라 크기 조절 
			if(data == 0) {
				w = 600;
				h = 460;
			}else if(data == 1) {
				w = 720;
				h = 540;
			}else {
				w = 840;
				h = 620;
			}
			
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 시작 화면 띄우기 
	public static void showStartup() {
		updateFrameSize(); // 멤버 변수 업데이트 
		
		sf.getContentPane().removeAll();
		sf.initComponents(w, h); // 멤버 변수 값에 따라 컴포넌트 위치 조정 
		sf.setVisible(true);
		sf.getContentPane().repaint();
	}
	
	// 게임 화면 띄우면서 스레드 시작 
	public static void start() {
		updateFrameSize();
		
		gf.getContentPane().removeAll();
		gf.initComponents(w, h);
		gf.initControls(of.getCurrentKeyMode()); // 조작 키 설정
		gf.setVisible(true);
		gf.getContentPane().repaint();
		
		gf.startGame(); // 게임 스레드 시작 
	}
	
	// 대전모드
	public static void start_pvp() {
		updateFrameSize();
		
		gf.getContentPane().removeAll();
		gf.initComponents_pvp(w, h); 	
		gf.initControls_pvp();   		
		gf.setVisible(true); 		
		gf.getContentPane().repaint();

		gf.startGame_pvp(); // 게임 스레드 시작
	}
	
	// 설정 화면 띄우기 
	public static void showOption() {
		updateFrameSize();
		
		of.getContentPane().removeAll();
		of.initComponents(w, h); // 확정된 칼럼 값으로 보여주기
		of.setVisible(true);
		of.getContentPane().repaint();
	}
	
	// 스코어보드 띄우기  
	public static void showLeaderboard() {
		updateFrameSize();
		
		lf.getContentPane().removeAll();
		lf.initComponents(w, h);
		lf.updateTableWithMode(0); // 일반 모드 먼저 보여주기 
		lf.setVisible(true);
		lf.getContentPane().repaint();
	}
	
	// 게임 모드
	public static int getGameMode() {
		if(sf == null) return 0;
		return sf.getCurrentGameMode();
	}
	
	// 조작 키 
	public static int getKeyMode() {
		if(of == null) return 0;
		return of.getCurrentKeyMode();
	}
	
	// 게임 난이도
	public static int getGameLevel() {
		if(of == null) return 0;
		return of.getCurrentGameLevel();
	}
	
	// 색상 모드
	public static int getColorMode() {
		if (of == null) return 0;
		return of.getCurrentColorMode();
	}

	// 게임 종료 (현재 모드, 이름, 점수, 난이도)
	public static void gameOver(int mode, int score, int levelMode) {
		// 유저 이름 입력 받기
		String name = JOptionPane.showInputDialog("Game Over!\n Please enter your name.");
		gf.setVisible(false);
		
		// 테이블에 데이터 추가
		switch(levelMode) {
		case 0:
			lf.addPlayer(mode, name, score, "Easy");
			break;
		case 1:
			lf.addPlayer(mode, name, score, "Normal");
			break;
		case 2:
			lf.addPlayer(mode, name, score, "Hard");
			break;
		}
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				// 파일에서 불러온 값에 따라 모든 프레임의 크기 조절 
				updateFrameSize();
				
				sf = new StartupForm(w, h);
				of = new OptionForm(w, h); 
				gf = new GameForm(w, h);
				lf = new LeaderboardForm(w, h);
				
				sf.setVisible(true);
			}
		});
	}
}