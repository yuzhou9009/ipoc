package edu.bupt.ipoc.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExecleOutputTool {

	FileOutputStream out;
	Workbook wb;
	Sheet s;
	
	public ExecleOutputTool(String file_path)
	{
		try {
			out = new FileOutputStream("workbook.xls");
			wb = new HSSFWorkbook();
			s = wb.createSheet();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
