package gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class BankGui extends JFrame {
	
	private JTextPane jpane = new JTextPane();
//	private JPanel jp = new JPanel();
	
//	private JTextArea txtContent = new JTextArea();

	//HomePage hp = new HomePage();
	
	public BankGui(){
		
		Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Resource/icon.jpg"));
		
		ImageIcon icon=new ImageIcon(img);  
		setIconImage(icon.getImage());
		LoginGui lg = new LoginGui(this);
		this.setLayout(new GridLayout(1,1));
		this.add(lg);
		//this.add(hp);
		
		//StockAccountInfoQueryResult saiqr = new StockAccountInfoQueryResult();
		//this.add(saiqr);
		
		this.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// 当按下回车时
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Are we here");
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					System.out.println("Here we are");
				}
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}
		});

				
	}
	
	public void changePanel(CustomedPage current_jp,CustomedPage new_jp)
	{
		current_jp.setVisible(false);
		this.remove(current_jp);
		this.add(new_jp);
		new_jp.setVisible(true);
		//this.setVisible(true);
		new_jp.setCustomedFocus();
	}
	
	
	
	public static void main(String[] args) {
		BankGui frame = new BankGui();
		frame.setTitle("综合业务");
		frame.setSize(1024, 700);
		frame.setVisible(true);
		frame.setBackground(Color.BLACK);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	//	frame.hp.setFocusOnSelectText();

		
//		int screenWidth=((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
//		int screenHeight = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height); 
//		System.out.println(screenWidth+"*"+screenHeight);
		
	} 

}
