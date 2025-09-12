package com.parameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	public static String[][] readData() {
		String[][] data = null;
		String filename = "src\\test\\resources\\Inputdata\\HotelbookingWorksheet.xlsx";
		try {
			FileInputStream fis = new FileInputStream(filename);
			XSSFWorkbook book = new XSSFWorkbook(fis);
			XSSFSheet sheet = book.getSheet("Sheet1");
			int rowCount = sheet.getLastRowNum();
			int cellCount = sheet.getRow(0).getLastCellNum();
			System.out.println("Row count : " + rowCount);
			DataFormatter df = new DataFormatter();
			XSSFCell cell;
			data = new String[rowCount][cellCount];
			for (int rowNo = 1; rowNo <= rowCount; rowNo++) {

				for (int cellNo = 0; cellNo < cellCount; cellNo++) {
					cell = sheet.getRow(rowNo).getCell(cellNo);
					data[rowNo - 1][cellNo] = df.formatCellValue(cell);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

}
