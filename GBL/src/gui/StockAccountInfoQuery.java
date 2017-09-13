package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StockAccountInfoQuery extends CustomedPage{

	JTextArea header = new JTextArea();
	JLabel footer = new JLabel();

	JPanel subPanel = new JPanel();

	JPanel subsubPanel = new JPanel();
	JLabel subsubPanel_part_0 = new JLabel();
	JLabel subsubPanel_part_1 = new JLabel();
	JLabel subsubPanel_part_2 = new JLabel();
	
	
	JLabel left_l = new JLabel();
	JLabel right_l = new JLabel();


	JLabel xinxihang_l = new JLabel();
	JLabel subsubheader_l = new JLabel();
	int tradeNum = 115810;
	
	JTextField query_orga_tf = new JTextField();
	JTextField account_tf = new JTextField();
	JTextField product_team_tf = new JTextField();
	JTextField stock_type_tf = new JTextField();
	JTextField client_name_tf = new JTextField();
	JTextField start_balance_tf = new JTextField();
	JTextField end_balance_tf = new JTextField();
	JTextField start_date_tf = new JTextField();
	JTextField end_date_tf = new JTextField();
	
	StockAccountInfoQueryResult saiqr;

	
	public StockAccountInfoQuery()
	{
		self = this;
		
		MatteBorder mb_h = new MatteBorder(0,0,3,0,Color.LIGHT_GRAY);
		header.setPreferredSize(new Dimension(0,64));
		header.setBackground(Color.BLACK);
		header.setBorder(mb_h);
		header.setForeground(Color.LIGHT_GRAY);
		header.setFont(new Font("����", Font.PLAIN, 24));
		header.setText("  ���� ��362102                                        ������ ��"+tradeNum+"\n  ���� ��"
				+getCurrentDate()+"             �ɽ��˻���Ϣ��ѯ       ��Ļ�� ��["+tradeNum+"-1]");
		header.setEditable(false);
		header.setFocusable(false);
		
		MatteBorder mb_f = new MatteBorder(3,0,0,0,Color.LIGHT_GRAY);
		footer.setPreferredSize(new Dimension(0,60));
		footer.setBackground(Color.BLACK);
		footer.setBorder(mb_f);
		footer.setForeground(Color.LIGHT_GRAY);
		footer.setFont(new Font("����", Font.PLAIN, 24));
		footer.setText("  ��Ա��364571  ������     pts/66   F3=�˳�  F9=�ظ�  F10=�ύ  F1=����");
		footer.setFocusable(false);
		
		
		subsubPanel.setBackground(Color.BLACK);
		subsubPanel.setLayout(null);
		
		MatteBorder mb_tem = new MatteBorder(3,3,3,3,Color.LIGHT_GRAY);
		subsubPanel_part_0.setForeground(Color.LIGHT_GRAY);
		subsubPanel_part_0.setBorder(mb_tem);
		subsubPanel_part_0.setFont(new Font("����", Font.PLAIN, 24));
		subsubPanel_part_0.setText("<html><body>��  ѯ  ��  �� ��<br>��  &nbsp&nbsp&nbsp�� ��<br>��  Ʒ  ��  �� ��<br>��  ��  ��  �� ��</body></html>");//\n\n��ѯ�ṹ��\n�˻�\n��Ʒ���");
		subsubPanel_part_0.setBounds(25, 35, 175, 125);
		
		subsubPanel_part_1.setForeground(Color.LIGHT_GRAY);
		subsubPanel_part_1.setBorder(mb_tem);
		subsubPanel_part_1.setFont(new Font("����", Font.PLAIN, 24));
		subsubPanel_part_1.setText("<html><body>��  ��  ��  �� ��</body></html>");
		subsubPanel_part_1.setBounds(25, 180, 175, 35);
		
		subsubPanel_part_2.setForeground(Color.LIGHT_GRAY);
		subsubPanel_part_2.setBorder(mb_tem);
		subsubPanel_part_2.setFont(new Font("����", Font.PLAIN, 24));
		subsubPanel_part_2.setText("<html><body>��ʼ�˻���<br>��ֹ�˻���<br>��ʼ�������ڣ�<br>��ֹ�������ڣ�</body></html>");//\n\n��ѯ�ṹ��\n�˻�\n��Ʒ���");
		subsubPanel_part_2.setBounds(25, 300, 175, 125);
		
		xinxihang_l.setBackground(Color.BLACK);
		xinxihang_l.setForeground(Color.LIGHT_GRAY);
		xinxihang_l.setFont(new Font("����", Font.PLAIN, 24));
		xinxihang_l.setText("<html><body>&nbsp��Ϣ�У�</body></html>");
		xinxihang_l.setBounds(0, 505, 175, 35);
		
		subsubPanel.add(subsubPanel_part_0);
		subsubPanel.add(subsubPanel_part_1);
		subsubPanel.add(subsubPanel_part_2);
		subsubPanel.add(xinxihang_l);
		
		end_date_tf.setBackground(Color.BLACK);
		end_date_tf.setFont(new Font("����", Font.PLAIN, 24));
		end_date_tf.setBounds(250, 400, 200, 30);

		subsubPanel.add(end_date_tf);
		
		end_date_tf.grabFocus();
		
		//saiqr = new StockAccountInfoQueryResult();
		//saiqr.setBounds(25, 175, 860, 330);

		//subsubPanel.add(saiqr);
		//saiqr.setVisible(true);
		//saiqr.result_tb.grabFocus();
		
		end_date_tf.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					xinxihang_l.grabFocus();
					saiqr = new StockAccountInfoQueryResult();
					saiqr.setBounds(25, 180, 870, 320);

					subsubPanel.add(saiqr);
					subsubPanel.setVisible(true);
					saiqr.setVisible(true);
					subsubPanel.validate();
					saiqr.result_tb.grabFocus();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
/*
		
		subsubheader_l.setBackground(Color.BLACK);
		subsubheader_l.setForeground(Color.LIGHT_GRAY);
		subsubheader_l.setFont(new Font("����", Font.PLAIN, 24));
		subsubheader_l.setText("<html><body>��  ѯ  ��  �� ��<br>��  &nbsp&nbsp&nbsp�� ��<br>��  Ʒ  ��  �� ��</body></html>");//\n\n��ѯ�ṹ��\n�˻�\n��Ʒ���");
		subsubheader_l.setPreferredSize(new Dimension(200,150));
		
		xinxihang_l.setBackground(Color.BLACK);
		xinxihang_l.setForeground(Color.LIGHT_GRAY);
		xinxihang_l.setFont(new Font("����", Font.PLAIN, 24));
		xinxihang_l.setText("  ��Ϣ�У�");
		xinxihang_l.setPreferredSize(new Dimension(0,36));
		
		//result_tb.set
		//getAccountInfoFromExele();
		saiqr = new StockAccountInfoQueryResult();
		
		subsubPanel_h.setBackground(Color.BLACK);
		subsubPanel_h.setLayout(new BorderLayout());
		subsubPanel_h.add(subsubheader_l, BorderLayout.WEST);

		subsubPanel_l.setBackground(Color.BLACK);
		subsubPanel_l.setLayout(new BorderLayout());
		subsubPanel_l.add(xinxihang_l, BorderLayout.CENTER);
		
		
		subsubsubPanel.setLayout(new BorderLayout());
		subsubsubPanel.add(subsubPanel_h, BorderLayout.NORTH);
		subsubsubPanel.add(saiqr, BorderLayout.CENTER);
		subsubsubPanel.add(subsubPanel_l, BorderLayout.SOUTH);		*/
		
		
		subPanel.setLayout(new BorderLayout()); 
		subPanel.add(header,BorderLayout.NORTH);
		subPanel.add(subsubPanel,BorderLayout.CENTER);
		subPanel.add(footer,BorderLayout.SOUTH);

		subPanel.setBackground(Color.BLACK);
		subPanel.setVisible(true);

		left_l.setPreferredSize(new Dimension(50,0));
		right_l.setPreferredSize(new Dimension(50,0));
        this.setLayout(new BorderLayout());

        this.add(left_l, BorderLayout.WEST);
        this.add(right_l, BorderLayout.EAST);
		this.add(subPanel, BorderLayout.CENTER);
        this.setBackground(Color.BLACK);
		this.setVisible(true);
	}
	

	public void setCustomedFocus()
	{
		end_date_tf.grabFocus();
		//saiqr.result_tb.grabFocus();
		//this.saiqr.result_tb.grabFocus();
	}
	
	
	

}
