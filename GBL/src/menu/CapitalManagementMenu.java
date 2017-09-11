package menu;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

public class CapitalManagementMenu extends MenuText{
	
	public CapitalManagementMenu()
	{
		this.menu_level = 2;

		this.menu_title = "股  金  管  理"; 
		
		doc =(DefaultStyledDocument)this.getDocument();
		StyleConstants.setBackground(attr_select,Color.LIGHT_GRAY);
		StyleConstants.setForeground(attr_select,Color.BLACK);
		StyleConstants.setBackground(attr_original,Color.BLACK);
		StyleConstants.setForeground(attr_original,Color.LIGHT_GRAY);
		
		this.menuAreaInit();
	}
	
	public void menuMapInit()
	{
		menu_map.put('A',"A. 股 金 账 务 处 理");
		menu_map.put('B',"B. 股 金 特 殊 交 易");
		menu_map.put('C',"C. 股 金 账 户 管 理");
		menu_map.put('D',"D. 股 金 查 询 打 印");
	}
	
	public void menuAreaInit()
	{
		menuMapInit();
		
		this.setBackground(Color.BLACK);
		this.setForeground(Color.LIGHT_GRAY);
		this.setFont(new Font("宋体", Font.PLAIN, 24));
		this.setText("\n\n\n\n");
		
		try {
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('A'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('B'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('C'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('D'))+"\b\n\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		this.setEditable(false);
		this.setFocusable(false);

		String tem = menu_map.get(new Character(current_select));
		doc.setCharacterAttributes(getStringPositionInTextArea(this,tem),tem.length(),attr_select,false);
	}
	
	public boolean checkInputSelect(char _input)
	{
		if(_input >= 'A' && _input <= 'D')
			return true;
		else if(_input >= 'a' && _input <= 'd')
			return true;
		return false;
	}
	
	public boolean checkIfHaveHigherMenu() {
		return true;
	}
	
	public MenuText getHigherLevelMenu() {
		return this.higher_level_menu;
	}
	
	public int checkSelection() {
		if(this.current_select == 'D')
			return MenuText.NEW_MENU;
		return MenuText.NOTHING;
	}
	
	public MenuText getAndSetLowerLeverMenu() {
		if(this.current_select == 'D')
		{
			MenuText _tem = new CapitalQueryPrintMenu();
			_tem.higher_level_menu = this;
			this.setVisible(false);
			return _tem;
		}
		
		return null;
	}

}
