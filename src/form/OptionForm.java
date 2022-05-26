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
	private String[] options = { "ȭ�� ũ��", "���� Ű", "���̵�", "���� ���", "���ھ�� ��� �ʱ�ȭ", "�⺻ �������� �ǵ�����"};
	private JLabel[] lblArrow = { new JLabel("<"), new JLabel(">") };
	private JButton[] btnOption = new JButton[ROW]; // ���� �ʱ�ȭ ����� �� ������ ���� �� ��. 
	private String[][] optionArray = {
		 { "Small (default)", "Medium", "Large" }, // ȭ�� ũ��
		 { "��, ��, ��, ��, SPACE, q, e", "a, d, s, w, ENTER, q, e"}, // ���� Ű 
		 { "Easy", "Normal", "Hard" }, // ���̵�
		 { "NO", "YES" }, // ���� ���
		 { "NO", "YES" }, // ���� ��� �ʱ�ȭ
		 { "NO", "YES" } // �⺻ �������� �ǵ�����
	};
	
	private int w, h; // �� �������� ũ�� 
	
	// ȭ��ǥ Ű �Է¿� ���� ���Ʒ��� �� �̵� 
	private int row = 0;
	
	// �� �ึ�� � ���� ��Ŀ���� ���� �ִ��� �迭�� �����ϱ� 
	private int[] focusColumn = new int[ROW];
	
	// ���� ������ Ȯ���� Į�� ���� ���� (�ٸ� ������ ���� ����)
	private int[] confirmedColumn = new int[ROW];
	
	// ���Ͽ� ����� ���� ���� ������ ũ�� ����, ������ ��� �ʱ�ȭ 
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
			
			// ���Ͽ��� ������ ���� ������ confirmedColumn �迭 �ʱ�ȭ
			while((data = fis.read()) != -1) {
				confirmedColumn[idx] = data;
				idx++;
			}
			
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// ������ ũ�� ���� 
		if(confirmedColumn[0] == 0) {
			updateFrameSize(600, 460);
		}else if(confirmedColumn[0] == 1) {
			updateFrameSize(720, 540);
		}else {
			updateFrameSize(840, 620);
		}
		
		initControls();
	}
	
	// ������ ũ�� ����
	private void updateFrameSize(int w, int h) {
		getContentPane().removeAll(); // �̰� �� ���ָ� ���� ���� �������� ��ħ.
		
		// ������ ũ�� �ٲٰ�, ������Ʈ ���� �ٽ� �׸���
		initComponents(w, h);
	
		getContentPane().repaint();
	}
	
	public void initComponents(int w, int h) {
		// ������ ũ�� ���� 
		this.w = w;
		this.h = h;
		this.setSize(w, h);
		
		// ������ �ʱ�ȭ 
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
		
		for(int i = 0; i < ROW; i++) {
			// ���̺� �ʱ�ȭ 
			lblOption[i] = new JLabel(options[i]);
			lblOption[i].setBounds(w/4, h/30 + i * 60, w/2, 25);
			lblOption[i].setHorizontalAlignment(JLabel.CENTER);
			this.add(lblOption[i]);
			
			// ��ư �ʱ�ȭ (ȭ�� ũ�� ������ ���� �ؽ�Ʈ�� ���� Ȯ���� Į������)
			btnOption[i] = new JButton(optionArray[i][confirmedColumn[i]]);
			btnOption[i].setBounds(w/4, h/30 + i * 60 + 25, w/2, 25);
			btnOption[i].setBackground(Color.white);
			btnOption[i].setFocusable(false);
			this.add(btnOption[i]);
		}
		
		// ȭ��ǥ�� ��ġ�� ù ������ �ʱ�ȭ 
		row = 0; // ��Ŀ�� ��ġ�� �ٲ��ֱ� 
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
				// ���� ȭ�鿡�� ��� ���� ȭ������ ���ư� ��, ���Ͽ� ��� ������ �����Ѵ�. 
				saveAllSettings();
				
				setVisible(false);
				Tetris.showStartup();
			}
		});
		
		// ���͸� ������ ���� Į���� ���� ������ Ȯ���ȴ�. 
		am.put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmedColumn[row] = focusColumn[row]; // ���� Į���� ���� ������ Ȯ�� 
				System.out.println(row + " " + confirmedColumn[row]); // ����� �뵵
				
				switch(row) {
				case 0: // ȭ�� ũ�� ���� 
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
				case 4: // ���ھ�� �ʱ�ȭ
					if(confirmedColumn[row] == 1) {
						initScoreboard();			
					}
					break;
				case 5: // �⺻ ����
					if(confirmedColumn[row] == 1) {
						// ù��° Į������ �ɼ� �ʱ�ȭ �ϰ�, ũ�� ���� 
						initDefaultSettings(); 
						setVisible(true);
					}
					break;
				}
			}
		});
	}
	
	// ��� ������ ù��° Į������ 
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
		
		// ���� �࿡ ���� ȭ��ǥ�� ��ġ�� visibility ����
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
		
		// ���� �࿡ ���� ȭ��ǥ�� ��ġ�� visibility ����
		lblArrow[0].setBounds(w/3, h/30 + row * 60, 25, 25);
		lblArrow[1].setBounds(w - w/3, h/30 + row * 60, 25, 25);
		lblArrow[0].setVisible(true);
		lblArrow[1].setVisible(true);
	}

	private void moveRight() {
		// �¿� ȭ��ǥ Ű �Է¿� ���� ��Ŀ���� ���� Į�� ��ġ �ٲٱ� 
		focusColumn[row]++;
		
		// ���� ���� ���� �� �ִ� �ִ� �ɼ� ������ ������ 0���� �ʱ�ȭ
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
	
	// ���� Ű
	public int getCurrentKeyMode() {
		return confirmedColumn[1];
	}
	
	// ���̵� 
	public int getCurrentGameLevel() {
		return confirmedColumn[2];
	}
	
	public void setCurrentGameLevel(int n) {
		
	}
	
	// ���� ��� 
	public int getCurrentColorMode() {
		return confirmedColumn[3];
	}
	
	// ���ھ�� ��� �ʱ�ȭ (���ϸ����� �˻�)
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
	
	// Ȯ���� Į�� �� ���Ͽ� �����ϱ� 
	public void saveAllSettings() {
		try {
			FileOutputStream fos = new FileOutputStream("settings.txt", false); // append ���� �ʰ� �Ź� ���� ���� 
			
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
	
	// OptionForm ������ ����
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}
}