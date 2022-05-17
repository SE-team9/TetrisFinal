package form;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import tetris.AttackLineArea;
import tetris.GameArea;
import tetris.GameThread;
import tetris.NextBlockArea;
import tetris.Tetris;

public class GameForm extends JFrame {
	private int w, h;
	
	private GameArea ga;
	private GameThread gt;
	private NextBlockArea nba;
	private JLabel lblScore, lblLevel;
	private JTextArea keyManual;
	private boolean isPaused = false;
	
	// ------------------------------ 대전모드용 변수들
	private GameArea ga2;
	private GameThread gt2;
	private NextBlockArea nba2;
	private JLabel lblScore2, lblLevel2;
	private JTextArea keyManual2;
	private AttackLineArea ala;
	private AttackLineArea ala2;
	private JLabel lblTime;

	public GameForm(int w, int h) { // 객체 생성 시 크기 설정 
		this.w = w;
		this.h = h;
		initComponents(w, h);
		initControls(0); // 조작 키 설정 
	}

	public void initComponents(int w, int h) { // 화면 띄울 때 크기 설정 
		this.w = w;
		this.h = h;
		
		initThisFrame();
		initDisplay();
		
		ga = new GameArea(w, h, 10);
		this.add(ga);

		nba = new NextBlockArea(w, h, ga);
		this.add(nba);
	}
	
	// 대전모드에 필요한 변수들을 초기화하고 프레임에 추가
	public void initComponents_pvp(int w, int h) {
		this.w = w;
		this.h = h;

		initThisFrame_pvp();
		initDisplay_pvp();

		ga = new GameArea(w, h, 10, 0);
		ga2 = new GameArea(w, h, 10, w);
		this.add(ga);
		this.add(ga2);

		nba = new NextBlockArea(w, h, ga, 0);
		nba2 = new NextBlockArea(w, h, ga2, w);
		this.add(nba);
		this.add(nba2);

		ala = new AttackLineArea(w, h, 0);
		ala2 = new AttackLineArea(w, h, w);
		this.add(ala);
		this.add(ala2);
	}
	

	private void initThisFrame() {
		this.setSize(w, h);
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
	}
	
	// 대전모드
	private void initThisFrame_pvp() {
		this.setSize(w * 2, h); // 대전모드는 가로 길이 2배
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
	
	// 대전모드
	private void initDisplay_pvp() {
		// 왼쪽 플레이어 점수, 레벨
		lblScore = new JLabel("Score: 0");
		lblLevel = new JLabel("Level: 0");
		lblScore.setBounds(w*11/15, h / 3, 100, 30);
		lblLevel.setBounds(w*11/15, h / 3 + 20, 100, 30);
		this.add(lblScore);
		this.add(lblLevel);

		// 오른쪽 플레이어 점수, 레벨
		lblScore2 = new JLabel("Score: 0");
		lblLevel2 = new JLabel("Level: 0");
		lblScore2.setBounds(w*11/15 + w, h / 3, 100, 30);
		lblLevel2.setBounds(w*11/15 + w, h / 3 + 20, 100, 30);
		this.add(lblScore2);
		this.add(lblLevel2);
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
				if (!isPaused && ga.moveBlockDown()) {
					gt.scorePlus1();
				}
			}
		});

		// TODO: 키 입력에 의해 한번에 떨어지면 15점 증가하도록 
		am.put("downToEnd", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused && ga.checkBottom()) {
					ga.dropBlock();
					gt.scorePlus15();
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

	// 대전모드 키 바인딩
	public void initControls_pvp() {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();

		im.clear();

		im.put(KeyStroke.getKeyStroke("D"), "right_1");
		im.put(KeyStroke.getKeyStroke("A"), "left_1");
		im.put(KeyStroke.getKeyStroke("W"), "up_1");
		im.put(KeyStroke.getKeyStroke("S"), "downOneLine_1");
		im.put(KeyStroke.getKeyStroke("SPACE"), "downToEnd_1");

		keyManual = new JTextArea(" 왼쪽 이동: a \n" + 
								  " 오른쪽 이동: d \n" + 
								  " 한칸 아래로 이동: s \n" + 
								  " 블럭 회전: w \n" + 
								  " 한번에 밑으로 이동: SPACE \n" + 
								  " 게임 정지/재개: q \n" + 
								  " 게임 종료: e  \n");

		im.put(KeyStroke.getKeyStroke("RIGHT"), "right_2");
		im.put(KeyStroke.getKeyStroke("LEFT"), "left_2");
		im.put(KeyStroke.getKeyStroke("UP"), "up_2");
		im.put(KeyStroke.getKeyStroke("DOWN"), "downOneLine_2");
		im.put(KeyStroke.getKeyStroke("ENTER"), "downToEnd_2");

		keyManual2 = new JTextArea(" 왼쪽 이동: ← \n" + 
								   " 오른쪽 이동: → \n" + 
								   " 한칸 아래로 이동: ↓ \n" + 
								   " 블럭 회전: ↑ \n" + 
								   " 한번에 밑으로 이동: ENTER \n" + 
								   " 게임 정지/재개: q \n" + 
								   " 게임 종료: e  \n");

		// 공통 (중지, 종료, 뒤로가기)
		im.put(KeyStroke.getKeyStroke("Q"), "quit");
		im.put(KeyStroke.getKeyStroke("E"), "exit");
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "back");

		// 왼쪽 플레이어 키매뉴얼
		keyManual.setBounds(w / 30, h - 300, 160, 130);
		keyManual.setFocusable(false);
		this.add(keyManual);

		// 오른쪽 플레이어 키매뉴얼
		keyManual2.setBounds(w / 30 + w, h - 300, 160, 130);
		keyManual2.setFocusable(false);
		this.add(keyManual2);

		am.put("right_1", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.moveBlockRight();
			}
		});
		am.put("right_2", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga2.moveBlockRight();
			}
		});

		am.put("left_1", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.moveBlockLeft();
			}
		});
		am.put("left_2", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga2.moveBlockLeft();
			}
		});
		am.put("up_1", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga.rotateBlock();
			}
		});
		am.put("up_2", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused)
					ga2.rotateBlock();
			}
		});
		// TODO: 키 입력에 의해 한칸 내려갈 때도 점수가 1점 증가하도록
		am.put("downOneLine_1", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					if (ga.moveBlockDown()) {
						gt.scorePlus1_pvp();
					}
				}
			}
		});
		// TODO: 키 입력에 의해 한칸 내려갈 때도 점수가 1점 증가하도록
		am.put("downOneLine_2", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					if (ga2.moveBlockDown()) {
						gt2.scorePlus1_pvp();
					}
				}
			}
		});
		am.put("downToEnd_1", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused && ga.checkBottom()) {
					ga.dropBlock();
					gt.scorePlus15_pvp();
				}
			}
		});
		am.put("downToEnd_2", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused && ga2.checkBottom()) {
					ga2.dropBlock();
					gt2.scorePlus15_pvp();
				}
			}
		});

		am.put("quit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused) {
					isPaused = true;
					gt.pause();
					gt2.pause();
				} else {
					isPaused = false;
					gt.reStart();
					gt2.reStart();
				}
			}
		});

		am.put("exit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // 게임 스레드 종료
				gt2.interrupt();

				setVisible(false);
				Tetris.showStartup();
			}
		});

		am.put("back", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // 게임 스레드 종료
				gt2.interrupt();

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
	
	// 대전모드 게임 스레드 시작
	public void startGame_pvp() {
		// 게임이 다시 시작될 때마다 초기화 되어야 하는 것들을 초기화한다.
		ga.initGameArea_pvp();
		ga2.initGameArea_pvp();
		
		nba.initNextBlockArea();
		nba2.initNextBlockArea();

		// 상대의 배경 정보를 확인해야 하므로, 인수로 전달해서 변수에 할당 해준다.
		ga.setOpponent_bg(ga2.getBackgroundArray());
		ga2.setOpponent_bg(ga.getBackgroundArray());
		
		// 자신의 배경 정보를 확인해야 하므로, 인수로 전달해서 변수에 할당 해준다.
		ala.set_bg(ga.getBackgroundArray());
		ala2.set_bg(ga2.getBackgroundArray());
		
		gt = new GameThread(ga, this, nba, ala, 1);
		gt2 = new GameThread(ga2, this, nba2, ala2, 2);
		
		gt.start();
		gt2.start();
	}

	public void updateScore(int score) {
		lblScore.setText("Score: " + score);
	}

	public void updateLevel(int level) {
		lblLevel.setText("Level: " + level);
	}
	
	// --------------------------------------------------- 대전모드를 위한 함수
	
	// 타임어택모드 시간표시, 어차피 시간은 공유되므로 플레이어1일 때만 해당 함수가 호출된다.
	public void displayTime(int userNum) {
		if(userNum == 1) {
			lblTime = new JLabel("Time: 100");
			lblTime.setBounds(w-w/20, h / 60, 100, 30);
			this.add(lblTime);
		}
	}
	
	// 시간 업데이트 
	// 어차피 남은 시간은 공유되므로, 플레이어1일 때만 시간을 업데이트 한다.
	public void updateTime(int time, int userNum) {
		if(userNum == 1) {
			lblTime.setText("Time: " + time);
		}
	}
	
	// userNum에 따라 해당하는 플레이어의 점수 업데이트
	public void updateScore(int score, int userNum) {
		if (userNum == 1) {
			lblScore.setText("Score: " + score);
		} else {
			lblScore2.setText("Score: " + score);
		}
	}

	// userNum에 따라 해당하는 플레이어의 레벨 업데이트
	public void updateLevel(int level, int userNum) {
		if (userNum == 1) {
			lblLevel.setText("Level: " + level);
		} else {
			lblLevel2.setText("Level: " + level);
		}
	}

	// 게임이 끝난 경우 상대 플레이어의 스레드를 종료시키기 위한 함수
	public void interrupt_Opp(int userNum) {
		if (userNum == 1) {
			gt2.interrupt();
		} else {
			gt.interrupt();
		}
	}

	// 삭제된 줄을 상대에게 넘겨서 공격한 후, 상대의 배경, 공격받은 줄을 다시 그려주기 위한 함수 
	public void repaint_attackLines(int userNum) {
		if (userNum == 1) {
			ga2.repaint();
			ala2.updateAttackLines();
		} else {
			ga.repaint();
			ala.updateAttackLines();
		}
	}
	
	// GameForm 프레임 실행
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}

}
