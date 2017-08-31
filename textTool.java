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

	String[] btnName = { "Undo", "¦���� ����", "���ڻ���", // param1�� ������ ���ڵ��� �����ϴ� ���
			"���ٻ���", // �� �� ����
			"���λ��߰�", // Param1�� Param2�� ���ڿ��� �� ������ �յڿ� ���̴� ���
			"substring", // Param1�� Param2�� ������ ���ڿ��� �� ���ο��� �����ϴ� ���
			"substring2", // Param1�� Param2�� ������ ���ڿ��� �ѷ����� �κ��� ����� �����ϴ� ���
			"distinct", // �ߺ��������� �� �����ؼ� �����ֱ�
			"distinct2", // �ߺ��������� �� �����ؼ� �����ֱ� - �ߺ�ī��Ʈ ����
			"��������", // �����Ϳ� ������ ���� �����ϱ�
			"��������", // �����Ϳ� ����� ���� �����ϱ�
			"��ҹ��ں�ȯ", // ��ҹ��� ��ȯ���ֱ�
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
		btn[n++].addActionListener(new ActionListener() { // Undo - �۾����� ���·� �ǵ���
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

				// 1. pram1 ��â�� �ƹ��͵� ������ ¦���ٸ� ����
				if (tmp.length() == 0) {
					while (sc.hasNextLine()) {
						String line = sc.nextLine();
						if (tmp.length() == 0 && count % 2 == 0) {
							sb.append(line).append(CR_LF);
						}
						count++;
					}
				}

				// 2. �ɼ��� �����ְ� �ؽ�Ʈ�ʵ忡 ���ڰ������� �׼��� ���� ���� ����
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
				// 3. �ɼ��� �����ְ� �ؽ�Ʈ �ʵ忡 ���ڰ������� �� ���� ���θ� ����
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

		btn[n++].addActionListener(new ActionListener() { // ���ڻ��� - Param1�� ������ ���ڸ� �����ϴ� ���

			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length());

				prevText = curText;

				String tmpText = tf1.getText();

				// param1�� �ƹ��͵� ������ �׳� ����
				if ("".equals(tmpText))
					return;

				// �ؽ�Ʈ �ʵ忡 ��� ���ڸ� �޾Ƽ�
				for (int i = 0; i < curText.length(); i++) {
					char ch = curText.charAt(i);

					// �ɼ��� �����ִٸ� param1�� �ִ� ���� ����
					if (!optionFlag) {
						if (tmpText.indexOf(ch) == -1)
							sb.append(ch);
						// �ɼ��� �����ִٸ� param1�� �ִ� ���ڸ� ����
					} else if (optionFlag) {
						if (tmpText.indexOf(ch) != -1 || ch == '\n')
							sb.append(ch);
					}
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // trim - ������ �¿������ �����ϴ� ���
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

		btn[n++].addActionListener(new ActionListener() { // ���λ� - �� ���ο� ���λ�, ���̻� ���̱�
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

		btn[n++].addActionListener(new ActionListener() { // substring - ���ڿ� �ڸ���
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length() * 2);
				Scanner sc = new Scanner(curText);
				prevText = curText;

				int from = tf1.getText().length();
				int to = tf2.getText().length();

				// hsaNext�� nextLine�ѹ�����ϸ� Ŀ���� �������� �Ű�����.
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

		btn[n++].addActionListener(new ActionListener() { // substring2 - ������ ���ڸ� ã�Ƽ� �� ��ġ���� �߶󳻱�
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

		btn[n++].addActionListener(new ActionListener() { // distinct - �ߺ� ���� ����
			public void actionPerformed(ActionEvent ae) {
				String curText = ta.getText();
				StringBuffer sb = new StringBuffer(curText.length() * 2);
				Scanner sc = new Scanner(curText);
				HashSet hash = new HashSet();

				prevText = curText;

				// hasset�� �ش� ������ ����
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					hash.add(line);
				}

				// �콬�� ����� ������ ��̸���Ʈ�� ����
				ArrayList list = new ArrayList(hash);

				// ������������ �������ش�.
				Collections.sort(list);

				// �ɼ��� ���ִٸ� reverse�� ������������ ����� �ش�.
				if (optionFlag) {
					Collections.reverse(list);
				}

				// Stringbuffer�� �ٿ��� text�� �������ش�.
				for (int i = 0; i < list.size(); i++) {
					sb.append(list.get(i)).append(CR_LF);
				}

				ta.setText(sb.toString());

			}
		});

		btn[n++].addActionListener(new ActionListener() { // distinct2 - �ߺ� ���� ���� + ī��Ʈ

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

		btn[n++].addActionListener(new ActionListener() { // ��������
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

		btn[n++].addActionListener(new ActionListener() { // ��������
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

		btn[n++].addActionListener(new ActionListener() { // ��ҹ��� ��ȯ
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
		super(title);// ����
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
