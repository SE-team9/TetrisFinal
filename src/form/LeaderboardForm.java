package form;

import tetris.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class LeaderboardForm extends JFrame {
	private int w, h;
	private int position;
	private JTable leaderboard;
	private DefaultTableModel tm;
	private String[] leaderboardFile = { "leaderboardFile_Normal", "leaderboardFile_Item" };
	private TableRowSorter<TableModel> sorter;
	private JScrollPane scrollLeaderboard; // 화면 범위를 넘어갈 때 스크롤 가능하도록

	private JLabel lblGameMode;
	private String gameMode[] = { "Normal Mode", "Item Mode" };
	private JLabel[] lblArrow = { new JLabel("<"), new JLabel(">") };
	private int curMode; // 게임 모드 
	private Vector ci; // 테이블 칼럼 식별자

	public LeaderboardForm(int w, int h) {
		this.w = w;
		this.h = h;
		this.curMode = 0;
		
		initComponents(w, h);
		updateTableWithMode(curMode); 
		
		initControls();
	}

	public void initComponents(int w, int h) {
		this.w = w;
		this.h = h;

		this.setSize(w, h);
		this.setResizable(false);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(false);
		
		// 텍스트를 일반모드로 초기화 
		lblGameMode = new JLabel(gameMode[0]);

		lblGameMode.setHorizontalAlignment(JLabel.CENTER);
		lblGameMode.setBounds(w / 3, h / 30, 200, 30);
		this.add(lblGameMode);

		lblArrow[0].setBounds(w / 3 + 10, h / 30, 30, 30);
		lblArrow[1].setBounds(w - (w / 3 + 20), h / 30, 30, 30);
		this.add(lblArrow[0]);
		this.add(lblArrow[1]);
	}
  
	public void updateTableWithMode(int mode) {		
		this.curMode = mode; // 1. 모드 변경 
		initTableData(); // 2. 테이블 모델 초기화  
    
    	// highlight 위해 initTableData에서 분리
		makeLeaderboard(-1); // 3. 테이블 모델 이용해서 테이블 초기화
    
		initTableSorter(); // 4. Sorter 초기화 및 정렬 
		initScrollLeaderboard(); // 5. 스크롤 가능한 테이블 생성 
	}
	
	// Input: 현재 모드에 해당하는 파일에서 데이터 읽어와서 테이블 모델 초기화 
	private void initTableData() {
		String header[] = { "Player", "Score", "Level" };
		String contents[][] = {};

		// 테이블의 데이터를 관리하는 테이블 모델
		tm = new DefaultTableModel(contents, header) {

			@Override // 모든 셀 편집 불가능하도록
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override 
			public Class<?> getColumnClass(int columnIndex) {
        			// 점수 읽어올 때 정수 타입으로
				if (columnIndex == 1)
					return Integer.class;
				
				return super.getColumnClass(columnIndex).getClass();
			}
		};

		// 테이블의 칼럼 식별자 초기화
		ci = new Vector();
		ci.add("Player");
		ci.add("Score");
		ci.add("Level");

		try {
			// 해당 이름의 파일이 존재하지 않는 경우 새로 생성 
			File file = new File(leaderboardFile[curMode]);
			if(!file.exists()) { 
				file.createNewFile(); 
				System.out.println("Create new file.");
			};
			
			FileInputStream fs = new FileInputStream(file);
			ObjectInputStream os = new ObjectInputStream(fs);
			
			// de-serialization (직렬화 된 바이트 데이터 -> 객체 타입으로 읽어오기)
			// 여기서 데이터 읽어올 때 파일의 끝에 이르면 eof 예외 발생함. (일단 넘어가자)
			tm.setDataVector((Vector<Vector>) os.readObject(), ci);
			
			os.close();
			fs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeLeaderboard(int position) {
		// 테이블 모델을 이용하여 테이블 초기화
		leaderboard = new JTable(tm) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				JComponent component = (JComponent) super.prepareRenderer(renderer, row, column);
        
        		// 강조 표시하고 싶은 행의 인덱스를 인자로 받아오기 
				if (row == position) {
					component.setBackground(Color.YELLOW);
				} else {
					component.setBackground(Color.WHITE);
				}
				return component;
			}
		};
		leaderboard.setFocusable(false);

		// 셀의 내용 가운데 정렬
		DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
		tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = leaderboard.getColumnModel();
		for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
		}
	}
	
	// 현재 테이블에 대한 Sorter 설정 
	private void initTableSorter() {
		sorter = new TableRowSorter<>(tm);
		leaderboard.setRowSorter(sorter);

		ArrayList<SortKey> keys = new ArrayList<>();
		keys.add(new SortKey(1, SortOrder.DESCENDING)); // column index, sort order
		sorter.setSortKeys(keys);

		sorter.sort(); // 정렬 수행
	}

	// 최종적으로, 스크롤 가능한 테이블 생성!
	private void initScrollLeaderboard() {
		scrollLeaderboard = new JScrollPane(leaderboard);
		scrollLeaderboard.setBounds(w / 30, h / 10, w - 50, h - 100);
		this.add(scrollLeaderboard);
	}

	// Output: 현재 모드에 해당하는 파일 열어서 테이블 데이터 저장
	private void saveLeaderboard() {
		try {
			// 해당 이름의 파일이 존재하지 않는 경우, 새로 생성하기
			File file = new File(leaderboardFile[curMode]);			
			if(!file.exists()) { 
				System.out.println("Create new file.");
				file.createNewFile(); 
			};

			FileOutputStream fs = new FileOutputStream(file);
			ObjectOutputStream os = new ObjectOutputStream(fs);
      
			// serialization (객체 -> 바이트 데이터로 직렬화하여 파일에 저장)
			os.writeObject(tm.getDataVector());
		    
			os.close();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initControls() {
		InputMap im = this.getRootPane().getInputMap();
		ActionMap am = this.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
		im.put(KeyStroke.getKeyStroke("LEFT"), "left");
		im.put(KeyStroke.getKeyStroke("ESCAPE"), "back");

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
				setVisible(false);
				Tetris.showStartup();
			}
		});
	}

	private void moveRight() {
		this.remove(scrollLeaderboard); // 화면에서 컴포넌트 제거 
		
		curMode++;
		if (curMode > gameMode.length - 1) {
			curMode = 0;
		}
		updateTableWithMode(curMode);
		
		lblGameMode.setText(gameMode[curMode]);
	}

	private void moveLeft() {
		this.remove(scrollLeaderboard);

		curMode--;
		if (curMode < 0) {
			curMode = gameMode.length - 1;
		}
		updateTableWithMode(curMode);

		lblGameMode.setText(gameMode[curMode]);
	}
	
	// 게임 종료 후 유저 이름 입력 받아서 스코어보드 띄우기
	public void addPlayer(int mode, String name, int score, String level) {
		this.remove(scrollLeaderboard);
		
    	// 현재 모드에 대한 스코어보드 먼저 보여주기
		lblGameMode.setText(gameMode[mode]);
		
    	// 테이블을 관리하는 tm이 하나이기 때문에, 행 추가를 바로 하지 않고
		// 현재 모드에 따라 파일 입출력을 처음부터 다시 한다!
		
		this.curMode = mode; // 1. 모드 변경 
		
		initTableData(); // 2. 모드에 따른 파일 재업로드 
		
		
		
		// 유저 정보 추가
		tm.addRow(new Object[] { name, score, level });

		// sort()후에 highlight하기 위해서 필요함 -> position 값 얻어오는데 사용
		leaderboard = new JTable(tm);
		initTableSorter();
		
		position = leaderboard.convertRowIndexToView(tm.getRowCount() - 1);
		makeLeaderboard(position); // 3. 마지막 행 강조 표시 
		
		initTableSorter(); // 4. 재정렬 

		initScrollLeaderboard(); // 5. 최종 테이블 생성 
		
		saveLeaderboard(); // 파일에 새로운 데이터 저장

		// 스코어보드 보여주기
		this.setVisible(true);
	}

	// LeaderboardForm 프레임 실행
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
			}
		});
	}
}