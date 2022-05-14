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
			
			// ���Ͽ� ����� ���� ���� ũ�� ���� 
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
	
	// ���� ȭ�� ���� 
	public static void showStartup() {
		updateFrameSize(); // ��� ���� ������Ʈ 
		
		sf.getContentPane().removeAll();
		sf.initComponents(w, h); // ��� ���� ���� ���� ������Ʈ ��ġ ���� 
		sf.setVisible(true);
		sf.getContentPane().repaint();
	}
	
	// ���� ȭ�� ���鼭 ������ ���� 
	public static void start() {
		updateFrameSize();
		
		gf.getContentPane().removeAll();
		gf.initComponents(w, h);
		gf.initControls(of.getCurrentKeyMode()); // ���� Ű ����
		gf.setVisible(true);
		gf.getContentPane().repaint();
		
		gf.startGame(); // ���� ������ ���� 
	}
	
	// �������
	public static void start_pvp() {
		updateFrameSize();
		
		gf.getContentPane().removeAll();
		gf.initComponents_pvp(w, h); 	
		gf.initControls_pvp();   		
		gf.setVisible(true); 		
		gf.getContentPane().repaint();

		gf.startGame_pvp(); // ���� ������ ����
	}
	
	// ���� ȭ�� ���� 
	public static void showOption() {
		updateFrameSize();
		
		of.getContentPane().removeAll();
		of.initComponents(w, h); // Ȯ���� Į�� ������ �����ֱ�
		of.setVisible(true);
		of.getContentPane().repaint();
	}
	
	// ���ھ�� ����  
	public static void showLeaderboard() {
		updateFrameSize();
		
		lf.getContentPane().removeAll();
		lf.initComponents(w, h);
		lf.updateTableWithMode(0); // �Ϲ� ��� ���� �����ֱ� 
		lf.setVisible(true);
		lf.getContentPane().repaint();
	}
	
	// ���� ���
	public static int getGameMode() {
		if(sf == null) return 0;
		return sf.getCurrentGameMode();
	}
	
	// ���� Ű 
	public static int getKeyMode() {
		if(of == null) return 0;
		return of.getCurrentKeyMode();
	}
	
	// ���� ���̵�
	public static int getGameLevel() {
		if(of == null) return 0;
		return of.getCurrentGameLevel();
	}
	
	// ���� ���
	public static int getColorMode() {
		if (of == null) return 0;
		return of.getCurrentColorMode();
	}

	// ���� ���� (���� ���, �̸�, ����, ���̵�)
	public static void gameOver(int mode, int score, int levelMode) {
		// ���� �̸� �Է� �ޱ�
		String name = JOptionPane.showInputDialog("Game Over!\n Please enter your name.");
		gf.setVisible(false);
		
		// ���̺� ������ �߰�
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
				
				// ���Ͽ��� �ҷ��� ���� ���� ��� �������� ũ�� ���� 
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