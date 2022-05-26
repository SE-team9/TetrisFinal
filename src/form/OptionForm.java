package form;
import tetris.*;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

public class OptionForm extends JFrame {
	private static final int ROW = 6;
	private JLabel[] lblOption = new JLabel[ROW]; 
	private String[] options = { "화면 크기", "조작 키", "난이도", "색맹 모드", "스코어보드 기록 초기화", "기본 설정으로 되돌리기"};
	private JLabel[] lblArrow = { new JLabel("<"), new JLabel(">") };
	private JButton[] btnOption = new JButton[ROW]; // 따로 초기화 해줘야 널 포인터 에러 안 뜸. 
	private String[][] optionArray = {
		 { "Small (default)", "Medium", "Large" }, // 화면 크기
		 { "←, →, ↓, ↑, SPACE, q, e", "a, d, s, w, ENTER, q, e"}, // 조작 키 
		 { "Easy", "Normal", "Hard" }, // 난이도
		 { "NO", "YES" }, // 색맹 모드
		 { "NO", "YES" }, // 점수 기록 초기화
		 { "NO", "YES" } // 기본 설정으로 되돌리기
	};
	
	private int w, h; // 이 프레임의 크기 
	
	// 화살표 키 입력에 따라 위아래로 행 이동 
	private int row = 0;
	
	// 각 행마다 어떤 열에 포커스가 놓여 있는지 배열에 저장하기 
	private int[] focusColumn = new int[ROW];
	
	// 엔터 눌러서 확정된 칼럼 값을 저장 (다른 곳에서 참조 가능)
	private int[] confirmedColumn = new int[ROW];
	
	// 파일에 저장된 값에 따라 프레임 크기 설정, 나머지 모두 초기화 
	public OptionForm(int w, int h) { 
		this.w = w;
		this.h = h;
		
		try {
			File file = new File("settings.txt");
			if(!file.exists()) { 
				file.createNewFile(); 
				System.out.println("Create new file.");
			};
			
			FileInputStream fis = new FileInputStream(file);
			int data = 0;
			int idx = 0;
			
			// 파일에서 가져온 설정 값으로 confirmedColumn 배열 초기화
			while((data = fis.read()) != -1) {
				confirmedColumn[idx] = data;
				idx++;
			}
			
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 프레임 크기 조절 
		if(confirmedColumn[0] == 0) {
			updateFrameSize(600, 460);
		}else if(confirmedColumn[0] == 1) {
			updateFrameSize(720, 540);
		}else {
			updateFrameSize(840, 620);
		}
		
		initControls();
	}
	
	// 프레임 크기 변경
	private void updateFrameSize(int w, int h) {
		getContentPane().removeAll(); // 이걸 안 해주면 여러 개의 프레임이 겹침.
		
		// 프레임 크기 바꾸고, 컴포넌트 전부 다시 그리기
		initComponents(w, h);
	
		getContentPane().repaint();
	}
	
	public void initComponents(int w, int h) {
		// 프레임 크기 변경 
		this.w = w;
		this.h = h;
		this.setSize(w, h);
		
		// 프레임 초기화 
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
		
		for(int i = 0; i < ROW; i++) {
			// 레이블 초기화 
			lblOption[i] = new JLabel(options[i]);
			lblOption[i].setBounds(w/4, h/30 + i * 60, w/2, 25);
			lblOption[i].setHorizontalAlignment(JLabel.CENTER);
			this.add(lblOption[i]);
			
			// 버튼 초기화 (화면 크기 조정한 뒤의 텍스트는 현재 확정된 칼럼으로)
			btnOption[i] = new JButton(optionArray[i][confirmedColumn[i]]);
			btnOption[i].setBounds(w/4, h/30 + i * 60 + 25, w/2, 25);
			btnOption[i].setBackground(Color.white);
			btnOption[i].setFocusable(false);
			this.add(btnOption[i]);
		}
		
		// 화살표의 위치를 첫 행으로 초기화 
		row = 0; // 포커스 위치도 바꿔주기 
		lblArrow[0].setBounds(w/3, h/30, 25, 25);
		lblArrow[1].setBounds(w - w/3, h/30, 25, 25);
		this.add(lblArrow[0]);
		this.add(lblArrow[1]);
	}
	
	private void initControls() {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke("UP"), "up");
		im.put(KeyStroke.getKeyStroke("DOWN"), "down");
		im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
		im.put(KeyStroke.getKeyStroke("LEFT"), "left");
		im.put(KeyStroke.getKeyStroke("ENTER"), "enter");
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "back");

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
		am.put("back", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 설정 화면에서 벗어나 시작 화면으로 돌아갈 때, 파일에 모든 내용을 저장한다. 
				saveAllSettings();
				
				setVisible(false);
				Tetris.showStartup();
			}
		});
		
		// 엔터를 눌러야 현재 칼럼이 설정 값으로 확정된다. 
		am.put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmedColumn[row] = focusColumn[row]; // 현재 칼럼을 설정 값으로 확정 
				System.out.println(row + " " + confirmedColumn[row]); // 디버깅 용도
				
				switch(row) {
				case 0: // 화면 크기 설정 
					if(confirmedColumn[row] == 0) {
						updateFrameSize(600, 450);
						setVisible(true);
					}else if(confirmedColumn[row] == 1) {
						updateFrameSize(700, 550);
						setVisible(true);
					}else {
						updateFrameSize(800, 650);
						setVisible(true);
					}
					break;
				case 4: // 스코어보드 초기화
					if(confirmedColumn[row] == 1) {
						initScoreboard();			
					}
					break;
				case 5: // 기본 설정
					if(confirmedColumn[row] == 1) {
						// 첫번째 칼럼으로 옵션 초기화 하고, 크기 조정 
						initDefaultSettings(); 
						setVisible(true);
					}
					break;
				}
			}
		});
	}
	
	// 모든 설정을 첫번째 칼럼으로 
	private void initDefaultSettings() {
		for(int i = 0; i < ROW; i++) {
			focusColumn[i] = 0;
			confirmedColumn[i] = 0;
		}
		
		updateFrameSize(600, 450);
	}
	
	private void moveUp() {
		lblArrow[0].setVisible(false);
		lblArrow[1].setVisible(false);
		
		row--;
		if(row < 0) {
			row = ROW - 1;
		}
		
		// 현재 행에 따라 화살표의 위치와 visibility 조절
		lblArrow[0].setBounds(w/3, h/30 + row * 60, 25, 25);
		lblArrow[1].setBounds(w - w/3, h/30 + row * 60, 25, 25);
		lblArrow[0].setVisible(true);
		lblArrow[1].setVisible(true);
	}

	private void moveDown() {
		lblArrow[0].setVisible(false);
		lblArrow[1].setVisible(false);
		
		row++;
		if(row >= ROW) {
			row = 0;
		}
		
		// 현재 행에 따라 화살표의 위치와 visibility 조절
		lblArrow[0].setBounds(w/3, h/30 + row * 60, 25, 25);
		lblArrow[1].setBounds(w - w/3, h/30 + row * 60, 25, 25);
		lblArrow[0].setVisible(true);
		lblArrow[1].setVisible(true);
	}

	private void moveRight() {
		// 좌우 화살표 키 입력에 따라 포커스가 놓인 칼럼 위치 바꾸기 
		focusColumn[row]++;
		
		// 현재 행이 가질 수 있는 최대 옵션 개수를 넘으면 0으로 초기화
		if(focusColumn[row] >= optionArray[row].length) {
			focusColumn[row] = 0;
		}
		
		btnOption[row].setText(optionArray[row][focusColumn[row]]);
	}

	private void moveLeft() {
		focusColumn[row]--;
		
		if(focusColumn[row] < 0) {
			focusColumn[row] = optionArray[row].length - 1;
		}
		
		btnOption[row].setText(optionArray[row][focusColumn[row]]);
	}
	
	// 조작 키
	public int getCurrentKeyMode() {
		return confirmedColumn[1];
	}
	
	// 난이도 
	public int getCurrentGameLevel() {
		return confirmedColumn[2];
	}
	
	public void setCurrentGameLevel(int n) {
		
	}
	
	// 색맹 모드 
	public int getCurrentColorMode() {
		return confirmedColumn[3];
	}
	
	// 스코어보드 기록 초기화 (파일명으로 검색)
	private void initScoreboard() {
		File file = new File("leaderboardFile_Normal");
		File file2 = new File("leaderboardFile_Item");
		
		if(file.exists()) {
			file.delete();
		}else {
			System.out.println("File does not exist.");
		}
		
		if(file2.exists()) {
			file2.delete();
		}else {
			System.out.println("File does not exist.");
		}
	}
	
	// 확정된 칼럼 값 파일에 저장하기 
	public void saveAllSettings() {
		try {
			FileOutputStream fos = new FileOutputStream("settings.txt", false); // append 하지 않고 매번 새로 쓰기 
			
			for(int i = 0; i < confirmedColumn.length; i++) {
				fos.write(confirmedColumn[i]);
			}
	
			fos.close();
			
			System.out.print("Save: ");
			for(int i = 0; i < confirmedColumn.length; i++) {
				System.out.print(confirmedColumn[i] + " ");
			}
			System.out.println();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// OptionForm 프레임 실행
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}
}