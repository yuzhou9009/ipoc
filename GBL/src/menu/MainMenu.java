package menu;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;

public class MainMenu extends MenuText{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainMenu()
	{
		this.menu_level = 1;
		this.menu_title = "综合网络业务系统";
		
		doc =(DefaultStyledDocument)this.getDocument();
		StyleConstants.setBackground(attr_select,Color.LIGHT_GRAY);
		StyleConstants.setForeground(attr_select,Color.BLACK);
		StyleConstants.setBackground(attr_original,Color.BLACK);
		StyleConstants.setForeground(attr_original,Color.LIGHT_GRAY);
		
		this.menuAreaInit();
	}
	
	public void menuMapInit()
	{
		menu_map.put('A',"A. 存 款 业 务");
		menu_map.put('B',"B. 贷 款 业 务");
		menu_map.put('C',"C. 外 围 业 务");
		menu_map.put('D',"D. 代 理 业 务");
		menu_map.put('E',"E. 柜 员 业 务");
		menu_map.put('F',"F. 公 用 交 易");		
		menu_map.put('G',"G. 卡 管 理 业 务");
		menu_map.put('H',"H. 客 户 信 息");
		menu_map.put('I',"I. 总 账 内 部 账");
		menu_map.put('J',"J. 凭 证 现 金");
		menu_map.put('K',"K. 结 算 业 务");
		menu_map.put('L',"L. 担 保 品 业 务");		
		menu_map.put('M',"M. 外 汇 业 务");
		menu_map.put('N',"N. 股 金 管 理");
		menu_map.put('O',"O. 资 产 管 理");
		menu_map.put('P',"P. 中 间 业 务");
		menu_map.put('Q',"Q. 开 发 工 具");
	}
	
	public void menuAreaInit()
	{
		menuMapInit();
		
		this.setBackground(Color.BLACK);
		this.setForeground(Color.LIGHT_GRAY);
		this.setFont(new Font("宋体", Font.PLAIN, 24));
		this.setText("\n\n\n\n");
		
		try {
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('A'))+"            \b"+menu_map.get(new Character('M'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('B'))+"            \b"+menu_map.get(new Character('N'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('C'))+"            \b"+menu_map.get(new Character('O'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('D'))+"            \b"+menu_map.get(new Character('P'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('E'))+"            \b"+menu_map.get(new Character('Q'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('F'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('G'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('H'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('I'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('J'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('K'))+"\b\n", null);
			doc.insertString(doc.getLength(), "          "+menu_map.get(new Character('L'))+"\b\n", null);
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
		if(_input >= 'A' && _input <= 'Q')
			return true;
		else if(_input >= 'a' && _input <= 'q')
			return true;
		return false;
	}
	
	public int checkSelection() {
		if(this.current_select == 'N')
			return MenuText.NEW_MENU;
		return MenuText.NOTHING;
	}
	
	public MenuText getAndSetLowerLeverMenu() {
		if(this.current_select == 'N')
		{
			MenuText _tem = new CapitalManagementMenu();
			_tem.higher_level_menu = this;
			this.setVisible(false);
			return _tem;
		}
		
		return null;
	}

}
