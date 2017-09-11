package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import menu.MainMenu;
import menu.MenuText;

public class HomePage extends CustomedPage{
	
	JTextArea header = new JTextArea();
	JPanel subPanel = new JPanel();
	JPanel subsubPanel = new JPanel();
	JPanel subsubsubPanel = new JPanel();
	JLabel footer = new JLabel();
//	JPanel menu_panel = new JPanel();
	
	MenuText menu_ta;// = new JTextPane();
	JLabel left_l = new JLabel();
	JLabel right_l = new JLabel();
	JLabel xinxihang_l = new JLabel();
	JLabel plsSelect_l = new JLabel();
	JTextField plsSelect_t = new JTextField();
		
	public HomePage()
	{
		self = this;
		
		MatteBorder mb_h = new MatteBorder(0,0,3,0,Color.LIGHT_GRAY);
		header.setPreferredSize(new Dimension(0,64));
		header.setBackground(Color.BLACK);
		header.setBorder(mb_h);
		header.setForeground(Color.LIGHT_GRAY);
		header.setFont(new Font("宋体", Font.PLAIN, 24));
		header.setText("  网点 ：362102\n  日期 ："+getCurrentDate()+"             综合业务网络系统");
		header.setEditable(false);
		header.setFocusable(false);
		
		MatteBorder mb_f = new MatteBorder(3,0,0,0,Color.LIGHT_GRAY);
		footer.setPreferredSize(new Dimension(0,60));
		footer.setBackground(Color.BLACK);
		footer.setBorder(mb_f);
		footer.setForeground(Color.LIGHT_GRAY);
		footer.setFont(new Font("宋体", Font.PLAIN, 24));
		footer.setText("  柜员：364571  李若慧         pts/66      F3=推出       F1=帮助");
		footer.setFocusable(false);
		
		menu_ta = new MainMenu();
		
		xinxihang_l.setBackground(Color.BLACK);
		xinxihang_l.setForeground(Color.LIGHT_GRAY);
		xinxihang_l.setFont(new Font("宋体", Font.PLAIN, 24));
		xinxihang_l.setText("  信息行");
		xinxihang_l.setPreferredSize(new Dimension(600,0));
		
		plsSelect_l.setBackground(Color.BLACK);
		plsSelect_l.setForeground(Color.LIGHT_GRAY);
		//MatteBorder mb_dd = new MatteBorder(3,0,0,0,Color.LIGHT_GRAY);
		//plsSelect_l.setBorder(mb_dd);
		plsSelect_l.setFont(new Font("宋体", Font.PLAIN, 24));
		plsSelect_l.setText("请选择 ==>");
		
		plsSelect_t.setBackground(Color.BLACK);
		plsSelect_t.setForeground(Color.LIGHT_GRAY);
		plsSelect_t.setFont(new Font("宋体", Font.PLAIN, 24));
		plsSelect_t.setPreferredSize(new Dimension(180,0));
		plsSelect_t.setBorder(null);
		plsSelect_t.setText("");
		plsSelect_t.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

				if(arg0.getKeyCode() == KeyEvent.VK_DOWN)
				{
					if(menu_ta.checkAndupdateCurrenSelect((char)(menu_ta.getCurrentSelection()+1)))
					{						
						plsSelect_t.setText(""+menu_ta.getCurrentSelection());
						arg0.consume();
					}				
				}
				else if(arg0.getKeyCode() == KeyEvent.VK_UP)
				{
					if(menu_ta.checkAndupdateCurrenSelect((char)(menu_ta.getCurrentSelection()-1)))
					{						
						plsSelect_t.setText(""+menu_ta.getCurrentSelection());
						arg0.consume();
					}	
				}
				else if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int _statue = menu_ta.checkSelection();
					if(_statue == MenuText.NOTHING)
						arg0.consume();
					else if(_statue == MenuText.NEW_MENU)
					{
						MenuText _tem = menu_ta.getAndSetLowerLeverMenu();
						menu_ta = _tem;		
						menu_ta.setVisible(true);
						subsubPanel.add(menu_ta,BorderLayout.CENTER);
						header.setText("  网点 ：362102\n  日期 ："+getCurrentDate()+"             "+menu_ta.menu_title);
					}
					else if(_statue == MenuText.NEW_PAGE)
					{
						CustomedPage _newpage = menu_ta.getAndSetNewPage();
						_newpage.setBandGui(bg);
						getNewPage(_newpage);
						
						//TODO
					}
						//getNewPage(newpage);
				}
				
				else if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					boolean _statue = menu_ta.checkIfHaveHigherMenu();
					if(_statue == false)
						bg.changePanel(self, new LoginGui(bg));
					else
					{
						menu_ta.setVisible(false);
						menu_ta = menu_ta.getHigherLevelMenu();
						menu_ta.setVisible(true);
						subsubPanel.add(menu_ta,BorderLayout.CENTER);
						header.setText("  网点 ：362102\n  日期 ："+getCurrentDate()+"             "+menu_ta.menu_title);
						
					}
						 
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
				char c_tem = arg0.getKeyChar();
				if(plsSelect_t.getText().length()==0)
				{
					if(menu_ta.checkAndupdateCurrenSelect(c_tem))
					{
						if(arg0.getKeyChar()>='a')
							arg0.setKeyChar((char) (c_tem-32));
					}
					else
						arg0.consume();					
				}
				else if(plsSelect_t.getText().length()==1)
				{
					plsSelect_t.setText("");
					if(menu_ta.checkAndupdateCurrenSelect(c_tem))
					{
						if(arg0.getKeyChar()>='a')
							arg0.setKeyChar((char) (c_tem-32));
					}
					else
						arg0.consume();	
				}
				else if(plsSelect_t.getText().length()>1)
				{
					System.out.println("Shouldn't be here");
				}
				c_tem = arg0.getKeyChar();
			}
			
		});
		
		//menu_ta.append(str);

		
		//	
		//doc.setCharacterAttributes(0,2,attr_bg,false);
		
		
		subsubsubPanel.setBackground(Color.BLACK);
		subsubsubPanel.setLayout(new BorderLayout());
		subsubsubPanel.add(xinxihang_l, BorderLayout.WEST);
		subsubsubPanel.add(plsSelect_l, BorderLayout.CENTER);
		subsubsubPanel.add(plsSelect_t, BorderLayout.EAST);
		subsubsubPanel.setPreferredSize(new Dimension(0,40));
		
		
		subsubPanel.setLayout(new BorderLayout()); 
		subsubPanel.setBackground(Color.BLACK);
		subsubPanel.add(menu_ta,BorderLayout.CENTER);
		subsubPanel.add(subsubsubPanel,BorderLayout.SOUTH);
		

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
        this.add(subPanel,BorderLayout.CENTER);

        this.setBackground(Color.BLACK);
		this.setVisible(true);
	
	}
	

	
	public int getStringPositionInTextArea(JTextPane _jtp, String _s_tem)
	{
		return _jtp.getText().indexOf(_s_tem);
	}
				
	public void setFocusOnSelectText()
	{
		plsSelect_t.requestFocus();
	}
	
	public void setCustomedFocus()
	{
		setFocusOnSelectText();
	}
	
	public void getNewPage(CustomedPage _newpage)
	{
		bg.changePanel(this,_newpage);
		this.setVisible(false);
		_newpage.setLastPage(this);
	}

}
