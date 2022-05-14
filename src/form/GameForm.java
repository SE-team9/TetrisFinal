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
				if (!isPaused) {
					ga.moveBlockDown();
				}
			}
		});

		// TODO: Ű �Է¿� ���� �ѹ��� �������� 15�� �����ϵ��� 
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

	// ���� ������ ����
	public void startGame() {
		// ������ �ٽ� ���۵� ������ �ʱ�ȭ �Ǿ�� �ϴ� �͵��� �ʱ�ȭ�Ѵ�. 
		ga.initGameArea(); 
		nba.initNextBlockArea(); 
		
		// ���� ������ ����
		gt = new GameThread(this, ga, nba);
		gt.start();
	}

	public void updateScore(int score) {
		lblScore.setText("Score: " + score);
	}

	public void updateLevel(int level) {
		lblLevel.setText("Level: " + level);
	}
	
	// GameForm ������ ����
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}

}