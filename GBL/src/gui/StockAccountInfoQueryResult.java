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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StockAccountInfoQueryResult extends JPanel {
	
	
	//JTextPane result_tp = new JTextPane();
	JTable result_tb;// = new JTable();
	JScrollPane jsp;
	
	List<StockAccount> saifqrl = new ArrayList<StockAccount>();
	
	
	String[] columnNames = { "股金帐号               ",
			"分账号", "产品种类",
			"产品名称","账户名称                                                  ",
			"存在状态","账户余额","冻结/止付状态",
			"冻结/止付金额","支取方式","挂式状态","开户日期",
			"凭证号码      ",
			"分红账户      ",
			"销户日期","证件类型","证件号码","开户网点","打印名称"};
	
	
	public StockAccountInfoQueryResult()
	{
/*		MatteBorder mb_r = new MatteBorder(2,2,2,2,Color.LIGHT_GRAY);
		result_tp.setPreferredSize(new Dimension(0,60));
		result_tp.setBackground(Color.BLACK);
		result_tp.setBorder(mb_r);
		result_tp.setForeground(Color.LIGHT_GRAY);
		
		result_tp.setFont(new Font("宋体", Font.PLAIN, 24));
		result_tp.setText("AAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCAAAAAAAAA\n\n\n\n");
		//JScrollPane sp = new JScrollPane(result_tp);
		result_tp.setCaretColor(Color.WHITE);*/
		
		//jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//this.setBackground(Color.BLACK);

		
		accountInfoTableInit();
		
		jsp = new JScrollPane(result_tb);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBackground(Color.BLACK);
		this.setLayout(new BorderLayout());
		this.add(jsp, BorderLayout.CENTER);
		
		this.setOpaque(true);

		
	}
	
	public void accountInfoTableInit()
	{

		
/*		Object[][] data = {
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
		};*/
		Object[][] data = getAccountInfoFromExele();
		
		result_tb = new JTable(data,columnNames);
		result_tb.setBackground(Color.BLACK);
		result_tb.setFont(new Font("宋体", Font.PLAIN, 24));
		result_tb.setForeground(Color.LIGHT_GRAY);
		result_tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		result_tb.setSelectionBackground(Color.LIGHT_GRAY);
		result_tb.setShowGrid(false);
		result_tb.setOpaque(true);
		
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
					//bg.changePanel(self, last_page);
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
		
	}
	
	public Object[][] getAccountInfoFromExele()
	{
		XSSFRow row;
	    Object[][] data = null;
		
		File file = new File("src/Resource/StockAccountInfo.xlsx");
	    FileInputStream fIP;
		try {
			fIP = new FileInputStream(file);
			//Get the workbook instance for XLSX file 
		      XSSFWorkbook workbook = new XSSFWorkbook(fIP);
		      
		      
		      if(file.isFile() && file.exists())
		      {
		         System.out.println(
		         "openworkbook.xlsx file open successfully.");
		      }
		      else
		      {
		         System.out.println(
		         "Error to open openworkbook.xlsx file.");
		      }
		      
		      XSSFSheet spreadsheet = workbook.getSheetAt(0);
		      Iterator < Row > rowIterator = spreadsheet.iterator();
		      
		      int row_count = spreadsheet.getPhysicalNumberOfRows()-1;
		      
		      System.out.println(""+spreadsheet.getFirstRowNum());
		      
		      data = new Object[row_count][];
		      
		      int current_row = 0;
		      
		      row = (XSSFRow) rowIterator.next();
		      while (rowIterator.hasNext()) 
		      {
		         row = (XSSFRow) rowIterator.next();
		         //Iterator < Cell > cellIterator = row.cellIterator();
		         
		         StockAccount aa = new StockAccount();
		         aa.setProperty(row);
		         
		         Object[] row_data = aa.changeToObjectArray();
		         data[current_row] = row_data;
		         current_row ++;

		         saifqrl.add(aa);
		      }
		      
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
		
		return data;
	}
	

}
