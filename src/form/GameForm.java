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
	
	// ------------------------------ �������� ������
	private GameArea ga2;
	private GameThread gt2;
	private NextBlockArea nba2;
	private JLabel lblScore2, lblLevel2;
	private JTextArea keyManual2;
	private AttackLineArea ala;
	private AttackLineArea ala2;

	// ó���� ������ ȣ���� ���� ��� �⺻ ������ 
	public GameForm(int w, int h) {
		this.w = w;
		this.h = h;
		initComponents(w, h);
		initControls(0); // ���� Ű ���� 
	}
	
	// Tetris���� ���� ���� ���� ���� ���� ũ�� ���� 
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
	
	// ������忡 �ʿ��� �������� �ʱ�ȭ�ϰ� �����ӿ� �߰�
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
	
	// �������
	private void initThisFrame_pvp() {
		this.setSize(w * 2, h); // �������� ���� ���� 2��
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
	
	// �������
	private void initDisplay_pvp() {
		// ���� �÷��̾� ����, ����
		lblScore = new JLabel("Score: 0");
		lblLevel = new JLabel("Level: 0");
		lblScore.setBounds(w*11/15, h / 3, 100, 30);
		lblLevel.setBounds(w*11/15, h / 3 + 20, 100, 30);
		this.add(lblScore);
		this.add(lblLevel);

		// ������ �÷��̾� ����, ����
		lblScore2 = new JLabel("Score: 0");
		lblLevel2 = new JLabel("Level: 0");
		lblScore2.setBounds(w*11/15 + w, h / 3, 100, 30);
		lblLevel2.setBounds(w*11/15 + w, h / 3 + 20, 100, 30);
		this.add(lblScore2);
		this.add(lblLevel2);
	}

	// Tetris���� ���� ���� ���� ���� ���� ���� Ű �����ϱ�
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

			keyManual = new JTextArea(" ���� �̵�: �� \n"
					+ " ������ �̵�: �� \n"
					+ " ��ĭ �Ʒ��� �̵�: �� \n"
					+ " �� ȸ��: �� \n"
					+ " �ѹ��� ������ �̵�: SPACE \n"
					+ " ���� ����/�簳: q \n"
					+ " ���� ����: e  \n");
		}
		else {
			im.clear(); // �ٸ� Ű��忡�� �����ߴ� �� �ʱ�ȭ
			
			im.put(KeyStroke.getKeyStroke("D"), "right");
			im.put(KeyStroke.getKeyStroke("A"), "left");
			im.put(KeyStroke.getKeyStroke("W"), "up");
			im.put(KeyStroke.getKeyStroke("S"), "downOneLine");
			im.put(KeyStroke.getKeyStroke("ENTER"), "downToEnd");
			
			keyManual = new JTextArea(" ���� �̵�: a \n"
					+ " ������ �̵�: d \n"
					+ " ��ĭ �Ʒ��� �̵�: s \n"
					+ " �� ȸ��: w \n"
					+ " �ѹ��� ������ �̵�: ENTER \n"
					+ " ���� ����/�簳: q \n"
					+ " ���� ����: e  \n");
		}
		
		// ���� (����, ����, �ڷΰ���)
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

		// TODO: Ű �Է¿� ���� ��ĭ ������ ���� ������ 1�� �����ϵ��� 
		am.put("downOneLine", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isPaused && ga.moveBlockDown()) {
					gt.scorePlus1();
				}
			}
		});

		// TODO: Ű �Է¿� ���� �ѹ��� �������� 15�� �����ϵ��� 
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
				gt.interrupt(); // ���� ������ ���� 

				setVisible(false);
				Tetris.showStartup();

			}
		});
		
		am.put("back", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // ���� ������ ���� 

				setVisible(false);
				Tetris.showStartup();
			}
		});
	}

	// ������� Ű ���ε�
	public void initControls_pvp() {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();

		im.clear();

		im.put(KeyStroke.getKeyStroke("D"), "right_1");
		im.put(KeyStroke.getKeyStroke("A"), "left_1");
		im.put(KeyStroke.getKeyStroke("W"), "up_1");
		im.put(KeyStroke.getKeyStroke("S"), "downOneLine_1");
		im.put(KeyStroke.getKeyStroke("SPACE"), "downToEnd_1");

		keyManual = new JTextArea(" ���� �̵�: a \n" + 
								  " ������ �̵�: d \n" + 
								  " ��ĭ �Ʒ��� �̵�: s \n" + 
								  " �� ȸ��: w \n" + 
								  " �ѹ��� ������ �̵�: SPACE \n" + 
								  " ���� ����/�簳: q \n" + 
								  " ���� ����: e  \n");

		im.put(KeyStroke.getKeyStroke("RIGHT"), "right_2");
		im.put(KeyStroke.getKeyStroke("LEFT"), "left_2");
		im.put(KeyStroke.getKeyStroke("UP"), "up_2");
		im.put(KeyStroke.getKeyStroke("DOWN"), "downOneLine_2");
		im.put(KeyStroke.getKeyStroke("ENTER"), "downToEnd_2");

		keyManual2 = new JTextArea(" ���� �̵�: �� \n" + 
								   " ������ �̵�: �� \n" + 
								   " ��ĭ �Ʒ��� �̵�: �� \n" + 
								   " �� ȸ��: �� \n" + 
								   " �ѹ��� ������ �̵�: ENTER \n" + 
								   " ���� ����/�簳: q \n" + 
								   " ���� ����: e  \n");

		// ���� (����, ����, �ڷΰ���)
		im.put(KeyStroke.getKeyStroke("Q"), "quit");
		im.put(KeyStroke.getKeyStroke("E"), "exit");
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "back");

		// ���� �÷��̾� Ű�Ŵ���
		keyManual.setBounds(w / 30, h - 300, 160, 130);
		keyManual.setFocusable(false);
		this.add(keyManual);

		// ������ �÷��̾� Ű�Ŵ���
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
		// TODO: Ű �Է¿� ���� ��ĭ ������ ���� ������ 1�� �����ϵ���
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
		// TODO: Ű �Է¿� ���� ��ĭ ������ ���� ������ 1�� �����ϵ���
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
				gt.interrupt(); // ���� ������ ����
				gt2.interrupt();

				setVisible(false);
				Tetris.showStartup();
			}
		});

		am.put("back", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gt.interrupt(); // ���� ������ ����
				gt2.interrupt();

				setVisible(false);
				Tetris.showStartup();
			}
		});
	}
	
	// ���� ������ ����
	public void startGame() {
		// ������ �ٽ� ���۵� ������ �ʱ�ȭ �Ǿ�� �ϴ� �͵��� �ʱ�ȭ�Ѵ�. 
		ga.initGameArea(); 
		nba.initNextBlockArea(); 
		
		// ���� ������ ����
		gt = new GameThread(this, ga, nba);
		gt.start();
	}
	
	// ������� ���� ������ ����
	public void startGame_pvp() {
		// ������ �ٽ� ���۵� ������ �ʱ�ȭ �Ǿ�� �ϴ� �͵��� �ʱ�ȭ�Ѵ�.
		ga.initGameArea_pvp();
		ga2.initGameArea_pvp();
		
		nba.initNextBlockArea();
		nba2.initNextBlockArea();

		// ����� ��� ������ Ȯ���ؾ� �ϹǷ�, �μ��� �����ؼ� ������ �Ҵ� ���ش�.
		ga.setOpponent_bg(ga2.getBackgroundArray());
		ga2.setOpponent_bg(ga.getBackgroundArray());
		
		// �ڽ��� ��� ������ Ȯ���ؾ� �ϹǷ�, �μ��� �����ؼ� ������ �Ҵ� ���ش�.
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
	
	// --------------------------------------------------- ������带 ���� �Լ�
	// userNum�� ���� �ش��ϴ� �÷��̾��� ���� ������Ʈ
	public void updateScore(int score, int userNum) {
		if (userNum == 1) {
			lblScore.setText("Score: " + score);
		} else {
			lblScore2.setText("Score: " + score);
		}
	}

	// userNum�� ���� �ش��ϴ� �÷��̾��� ���� ������Ʈ
	public void updateLevel(int level, int userNum) {
		if (userNum == 1) {
			lblLevel.setText("Score: " + level);
		} else {
			lblLevel2.setText("Score: " + level);
		}
	}

	// ������ ���� ��� ��� �÷��̾��� �����带 �����Ű�� ���� �Լ�
	public void interrupt_Opp(int userNum) {
		if (userNum == 1) {
			gt2.interrupt();
		} else {
			gt.interrupt();
		}
	}

	// ������ ���� ��뿡�� �Ѱܼ� ������ ��, ����� ���, ���ݹ��� ���� �ٽ� �׷��ֱ� ���� �Լ� 
	public void repaint_attackLines(int userNum) {
		if (userNum == 1) {
			ga2.repaint();
			ala2.updateAttackLines();
		} else {
			ga.repaint();
			ala.updateAttackLines();
		}
	}
	
	// GameForm ������ ����
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}

}