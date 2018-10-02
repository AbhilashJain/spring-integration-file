package com.spring.demo.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import com.spring.demo.config.DemoFileFilter;

public class DemoFileProcessor {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final String USER_ID = "User ID";
	private final String USER_FULL_NAME = "User Full Name";
	private final String USER_EMAIL = "User Email";
	private final String USER_ABSENT = "User Absent";
	
    public void process(Message<File> file) {
    	
    	final String fileName = (String) file.getHeaders().get("file_name");
		LOGGER.debug(fileName);
    	
		DemoFileFilter.processedFile.add(fileName);
		
    	File excelFile = (File)file.getHeaders().get("file_originalFile");
    	Map<String, Integer> header = new HashMap<>();
    	try(Workbook workbook = WorkbookFactory.create(excelFile)) {
    		Sheet worksheet = workbook.getSheetAt(0);
    		
			Iterator<Row> rowIterator = worksheet.rowIterator();
			if(rowIterator.hasNext()){
				Row row  = rowIterator.next();
				for(int i=0; i<4; i++)
					header.put(row.getCell(i).getStringCellValue(),i);
			}
			
			rowIterator.forEachRemaining(r-> {
				XSSFRow row = (XSSFRow) r;
				LOGGER.info(row.getCell(header.get(USER_ID)).getStringCellValue());
				LOGGER.info(row.getCell(header.get(USER_FULL_NAME)).getStringCellValue());
				LOGGER.info(row.getCell(header.get(USER_EMAIL)).getStringCellValue());
				LOGGER.info(row.getCell(header.get(USER_ABSENT)).getStringCellValue());
				
			});
			
			
		}catch (Exception ex) {
			LOGGER.error("Read failed.", ex);
			DemoFileFilter.processedFile.remove(fileName);
		}
    }
}
