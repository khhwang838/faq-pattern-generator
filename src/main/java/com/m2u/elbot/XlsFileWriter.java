package com.m2u.elbot;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XlsFileWriter {

	private final String[] headers;
	private final List<String[]> data;
	
	XlsFileWriter(String[] headers, List<String[]> data){
		this.headers = headers;
		this.data = data;
	}
	
	public void write(String filePath) {
		
		System.out.println("파일 쓰기 중... " + filePath);
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		//Sheet명 설정
		HSSFSheet sheet = workbook.createSheet("mySheet");
		HSSFRow row;
		
		/* xlsx 파일 출력시 선언
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("mySheet");
		*/

		//header row 생성
		row = sheet.createRow(0);
		//출력 cell 생성
		for ( int cellIdx = 0; cellIdx < headers.length ; cellIdx++ ) {
			row.createCell(cellIdx).setCellValue(headers[cellIdx]);
		}
				
		for ( int rowIdx = 1; rowIdx < data.size() ; rowIdx++ ) {
			//출력 row 생성
			row = sheet.createRow(rowIdx);
			
			String[] lineData = data.get(rowIdx);
			
			for ( int cellIdx = 0; cellIdx < lineData.length ; cellIdx++ ) {
				//출력 cell 생성
				row.createCell(cellIdx).setCellValue(lineData[cellIdx]);
			}
		}
		
		// 출력 파일 위치및 파일명 설정
		FileOutputStream outFile;
		try {
			outFile = new FileOutputStream(filePath);
			workbook.write(outFile);
			outFile.close();
			
			System.out.println(">>>> 파일생성 완료");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
