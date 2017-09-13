package menu;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

import gui.CustomedPage;
import gui.StockAccountInfoQuery;

public class CapitalQueryPrintMenu extends MenuText{

	public CapitalQueryPrintMenu()
	{
		this.menu_level = 3;
		this.menu_title = "�ɽ��ѯ��ӡ"; 
		
		doc =(DefaultStyledDocument)this.getDocument();
		StyleConstants.setBackground(attr_select,Color.LIGHT_GRAY);
		StyleConstants.setForeground(attr_select,Color.BLACK);
		StyleConstants.setBackground(attr_original,Color.BLACK);
		StyleConstants.setForeground(attr_original,Color.LIGHT_GRAY);
		
		this.menuAreaInit();
	}
	
	public void menuMapInit()
	{
		menu_map.put('A',"A. �ɽ��˻���Ϣ��ѯ");
		menu_map.put('B',"B. �ɽ��˻���ϸ��ѯ");
		menu_map.put('C',"C. �ɽ�ֺ��˻�����ѯ");
		menu_map.put('D',"D. �ɽ�����ѯ");
		menu_map.put('E',"E. �ɽ�ת��/�˹ɲ�ѯ");
	}
	
	public void menuAreaInit()
	{
		menuMapInit();
		
		this.setBackground(Color.BLACK);
		this.setForeground(Color.LIGHT_GRAY);
		this.setFont(new Font("����", Font.PLAIN, 24));
		this.setText("\n\n\n\n");
		
		try {
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('A'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('B'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('C'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('D'))+"\b\n\n", null);
			doc.insertString(doc.getLength(), "                          "+menu_map.get(new Character('E'))+"\b", null);
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
		if(_input >= 'A' && _input <= 'E')
			return true;
		else if(_input >= 'a' && _input <= 'e')
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
		if(this.current_select == 'A')
			return MenuText.NEW_PAGE;
		return MenuText.NOTHING;
	}
	
	public CustomedPage getAndSetNewPage() {
		StockAccountInfoQuery saiqr = new StockAccountInfoQuery();
		return saiqr;
	}

	
}
