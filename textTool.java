package TextEditor;

import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textTool extends Frame implements WindowListener {
	TextArea ta;
	TextField tf1, tf2;
	Panel pNorth, pSouth;
	Label lb1, lb2, lb3;
	Checkbox op;
	boolean optionFlag = false;

	String[] btnName = { "Undo", "짝수줄 삭제", "문자삭제", // param1에 지정된 문자들을 삭제하는 기능
			"빈줄삭제", // 빈 줄 삭제
			"접두사추가", // Param1과 Param2의 문자열을 각 라인의 앞뒤에 붙이는 기능
			"substring", // Param1과 Param2에 지정된 문자열을 각 라인에서 제거하는 기능
			"substring2", // Param1과 Param2에 지정된 문자열로 둘러싸인 부분을 남기고 제거하는 기능
			"distinct", // 중복값제거한 후 정렬해서 보여주기
			"distinct2", // 중복값제거한 후 정렬해서 보여주기 - 중복카운트 포함
			"패턴적용", // 데이터에 지정된 패턴 적용하기
			"패턴제거", // 데이터에 적용된 패턴 제거하기
			"대소문자변환", // 대소문자 변환해주기
	};

	Button[] btn = new Button[btnName.length];

	private final String CR_LF = System.getProperty("line.separator");

	private String prevText = "";

	private void registerEventHandler() {

		addWindowListener(this);

		op.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				optionFlag = !optionFlag;
				System.out.println(optionFlag);
			}

		});

		int n = 0;
		btn[n++].addActionListener(new ActionListener() { // Undo - 작업이전 상태로 되돌림
			public void actionPerformed(ActionEvent e) {

				String text = ta.getText();
				String temp = prevText;
				prevText = text;
				ta.setText(temp);

			}
		});

		btn[n++].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = ta.getText();
				StringBuffer sb = new StringBuffer(text.length());
				Scanner sc = new Scanner(text);
				String tmp = tf1.getText();
				int num;

				prevText = text;
				int count = 0;

				// 1. pram1 번창에 아무것도 없으면 짝수줄만 삭제
				if (tmp.length() == 0) {
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						if (tmp.length() == 0 && count % 2 == 0) {
							sb.append(line).append(CR_LF);
						}
						count++;
					}
				}

				// 2. 옵션이 켜져있고 텍스트필드에 숫자가있으면 그숫자 라인 빼고 삭제
				else if (optionFlag && tmp.length() != 0) {
					num = tmp.charAt(0) - '0' - 1;
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						if (count == num) {
							sb.append(line).append(CR_LF);
						}
						count++;
					}
				}
				// 3. 옵션이 꺼져있고 텍스트 필드에 숫자가있으면 그 숫자 라인만 삭제
				else if (!optionFlag && tmp.length() != 0) {
					num = tmp.charAt(0) - '0' - 1;
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						if (count != num) {
							sb.append(line).append(CR_LF);
						}
						count++;
					}
				}

				ta.setText(sb.toString());
			}

		});

		btn[n++].addActionListener(new ActionListener() { // 문자삭제 - Param1에 지정된 문자를 삭제하는 기능

			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());

				prevText = curText;

				String tmpText = tf1.getText();

				// param1에 아무것도 없으면 그냥 종료
				if ("".equals(tmpText))
					return;

				// 텍스트 필드에 모든 문자를 받아서
				for (int i = 0; i < curText.length(); i++) {
					char ch = curText.charAt(i);

					// 옵션이 꺼져있다면 param1에 있는 문자 제거
					if (!optionFlag) {
						if (tmpText.indexOf(ch) == -1)
							sb.append(ch);
						// 옵션이 켜져있다면 param1에 있는 문자만 남김
					} else if (optionFlag) {
						if (tmpText.indexOf(ch) != -1 || ch == '\n')
							sb.append(ch);
					}
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // trim - 라인의 좌우공백을 제거하는 기능
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());
				Scanner sc = new Scanner(curText);
				prevText = curText;

				while (sc.hasNextLine()) {
					sb.append(sc.nextLine().trim()).append(CR_LF);
				}

				ta.setText(sb.toString());
			}
		});

		btn[n++].addActionListener(new ActionListener() { // 접두사 - 각 라인에 접두사, 접미사 붙이기
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());
				Scanner sc = new Scanner(curText);

				prevText = curText;

				String tmp1 = tf1.getText();
				String tmp2 = tf2.getText();

				while (sc.hasNextLine()) {
					sb.append(tmp1).append(sc.nextLine()).append(tmp2).append(CR_LF);
				}

				ta.setText(sb.toString());
			}
		});

		btn[n++].addActionListener(new ActionListener() { // substring - 문자열 자르기
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length() * 2);
				Scanner sc = new Scanner(curText);
				prevText = curText;

				int from = tf1.getText().length();
				int to = tf2.getText().length();

				// hsaNext는 nextLine한번사용하면 커서가 다음으로 옮겨진다.
				System.out.println(from + " " + to);

				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() < from + to)
						return;
					sb.append(line.substring(from, line.length() - to)).append(CR_LF);
				}

				ta.setText(sb.toString());
			}
		});

		btn[n++].addActionListener(new ActionListener() { // substring2 - 지정된 문자를 찾아서 그 위치까지 잘라내기
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length() * 2);
				Scanner sc = new Scanner(curText);

				prevText = curText;

				String tmp1 = tf1.getText();
				String tmp2 = tf2.getText();

				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					int from = line.indexOf(tmp1);
					int to = line.lastIndexOf(tmp2);

					if (from == -1)
						return;

					sb.append(line.substring(from + tmp1.length(), to)).append(CR_LF);
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // distinct - 중복 라인 제거
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length() * 2);
				Scanner sc = new Scanner(curText);
				HashSet hash = new HashSet();

				prevText = curText;

				// hasset에 해당 라인을 저장
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					hash.add(line);
				}

				// 헤쉬에 저장된 정보를 어레이리스트로 만듬
				ArrayList list = new ArrayList(hash);

				// 오름차순으로 정렬해준다.
				Collections.sort(list);

				// 옵션이 켜있다면 reverse로 내림차순으로 만들어 준다.
				if (optionFlag) {
					Collections.reverse(list);
				}

				// Stringbuffer에 붙여서 text로 설정해준다.
				for (int i = 0; i < list.size(); i++) {
					sb.append(list.get(i)).append(CR_LF);
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // distinct2 - 중복 라인 제거 + 카운트

			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());
				Scanner sc = new Scanner(curText);
				TreeMap tm = new TreeMap();
				String sp = tf1.getText();

				prevText = curText;

				if (sp.length() == 0)
					sp = ",";

				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					line.trim();

					if (tm.containsKey(line))
						tm.put(line, (int) (tm.get(line)) + 1);
					else
						tm.put(line, 1);
				}

				System.out.println(tm);

				if (optionFlag) {
					Iterator it = tm.descendingMap().entrySet().iterator();
				}

				Iterator it = tm.entrySet().iterator();

				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();

					int value = ((int) entry.getValue());

					sb.append(entry.getKey()).append(sp).append(value).append(CR_LF);
				}

				ta.setText(sb.toString());
			}
		});

		btn[n++].addActionListener(new ActionListener() { // 패턴적용
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());
				Scanner sc = new Scanner(curText);
				ArrayList list = new ArrayList();

				prevText = curText;

				String pattern = tf1.getText();
				String delimiter = tf2.getText();

				if (delimiter.length() == 0)
					delimiter = ",";

				while (sc.hasNext()) {
					String line = sc.nextLine();
					String[] tmp = line.split(delimiter);

					sb.append(MessageFormat.format(pattern, tmp)).append(CR_LF);
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // 패턴제거
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());
				Scanner sc = new Scanner(curText);

				prevText = curText;

				String pattern = tf1.getText();
				String delimiter = tf2.getText();

				Pattern p = Pattern.compile(pattern);

				if (delimiter.length() == 0)
					delimiter = ",";

				while (sc.hasNext()) {

					String line = sc.nextLine();
					line.trim();

					Matcher m = p.matcher(line);
					if (m.find()) {
						for (int i = 1; i <= m.groupCount(); i++) {
							sb.append(m.group(i)).append(delimiter);
						}
						sb.append(CR_LF);
					}

					ta.setText(sb.toString());
				}
			}
		});

		btn[n++].addActionListener(new ActionListener() { // 대소문자 변환
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());

				prevText = curText;

				System.out.println(curText.toUpperCase());
				System.out.println(curText.toLowerCase());

				if (!optionFlag) {
					sb.append(curText.toUpperCase());
				} else if (optionFlag) {
					sb.append(curText.toLowerCase());
				}

				ta.setText(sb.toString());
			}
		});

	}

	public static void main(String[] args) {
		textTool win = new textTool("Text Tool");
	}

	public textTool(String title) {
		super(title);// 질문
		lb1 = new Label("param1:", Label.RIGHT);
		lb2 = new Label("param2:", Label.RIGHT);
		lb3 = new Label("option", Label.RIGHT);
		tf1 = new TextField(15);
		tf2 = new TextField(15);

		ta = new TextArea();
		pNorth = new Panel();
		pSouth = new Panel();
		op = new Checkbox();
		for (int i = 0; i < btn.length; i++) {
			btn[i] = new Button(btnName[i]);
		}

		pNorth.setLayout(new FlowLayout());
		pNorth.add(lb3);
		pNorth.add(op);
		pNorth.add(lb1);
		pNorth.add(tf1);
		pNorth.add(lb2);
		pNorth.add(tf2);

		pSouth.setLayout(new GridLayout(2, 10));

		for (int i = 0; i < btn.length; i++) {
			pSouth.add(btn[i]);
		}

		add(pNorth, "North");
		add(ta, "Center");
		add(pSouth, "South");

		setBounds(100, 100, 600, 300);
		ta.requestFocus();
		registerEventHandler();
		setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		e.getWindow().setVisible(false);
		e.getWindow().dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
