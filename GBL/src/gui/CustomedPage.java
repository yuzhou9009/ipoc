package gui;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;

public class CustomedPage extends JPanel{

	protected BankGui bg;
	protected CustomedPage self;
	protected CustomedPage last_page;
	
	public void setCustomedFocus()
	{
		
	}
	
	public void setLastPage(CustomedPage _cp)
	{
		this.last_page = _cp;
	}
	
	public String getCurrentDate()
	{
		
		Date d = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");  
        return sdf.format(d);  
	}
	
	public void setBandGui(BankGui _bg)
	{
		this.bg = _bg;
	}
}
