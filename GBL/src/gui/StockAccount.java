package gui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class StockAccount {

	public String accountID;
	public String accountSubID;
	public int productType;
	public String productName;
	public String accountName;
	public String currentState;
	public float balance;
	public String freezeState;
	public float freezeAmount;
	public String drawType;
	public String lossState;
	public Date openDate;
	public String dividendsAccount;
	public String voucherNumber;
	public Date closeDate;
	public String IDType;
	public String ID;
	public int openSite;
	
	@SuppressWarnings("deprecation")
	public void setProperty(XSSFRow _row)
	{
         Cell _cell;
         
         _cell = _row.getCell(0);         
         this.accountID = _cell.getStringCellValue();  
         
         _cell = _row.getCell(1);  
         this.accountSubID = _cell.getStringCellValue();
         
         _cell = _row.getCell(2);  
    	this.productType = (int) _cell.getNumericCellValue();

    	_cell = _row.getCell(3);  
    	this.productName = _cell.getStringCellValue();

         _cell = _row.getCell(4);  		         
    	this.accountName = _cell.getStringCellValue();

         _cell = _row.getCell(5);  
    	this.currentState = _cell.getStringCellValue();

         _cell = _row.getCell(6);   
    	this.balance = (float) _cell.getNumericCellValue();

    	
    	_cell = _row.getCell(7, XSSFRow.RETURN_NULL_AND_BLANK);  
    	if(_cell == null)
    		this.freezeState = "";
    	else
    		this.freezeState = _cell.getStringCellValue();
         
         _cell = _row.getCell(8, XSSFRow.RETURN_NULL_AND_BLANK);    
    	this.freezeAmount = (float) _cell.getNumericCellValue();
    	
    	_cell = _row.getCell(9, XSSFRow.RETURN_NULL_AND_BLANK);  
    	this.drawType = _cell.getStringCellValue();

    	_cell = _row.getCell(10, XSSFRow.RETURN_NULL_AND_BLANK);
    	if(_cell == null)
    		this.lossState = "";
    	else
    		this.lossState = _cell.getStringCellValue();

    	_cell = _row.getCell(11, XSSFRow.RETURN_NULL_AND_BLANK);   
    	this.openDate = _cell.getDateCellValue();//;_cell.getStringCellValue();

    	_cell = _row.getCell(12, XSSFRow.RETURN_NULL_AND_BLANK);   
    	this.voucherNumber = _cell.getStringCellValue();
    	
    	_cell = _row.getCell(13, XSSFRow.RETURN_NULL_AND_BLANK);
    	if(_cell == null)
    		this.dividendsAccount = "";
    	else
    		this.dividendsAccount = _cell.getStringCellValue();

    	_cell = _row.getCell(14, XSSFRow.RETURN_NULL_AND_BLANK);
    	if(_cell == null)
    		this.closeDate = null;
    	else
    		this.closeDate = _cell.getDateCellValue();

    	_cell = _row.getCell(15, XSSFRow.RETURN_NULL_AND_BLANK);   
    	this.IDType = _cell.getStringCellValue();

    	_cell = _row.getCell(16, XSSFRow.RETURN_NULL_AND_BLANK);   
    	this.ID = _cell.getStringCellValue();

    	_cell = _row.getCell(17, XSSFRow.RETURN_NULL_AND_BLANK);   
    	this.openSite =  (int) _cell.getNumericCellValue();

	}
	
	public Object[] changeToObjectArray()
	{
		Object[] _tem = new Object[19];
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat   fnum  =   new  DecimalFormat("##0.00");   

		_tem[0] = accountID;
		_tem[1] = accountSubID;
		_tem[2] = productType;
		_tem[3] = productName;
		_tem[4] = accountName;
		_tem[5] = currentState;
		_tem[6] = fnum.format(balance);
		_tem[7] = freezeState;
		_tem[8] = fnum.format(freezeAmount);
		_tem[9] = drawType;
		_tem[10] = lossState;
		_tem[11] = formatter.format(openDate);
		_tem[12] = voucherNumber;
		_tem[13] = dividendsAccount;
		if(closeDate != null)
			_tem[14] = formatter.format(closeDate);
		_tem[15] = IDType;
		_tem[16] = ID;
		_tem[17] = openSite;
		
		return _tem;
	}
	
}
