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
	
	// 0�̸� �Ϲ� ���, 1�̸� ������ ��� 
	private JLabel[] lblArrow = { new JLabel("<"), new JLabel(">") };
	private JLabel[] lblGameMode = new JLabel[5];
	private int curGameMode; 

	// ���� �޴�, ���� ȭ��, ���ھ� ����, ���� ���� 
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
	
	// �ٸ� ������ �� form�� ��� �� �� �Լ��� ũ�� �ʱ�ȭ
	public void initComponents(int w, int h) {
		// ��� ���� �� ������Ʈ
		this.w = w;
		this.h = h;
		
		initThisFrame();
		initLable();
		initButtons();
	}
	
	// todo: �������� ȭ�� ũ�� ����
	private void initThisFrame() {
		this.setSize(w, h);
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null); // ������ â�� ����� ����� ����.
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
	
	// up-down���� �޴� ����, right-left�� ���� ��� ����
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

	// ������ �޴��� ���� ȭ�� ��ȯ
	private void selectMenu(int curPos) {
		switch (curPos) {
		case 0:
			this.setVisible(false);
			if (curGameMode < 2) {
				Tetris.start(); // �Ϲݸ�� ���� ����
			} else {
				Tetris.start_pvp(); // ������� ���� ����
			}
			break;
		case 1:
			this.setVisible(false);
			Tetris.showOption(); // ���� ȭ�� 
			break;
		case 2:
			this.setVisible(false);
			Tetris.showLeaderboard(); // ���ھ� ����
			break;
		case 3:
			System.exit(0); // ���� ����
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

	// ���� ȭ�鿡�� �����ߴ� ���� ��带, ���� ���� �� �����Ͽ� ���ھ�忡 ������ �� �ֵ���
	public int getCurrentGameMode() {
		return curGameMode;
	}
	
	public void setCurrentGameMode(int n) {
		curGameMode = n;
	}
	
	//test�� ���� �Լ�
	public int getCurPos() {return curPos;}
	public int getCurGameMode() {return curGameMode;}

	// StartupForm ������ ����
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}
}