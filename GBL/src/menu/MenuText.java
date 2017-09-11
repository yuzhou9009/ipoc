package menu;

import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;

import gui.CustomedPage;

public class MenuText extends JTextPane{

	HashMap<Character, String> menu_map = new HashMap<Character, String>();
	public int menu_level;
	public MenuText higher_level_menu;
	public String menu_title;
	DefaultStyledDocument doc;
	public static int NOTHING = 0;
	public static int NEW_MENU = 1;
	public static int NEW_PAGE = 2;

	SimpleAttributeSet attr_select = new SimpleAttributeSet();
	SimpleAttributeSet attr_original = new SimpleAttributeSet();
		
	char current_select = 'A'; 
	
	public void menuMapInit()
	{
	
	}
	
	public void menuAreaInit()
	{
		
	}
	
	public int getStringPositionInTextArea(JTextPane _jtp, String _s_tem)
	{
		//System.out.println(""+_jtp.getText());
		return _jtp.getText().indexOf(_s_tem);
	}
	
	public boolean checkInputSelect(char _input)
	{
		if(_input>='A' && _input<='Q')
		{
			return true;
		}//System.out.println("right");
		else if(_input>='a' && _input<='q')
		{
			return true;
		}	
		
		return false;
	}
	
	public boolean checkAndupdateCurrenSelect(char _new)
	{
		if(checkInputSelect(_new) == false)
			return false;
		if(_new >= 'a')
			_new -= 32;
			
		String tem = menu_map.get(new Character(current_select));
		//System.out.println(current_select+""+tem);
		doc.setCharacterAttributes(getStringPositionInTextArea(this,tem),tem.length(),attr_original,false);
		tem = menu_map.get(new Character(_new));
		doc.setCharacterAttributes(getStringPositionInTextArea(this,tem),tem.length(),attr_select,false);
		current_select = _new;
		return true;
	}
	
	public char getCurrentSelection()
	{
		return this.current_select;
	}

	public int checkSelection() {
		// TODO Auto-generated method stub
		return 0;
	}

	public MenuText getAndSetLowerLeverMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	public CustomedPage getAndSetNewPage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkIfHaveHigherMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	public MenuText getHigherLevelMenu() {
		// TODO Auto-generated method stub
		return null;
	}
}
