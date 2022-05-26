package form;
import tetris.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class StartupForm extends JFrame {
	private int w, h;
	
	private JLabel title = new JLabel("SE Team9 Tetris");
	
	// 0이면 일반 모드, 1이면 아이템 모드 
	private JLabel[] lblArrow = { new JLabel("<"), new JLabel(">") };
	private JLabel[] lblGameMode = new JLabel[5];
	private int curGameMode; 

	// 시작 메뉴, 설정 화면, 스코어 보드, 게임 종료 
	private JButton[] btnMenu = new JButton[4];
	private String[] btnText = { "Start Game", "Settings", "ScoreBoard", "Quit" };
	private String[] gameModeText = { "1P Normal Mode", "1P Item Mode", "2P Normal Mode", "2P Item Mode", "2P Time Attack Mode" };
	private int curPos;
	
	public StartupForm(int w, int h) {
		this.w = w;
		this.h = h;
		initComponents(w, h);
		initControls();
	}
	
	// 다른 곳에서 이 form을 띄울 때 이 함수로 크기 초기화
	public void initComponents(int w, int h) {
		// 멤버 변수 값 업데이트
		this.w = w;
		this.h = h;
		
		initThisFrame();
		initLable();
		initButtons();
	}
	
	// todo: 설정에서 화면 크기 선택
	private void initThisFrame() {
		this.setSize(w, h);
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null); // 프레임 창을 모니터 가운데에 띄운다.
		this.setVisible(false);
	}
	
	private void initLable() {
		title.setFont(new Font("Arial", Font.BOLD, 30));
		title.setBounds(w / 4, h / 20, w / 2, h / 6);
		title.setHorizontalAlignment(JLabel.CENTER);
		this.add(title);
		
		for (int i = 0; i < 5; i++) {
			lblGameMode[i] = new JLabel(gameModeText[i]);
			lblGameMode[i].setFont(new Font("Arial", Font.BOLD, 15));
			lblGameMode[i].setBounds(w / 3, h / 3, w / 3, h / 15);
			lblGameMode[i].setHorizontalAlignment(JLabel.CENTER);
			this.add(lblGameMode[i]);
			lblGameMode[i].setVisible(false);
		}
		lblGameMode[curGameMode].setVisible(true);
		
		lblArrow[0].setBounds(w/3 + 10, h/3, 30, 30);
		lblArrow[1].setBounds(w - (w/3 + 20), h/3, 30, 30);
		this.add(lblArrow[0]);
		this.add(lblArrow[1]);
	}

	private void initButtons() {
		for (int i = 0; i < btnMenu.length; i++) {
			btnMenu[i] = new JButton(btnText[i]);
			btnMenu[i].setBounds(w / 3, h / 3 + (h / 10) * (i + 1), w / 3, h / 15);
			btnMenu[i].setBackground(Color.white);
			btnMenu[i].setFocusable(false);
			this.add(btnMenu[i]);
		}
		btnMenu[curPos].setBackground(Color.lightGray);
	}
	
	// up-down으로 메뉴 선택, right-left로 게임 모드 선택
	private void initControls() {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke("UP"), "up");
		im.put(KeyStroke.getKeyStroke("DOWN"), "down");
		im.put(KeyStroke.getKeyStroke("ENTER"), "enter");
		im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
		im.put(KeyStroke.getKeyStroke("LEFT"), "left");
		
		am.put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveUp();
			}
		});

		am.put("down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveDown();
			}
		});

		am.put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveRight();
			}
		});

		am.put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveLeft();
			}
		});
		
		am.put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenu(curPos);
			}
		});
	}

	// 선택한 메뉴에 따라 화면 전환
	private void selectMenu(int curPos) {
		switch (curPos) {
		case 0:
			this.setVisible(false);
			if (curGameMode < 2) {
				Tetris.start(); // 일반모드 게임 시작
			} else {
				Tetris.start_pvp(); // 대전모드 게임 시작
			}
			break;
		case 1:
			this.setVisible(false);
			Tetris.showOption(); // 설정 화면 
			break;
		case 2:
			this.setVisible(false);
			Tetris.showLeaderboard(); // 스코어 보드
			break;
		case 3:
			System.exit(0); // 게임 종료
			break;
		}
	}

	public void moveUp() {
		btnMenu[curPos].setBackground(Color.white);
		curPos--;
		if (curPos < 0) {
			curPos = btnMenu.length - 1;
		}
		btnMenu[curPos].setBackground(Color.lightGray);
	}

	public void moveDown() {
		btnMenu[curPos].setBackground(Color.white);
		curPos++;
		if (curPos > btnMenu.length - 1) {
			curPos = 0;
		}
		btnMenu[curPos].setBackground(Color.lightGray);
	}

	public void moveRight() {
		lblGameMode[curGameMode].setVisible(false);
		curGameMode++;
		if (curGameMode > lblGameMode.length - 1) {
			curGameMode = 0;
		}
		lblGameMode[curGameMode].setVisible(true);
	}

	public void moveLeft() {
		lblGameMode[curGameMode].setVisible(false);
		curGameMode--;
		if (curGameMode < 0) {
			curGameMode = lblGameMode.length - 1;
		}
		lblGameMode[curGameMode].setVisible(true);
	}

	// 시작 화면에서 선택했던 게임 모드를, 게임 종료 시 참조하여 스코어보드에 보여줄 수 있도록
	public int getCurrentGameMode() {
		return curGameMode;
	}
	
	public void setCurrentGameMode(int n) {
		curGameMode = n;
	}
	
	//test를 위한 함수
	public int getCurPos() {return curPos;}
	public int getCurGameMode() {return curGameMode;}

	// StartupForm 프레임 실행
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}
}