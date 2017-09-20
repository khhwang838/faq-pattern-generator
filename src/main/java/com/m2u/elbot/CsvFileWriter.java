package com.m2u.elbot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.opencsv.CSVWriter;

public class CsvFileWriter {

	private final List<String[]> data;
	
	CsvFileWriter(List<String[]> data){
		this.data = data;
	}
	
	public void write(String filePath) {
		System.out.println("파일 쓰기 중... " + filePath);
		
		
		try {
//            CSVWriter cw = new CSVWriter(new FileWriter(filePath), ',', '"');
            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), "euc-kr"), ',', '"');
            try {
                cw.writeAll(data);
                System.out.println(">>>> 파일생성 완료");
            } finally {
                cw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
	}
}
