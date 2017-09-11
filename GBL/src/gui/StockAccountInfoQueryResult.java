package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class StockAccountInfoQueryResult extends CustomedPage{

	JTextArea header = new JTextArea();
	JLabel footer = new JLabel();

	JPanel subPanel = new JPanel();

	JPanel subsubsubPanel = new JPanel();
	JPanel subsubPanel_h = new JPanel();
	JPanel subsubPanel_m = new JPanel();
	JPanel subsubPanel_l = new JPanel();
	
	JLabel left_l = new JLabel();
	JLabel right_l = new JLabel();
	
	JTextPane result_tp = new JTextPane();
	JTable result_tb;// = new JTable();
	JScrollPane jsp;
	

	JLabel xinxihang_l = new JLabel();
	JLabel subsubheader_l = new JLabel();
	int tradeNum = 115810;
	
	public StockAccountInfoQueryResult()
	{
		self = this;
		
		MatteBorder mb_h = new MatteBorder(0,0,3,0,Color.LIGHT_GRAY);
		header.setPreferredSize(new Dimension(0,64));
		header.setBackground(Color.BLACK);
		header.setBorder(mb_h);
		header.setForeground(Color.LIGHT_GRAY);
		header.setFont(new Font("宋体", Font.PLAIN, 24));
		header.setText("  网点 ：362102                                        交易码 ："+tradeNum+"\n  日期 ："
				+getCurrentDate()+"             股金账户信息查询       屏幕号 ：["+tradeNum+"-1]");
		header.setEditable(false);
		header.setFocusable(false);
		
		MatteBorder mb_f = new MatteBorder(3,0,0,0,Color.LIGHT_GRAY);
		footer.setPreferredSize(new Dimension(0,60));
		footer.setBackground(Color.BLACK);
		footer.setBorder(mb_f);
		footer.setForeground(Color.LIGHT_GRAY);
		footer.setFont(new Font("宋体", Font.PLAIN, 24));
		footer.setText("  柜员：364571  李若慧     pts/66   F3=退出  F9=重复  F10=提交  F1=帮助");
		footer.setFocusable(false);
		
		MatteBorder mb_r = new MatteBorder(2,2,2,2,Color.LIGHT_GRAY);
		result_tp.setPreferredSize(new Dimension(0,60));
		result_tp.setBackground(Color.BLACK);
		result_tp.setBorder(mb_r);
		result_tp.setForeground(Color.LIGHT_GRAY);
		
		result_tp.setFont(new Font("宋体", Font.PLAIN, 24));
		result_tp.setText("AAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCAAAAAAAAA\n\n\n\n");
		JScrollPane sp = new JScrollPane(result_tp);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		result_tp.setCaretColor(Color.WHITE);
		
		subsubheader_l.setBackground(Color.BLACK);
		subsubheader_l.setForeground(Color.LIGHT_GRAY);
		subsubheader_l.setFont(new Font("宋体", Font.PLAIN, 24));
		subsubheader_l.setText("<html><body>查  询  机  构 ：<br>账  &nbsp&nbsp&nbsp户 ：<br>产  品  组  别 ：</body></html>");//\n\n查询结构：\n账户\n产品组别");
		subsubheader_l.setPreferredSize(new Dimension(200,150));
		
		xinxihang_l.setBackground(Color.BLACK);
		xinxihang_l.setForeground(Color.LIGHT_GRAY);
		xinxihang_l.setFont(new Font("宋体", Font.PLAIN, 24));
		xinxihang_l.setText("  信息行：");
		xinxihang_l.setPreferredSize(new Dimension(0,36));
		
		//result_tb.set
		accountInfoTableInit();
		
		subsubPanel_h.setBackground(Color.BLACK);
		subsubPanel_h.setLayout(new BorderLayout());
		subsubPanel_h.add(subsubheader_l, BorderLayout.WEST);

		subsubPanel_l.setBackground(Color.BLACK);
		subsubPanel_l.setLayout(new BorderLayout());
		subsubPanel_l.add(xinxihang_l, BorderLayout.CENTER);
		
		subsubPanel_m.setLayout(new BorderLayout());
		subsubPanel_m.add(jsp,BorderLayout.CENTER);
		
		subsubsubPanel.setLayout(new BorderLayout());
		subsubsubPanel.add(subsubPanel_h, BorderLayout.NORTH);
		subsubsubPanel.add(subsubPanel_m, BorderLayout.CENTER);
		subsubsubPanel.add(subsubPanel_l, BorderLayout.SOUTH);		
		
		
		subPanel.setLayout(new BorderLayout()); 
		subPanel.add(header,BorderLayout.NORTH);
		subPanel.add(subsubsubPanel,BorderLayout.CENTER);
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
	
	public void accountInfoTableInit()
	{
		String[] columnNames = { "股金帐号               ",
				"分账号", "产品种类",
				"产品名称","账户名称                                                  ",
				"存在状态","账户余额","冻结/止付状态",
				"冻结/止付金额","支取方式","挂式状态","开户日期",
				"凭证号码      ",
				"分红账户      ",
				"销户日期","证件类型","证件号码","开户网点","打印名称"};
		
		Object[][] data = {
{"1229630012293011021744", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},                     
{"1229630012293011021745", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""}, 
{"1229630012293011021746", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021747", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021748", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021749", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""}, 
{"1229630012293011021750", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021751", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021752", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""}, 
{"1229630012293011021753", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021754", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021755", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},
{"1229630012293011021754", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""},  
{"1229630012293011021755", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""}, 
{"1229630012293011021756", "000001", new Integer(282), "法人股","晋中中级机械有限公司","正常",new Float(0.00), "",new Float(0.00),"证件","","2004-02-27","00000900275245","630012042011813718","","Z","1-383487534","362102",""}  
		};
		result_tb = new JTable(data,columnNames);
		result_tb.setBackground(Color.BLACK);
		result_tb.setFont(new Font("宋体", Font.PLAIN, 24));
		result_tb.setForeground(Color.LIGHT_GRAY);
		result_tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		result_tb.setSelectionBackground(Color.LIGHT_GRAY);
		result_tb.setShowGrid(false);
		
		MouseListener[] mls = result_tb.getMouseListeners();
		for(MouseListener m : mls)
		{
			result_tb.removeMouseListener(m);
		}
		MouseMotionListener[] mmls = result_tb.getMouseMotionListeners();
		for(MouseMotionListener mm : mmls)
		{
			result_tb.removeMouseMotionListener(mm);
		}
		
		//result_tb.setsele
		//result_tb.editab
		
		result_tb.getTableHeader().setBackground(Color.BLACK);
		result_tb.getTableHeader().setFont(new Font("宋体", Font.PLAIN, 24));
		result_tb.getTableHeader().setForeground(Color.LIGHT_GRAY);
		result_tb.getTableHeader().setResizingAllowed(false);
		result_tb.getTableHeader().setReorderingAllowed(false);
		result_tb.getTableHeader().setBorder(null);
		//result_tb.getTableHeader().table
		
		result_tb.setRowHeight(28);
		
		TableColumnModel tcm = result_tb.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(280);
		tcm.getColumn(1).setPreferredWidth(100);
		tcm.getColumn(2).setPreferredWidth(104);
		tcm.getColumn(3).setPreferredWidth(104);
		tcm.getColumn(4).setPreferredWidth(700);
		tcm.getColumn(5).setPreferredWidth(104);
		tcm.getColumn(6).setPreferredWidth(104);
		tcm.getColumn(7).setPreferredWidth(160);
		tcm.getColumn(8).setPreferredWidth(160);
		tcm.getColumn(9).setPreferredWidth(160);
		tcm.getColumn(10).setPreferredWidth(160);
		tcm.getColumn(11).setPreferredWidth(160);
		tcm.getColumn(12).setPreferredWidth(360);
		tcm.getColumn(13).setPreferredWidth(360);
		tcm.getColumn(14).setPreferredWidth(160);
		tcm.getColumn(15).setPreferredWidth(160);
		tcm.getColumn(16).setPreferredWidth(240);
		tcm.getColumn(17).setPreferredWidth(160);
		tcm.getColumn(18).setPreferredWidth(160);
		
		result_tb.getTableHeader().setPreferredSize(new Dimension(result_tb.getTableHeader().getPreferredSize().width,48));

		result_tb.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					bg.changePanel(self, last_page);
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		jsp = new JScrollPane(result_tb);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBackground(Color.BLACK);
	}
	
	public void setCustomedFocus()
	{
		this.result_tb.grabFocus();
	}
	

}
