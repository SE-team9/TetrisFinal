package form;
import tetris.*;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class GameForm extends JFrame {
	private int w, h;
	
	private GameArea ga;
	private GameThread gt;
	private NextBlockArea nba;
	private JLabel lblScore, lblLevel;
	private JTextArea keyManual;
	private boolean isPaused = false;

	// 처음에 생성자 호출할 때는 모두 기본 값으로 
	public GameForm(int w, int h) {
		this.w = w;
		this.h = h;
		initComponents(w, h);
		initControls(0); // 조작 키 설정 
	}
	
	// Tetris에서 전달 받은 인자 값에 따라 크기 조정 
	public void initComponents(int w, int h) {
		this.w = w;
		this.h = h;
		
		initThisFrame();
		initDisplay();
		
		ga = new GameArea(w, h, 10);
		this.add(ga);

		nba = new NextBlockArea(w, h, ga);
		this.add(nba);
	}

	private void initThisFrame() {
		this.setSize(w, h);
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
	}

	private void initDisplay() {
		lblScore = new JLabel("Score: 0");
		lblLevel = new JLabel("Level: 0");
		lblScore.setBounds(w - (w/5), h / 20, 100, 30);
		lblLevel.setBounds(w - (w/5), h / 20 + 20, 100, 30);
		this.add(lblScore);
		this.add(lblLevel);
	}

	// Tetris에서 전달 받은 인자 값에 따라 조작 키 변경하기
	public void initControls(int keyMode) {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();
	
		if(keyMode == 0) {
			im.clear();
			
			im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
			im.put(KeyStroke.getKeyStroke("LEFT"), "left");
			im.put(KeyStroke.getKeyStroke("UP"), "up");
			im.put(KeyStroke.getKeyStroke("DOWN"), "downOneLine");
			im.put(KeyStroke.getKeyStroke("SPACE"), "downToEnd");

			keyManual = new JTextArea(" 왼쪽 이동: ← \n"
					+ " 오른쪽 이동: → \n"
					+ " 한칸 아래로 이동: ↓ \n"
					+ " 블럭 회전: ↑ \n"
					+ " 한번에 밑으로 이동: SPACE \n"
					+ " 게임 정지/재개: q \n"
					+ " 게임 종료: e  \n");
		}
		else {
			im.clear(); // 다른 키모드에서 설정했던 거 초기화
			
			im.put(KeyStroke.getKeyStroke("D"), "right");
			im.put(KeyStroke.getKeyStroke("A"), "left");
			im.put(KeyStroke.getKeyStroke("W"), "up");
			im.put(KeyStroke.getKeyStroke("S"), "downOneLine");
			im.put(KeyStroke.getKeyStroke("ENTER"), "downToEnd");
			
			keyManual = new JTextArea(" 왼쪽 이동: a \n"
					+ " 오른쪽 이동: d \n"
					+ " 한칸 아래로 이동: s \n"
					+ " 블럭 회전: w \n"
					+ " 한번에 밑으로 이동: ENTER \n"
					+ " 게임 정지/재개: q \n"
					+ " 게임 종료: e  \n");
		}
		
		// 공통 (중지, 종료, 뒤로가기)
		im.put(KeyStroke.getKeyStroke("Q"), "quit");
		im.put(KeyStroke.getKeyStroke("E"), "exit");
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "back");
		
		keyManual.setBounds(w/30, h-300, 160, 130);
		keyManual.setFocusable(false);
		this.add(keyManual);
		
		am.put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.moveBlockRight();
			}
		});

		am.put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.moveBlockLeft();
			}
		});

		am.put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.rotateBlock();
			}
		});

		// TODO: 키 입력에 의해 한칸 내려갈 때도 점수가 1점 증가하도록 
		am.put("downOneLine", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					ga.moveBlockDown();
				}
			}
		});

		// TODO: 키 입력에 의해 한번에 떨어지면 15점 증가하도록 
		am.put("downToEnd", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					ga.dropBlock();
				}
			}
		});

		am.put("quit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					isPaused = true;
					gt.pause();
				} else {
					isPaused = false;
					gt.reStart();
				}
			}
		});
		
		am.put("exit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // 게임 스레드 종료 

				setVisible(false);
				Tetris.showStartup();

			}
		});
		
		am.put("back", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // 게임 스레드 종료 

				setVisible(false);
				Tetris.showStartup();
			}
		});
	}

	// 게임 스레드 시작
	public void startGame() {
		// 게임이 다시 시작될 때마다 초기화 되어야 하는 것들을 초기화한다. 
		ga.initGameArea(); 
		nba.initNextBlockArea(); 
		
		// 게임 스레드 시작
		gt = new GameThread(this, ga, nba);
		gt.start();
	}

	public void updateScore(int score) {
		lblScore.setText("Score: " + score);
	}

	public void updateLevel(int level) {
		lblLevel.setText("Level: " + level);
	}
	
	// GameForm 프레임 실행
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}

}