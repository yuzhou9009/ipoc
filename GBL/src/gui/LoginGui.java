package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class LoginGui extends CustomedPage{
	
	private JPanel subPanel = new JPanel();
	
	private JTextArea jta = new JTextArea();
	private JLabel user_l = new JLabel();
	private JLabel site_l = new JLabel();
	private JLabel pswd_l = new JLabel();
	
	private JTextField userID = new JTextField();
	private JTextField siteID = new JTextField();
	private JPasswordField  pswd = new JPasswordField();
	
	private MatteBorder underline_border = new MatteBorder(0,0,1,0,Color.LIGHT_GRAY);
	
	private UserInfo user = new UserInfo();
	//char[] pswd_tem = {'A','a','1','2','3','4','5'};
	//private UserInfo static_user = new UserInfo(312312,362102,pswd_tem);

	char[] pswd_tem = {'1','2','3'};
	private UserInfo static_user = new UserInfo(111,222,pswd_tem);
//	private JLabel black_l = new JLabel();

	private String icon = "\r\n\r\n"
			+ "                FFFFFFFFFFFFFFFFFFFF       CCCCCCCCCCC           BBBBBBBBBBBBB\r\n"
			+ "                   FFFF         FFFFF    CCCC         CCC          BBB       BBBB\r\n"
			+ "                  FFF             FFF  CCCC             CC        BBB          BBBB\r\n"
			+ "                 FFF                  CCC               C        BBB         BBBB\r\n"
			+ "                 FFF         FF      CCC                        BBB        BBB\r\n"
			+ "               FFFFFFFFFFFFFF       CCCC                       BBBBBBBBBBB\r\n"
			+ "              FFFFFFFFFFFFFF       CCCC                       BBBBBBBBBBBBB\r\n"
			+ "             FFF         FF        CCCC                      BBBB          BBBB\r\n"
			+ "            FFF                    CCCC               C    BBBB            BBBB\r\n"
			+ "           FFF                      CCC             CC    BBB            BB\r\n"
			+ "          FFF                       CCCCC        CCC     BBBB          BBBB\r\n"
			+ "     FFFFFFFFFF                      CCCCCCCCCCCC     BBBBBBBBBBBBBBBBB\r\n\r\n\r\n"
			+ "                                   北京宇信易诚科技有限公司                                  \r\n";
	
	
	public LoginGui(BankGui _bg){
		this.bg = _bg;
		self = this;
		
		jta.setForeground(Color.LIGHT_GRAY);
		jta.setFont(new Font("宋体", Font.PLAIN, 22));
		jta.setText(icon);
		jta.setBackground(Color.BLACK);
		jta.setPreferredSize(new Dimension(0,480));
		jta.setEditable(false);
		jta.setFocusable(false);
		
				
		
		user_l.setText("             柜  员  号  ：");
		site_l.setText("             网  点  号  ：");
		pswd_l.setText("             密      码  ：");
		user_l.setForeground(Color.LIGHT_GRAY);
		user_l.setFont(new Font("宋体", Font.PLAIN, 24));
		site_l.setForeground(Color.LIGHT_GRAY);
		site_l.setFont(new Font("宋体", Font.PLAIN, 24));
		pswd_l.setForeground(Color.LIGHT_GRAY);
		pswd_l.setFont(new Font("宋体", Font.PLAIN, 24));
//		black_l.setText(" 1");
//		black_l.setForeground(Color.WHITE);
		
		userID.setBorder(underline_border);
		siteID.setBorder(underline_border);
		pswd.setBorder(underline_border);
		//siteID.setBorder(border);
		//pswd.setBorder(border);
		
		MouseListener[] arr2 = userID.getMouseListeners();
		for(MouseListener m : arr2)
			userID.removeMouseListener(m);
		arr2 = siteID.getMouseListeners();
		for(MouseListener m : arr2)
			siteID.removeMouseListener(m);
		arr2 = pswd.getMouseListeners();
		for(MouseListener m : arr2)
			pswd.removeMouseListener(m);
		
		MouseMotionListener[] arr3 = userID.getMouseMotionListeners();
		for(MouseMotionListener m : arr3)
			userID.removeMouseMotionListener(m);
		arr2 = siteID.getMouseListeners();
		for(MouseMotionListener m : arr3)
			siteID.removeMouseMotionListener(m);
		arr2 = pswd.getMouseListeners();
		for(MouseMotionListener m : arr3)
			pswd.removeMouseMotionListener(m);
		
		userID.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					siteID.grabFocus();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					siteID.grabFocus();
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
		
		siteID.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					pswd.grabFocus();
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) {
					userID.grabFocus();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					pswd.grabFocus();
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
		
		pswd.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					user.userID = Integer.parseInt(userID.getText());
					user.siteID = Integer.parseInt(siteID.getText());
					user.pswd = pswd.getPassword();
					
					if(checkUserInfo(user) == true)
					{
						HomePage hp = new HomePage();
						hp.setBandGui(bg);
						bg.changePanel(self, hp);
						self.setVisible(false);
						hp.setLastPage(self);
					}
					else
					{
						pswd.setText("");
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) {
					siteID.grabFocus();
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

		
		userID.setBackground(Color.BLACK);
		userID.setForeground(Color.LIGHT_GRAY);
		userID.setFont(new Font("宋体", Font.PLAIN, 24));		
		siteID.setBackground(Color.BLACK);
		siteID.setForeground(Color.LIGHT_GRAY);
		siteID.setFont(new Font("宋体", Font.PLAIN, 24));
		pswd.setBackground(Color.BLACK);
		pswd.setForeground(Color.LIGHT_GRAY);
		pswd.setFont(new Font("宋体", Font.PLAIN, 24));
		
		
		subPanel.setLayout(new GridLayout(5,3));

		subPanel.add(user_l);
		subPanel.add(userID);
		subPanel.add(new JLabel());
		
		subPanel.add(site_l);
		subPanel.add(siteID);
		subPanel.add(new JLabel());
		

		subPanel.add(pswd_l);
		subPanel.add(pswd);
		subPanel.add(new JLabel());
		
		subPanel.add(new JLabel());
		subPanel.add(new JLabel());
		subPanel.add(new JLabel());
		
		subPanel.add(new JLabel());
		subPanel.add(new JLabel());
		subPanel.add(new JLabel());
		
		//subPanel.add(new JLabel());
		//subPanel.add(new JLabel());
		//subPanel.add(new JLabel());
		
		subPanel.setBackground(Color.BLACK);
		subPanel.setVisible(true);
		
        this.setLayout(new BorderLayout());  
        this.add(jta, BorderLayout.NORTH);
        this.add(subPanel,BorderLayout.CENTER);

        this.setBackground(Color.BLACK);
        
		this.setVisible(true);
		
	}
	
	public boolean checkUserInfo(UserInfo ui)
	{
		if(ui.userID == this.static_user.userID && ui.siteID == this.static_user.siteID)
		{
			if(new String(ui.pswd).equals(new String(this.static_user.pswd)))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public void setCustomedFocus()
	{
		userID.grabFocus();
	}
}
