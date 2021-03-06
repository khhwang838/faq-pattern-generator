package com.m2u.elbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 질문 생성기
 */
public class Qgenerator {

	private final static Map<Integer, String[]> keywordsMap = new LinkedHashMap<>();
	private final static Map<Integer, String> categoryMap = new LinkedHashMap<>();
	private final static Map<Integer, String> questionMap = new LinkedHashMap<>();
	private final static Map<Integer, String> responseMap = new LinkedHashMap<>();
	private final static Map<Integer, String[]> patternsMap = new LinkedHashMap<>();
	private final static String resultFilePath = "D:\\kihyun\\z50. projects\\KT_AIBOT\\M2U 내부 정리 자료\\autoGeneratedPatterns_integrated.csv";
	
	public static void main(String[] args) throws IOException {
		if ( args.length != 1) {
			printUsage();
			System.exit(0);
		}
		extractKeywords(args[0]);
		
		getPatterns(keywordsMap);
		
		// print extracted pattern
		for ( Integer key : patternsMap.keySet() ) {
			System.out.println(key + " : " + Arrays.toString(patternsMap.get(key)));
		}
		
		writeToFile(patternsMap);
	}
	
	private static void printUsage() {
		System.out.println("Need an excel file path to read.");
	}

	private static void writeToFile(Map<Integer, String[]> patterns) {
		String[] headers = new String[] {"카테고리", "질문", "검증샘플", "답변", "이미지", "이전질문", "추천질문", "링크"};
		
		// create data
		List<String[]> data = new ArrayList<>();
		data.add(headers);
		
		for ( Integer key : patterns.keySet() ) {
//			for ( String pattern : patterns.get(key) ) {
			for ( int ptrnIdx = 0; ptrnIdx < patterns.get(key).length ; ptrnIdx++ ) {
				String pattern = patterns.get(key)[ptrnIdx];
				
				String[] linedata = new String[headers.length];
				
				// 카테고리
//				linedata[0] = IConstants.Defaults.category;
				linedata[0] = categoryMap.get(key);
				// 패턴
				linedata[1] = escapeSpecialCharacters(pattern);
				// 검증샘플 
				linedata[2] = getVerifySample2(pattern);
				// test1
//				linedata[2] = getVerifySample(keywordsMap.get(key));
				
				// test2
//				if ( ptrnIdx == 0 ) {
//					linedata[2] = questionMap.get(key);
//				}else {
//					linedata[2] = getVerifySample2(pattern);
//				}
				
				// 답변
				linedata[3] = responseMap.get(key);
				linedata[4] = "";
				linedata[5] = "";
				linedata[6] = "";
				linedata[7] = "";
				
				data.add(linedata);
			}
		}
		
//		XlsFileWriter writer = new XlsFileWriter(headers, data);	// 엑셀 파일용
		CsvFileWriter writer = new CsvFileWriter(data);	// CSV 파일용
		writer.write(resultFilePath);
	}

	private static String getVerifySample2(String pattern) {
		pattern = pattern
					.replaceAll(Pattern.quote("{*}"), "")
					.replaceAll(Pattern.quote("+"), "")
					.replaceAll("  ", " ")
					.trim();
		// TODO : add more samples to check if there's (A|B|C|...) pattern
		
		return pattern;
	}
	
	private static String getVerifySample(String[] keywords) {
		StringBuilder sb = new StringBuilder();
		for ( String keyword : keywords ) {
			sb.append(keyword);
		}
		sb.append(IConstants.Defaults.separator);
		return sb.toString();
	}

	public static final List<List<Integer>> getSpaceLocations(final int n) {
        
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		for ( int r = 1; r <= n; r++) {
			int[] res = new int[r];
	        for (int i = 0; i < res.length; i++) {
	            res[i] = i + 1;
	        }
	        boolean done = false;
	        while (!done) {
	//            System.out.println(Arrays.toString(res));
	        	List<Integer> k = new ArrayList<>();
	            for ( int d : res ) {
	            	k.add(d);
	            }
	        	result.add(k);
	        	done = getNext(res, n, r);
	        }
	    }
        return result;
    }

    public static final boolean getNext(final int[] num, final int n, final int r) {
        int target = r - 1;
        num[target]++;
        if (num[target] > ((n - (r - target)) + 1)) {
            // Carry the One
            while (num[target] > ((n - (r - target)))) {
                target--;
                if (target < 0) {
                    break;
                }
            }
            if (target < 0) {
                return true;
            }
            num[target]++;
            for (int i = target + 1; i < num.length; i++) {
                num[i] = num[i - 1] + 1;
            }
        }
        return false;
    }
    
	private static void getPatterns(Map<Integer, String[]> keywordsMap) {
		// TODO : 키워드 순서를 다르게 배열한 경우도 패턴으로 추출할 수 있도록....--> 조사까지 포함된 키워드가 있어야 함.
		
		// 동일한 셀에서 구분자 사용 (구분자 : ";")
		
		StringBuilder sb = new StringBuilder();
		
		for ( Integer key : keywordsMap.keySet() ) {
		
			String[] keywords = keywordsMap.get(key);
			int patternCnt = (int) Math.pow(2, keywords.length-1);
			String[] patterns = new String[patternCnt];
			
			// pattern 만들기
			List<List<Integer>> r = getSpaceLocations(keywords.length-1);
			// 첫번 째 패턴은 띄어쓰기 없이 다 붙여쓴 것
			sb.append(IConstants.WildCards.asta);
			sb.append(IConstants.WildCards.space);
			for ( int j = 0 ; j < keywords.length ; j++ ) {
				sb.append(keywords[j]);
				sb.append(IConstants.WildCards.plus);
			}
			sb.append(IConstants.WildCards.space);
			sb.append(IConstants.WildCards.asta);
			patterns[0] = sb.toString();
			sb.setLength(0);
			
			for ( int i = 0 ; i < patterns.length - 1; i++ ) {
				List<Integer> spLoc = r.get(i);
				
				sb.append(IConstants.WildCards.asta);
				sb.append(IConstants.WildCards.space);
			
				for ( int j = 0 ; j < keywords.length ; j++ ) {
					
					sb.append(keywords[j]);
					sb.append(IConstants.WildCards.plus);
					
					if ( spLoc.contains(j+1) && j < keywords.length-1) {
						sb.append(IConstants.WildCards.space);
						sb.append(IConstants.WildCards.asta);
						sb.append(IConstants.WildCards.space);
					}
					
				}
				sb.append(IConstants.WildCards.space);
				sb.append(IConstants.WildCards.asta);
				patterns[i+1] = sb.toString();
				
				sb.setLength(0);
			}
			patternsMap.put(key, patterns);
		}
	}


	private static void extractKeywords(String filepath) throws IOException {
		// 파일을 읽기위해 엑셀파일을 가져온다
		FileInputStream fis = new FileInputStream(filepath);
		HSSFWorkbook workbook = new HSSFWorkbook(fis);
		int rowIndex = 0;
		int columnindex = 0;
		// 시트 수 (첫번째에만 존재하므로 0을 준다)
		// 만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
		HSSFSheet sheet = workbook.getSheet(IConstants.faqSheetName);	// FAQ 이름의 시트 열기
		// 행의 수
		int rows = sheet.getPhysicalNumberOfRows();
		
		// get keyword index
		HSSFRow row = sheet.getRow(rowIndex);
		int cells = row.getLastCellNum();
		
		int category1Index = -1;
		int questionIndex = -1;
		int keywordIndex = -1;
		int responseIndex = -1;
		
		// 질문, 답변, 키워드 컬럼의 인덱스 구하기
		for (columnindex = 0; columnindex <= cells; columnindex++) {
			// 셀값을 읽는다
			HSSFCell cell = row.getCell(columnindex);
			
			if ( cell != null ) {
				
				if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING 
						&& IConstants.catetory1.equals( cell.getStringCellValue()) ) {
					category1Index = columnindex;
				}
				if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING 
						&& IConstants.keyword.equals( cell.getStringCellValue()) ) {
					keywordIndex = columnindex;
				}
				if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING 
						&& IConstants.question.equals( cell.getStringCellValue()) ) {
					questionIndex = columnindex;
				}
				if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING 
						&& IConstants.response.equals( cell.getStringCellValue()) ) {
					responseIndex = columnindex;
				}
			}
			if ( category1Index >  -1 && questionIndex > -1 && keywordIndex > -1 && responseIndex > -1 ) break;
		}
		
		System.out.println("cateIdx : " + category1Index + ", qIdx : " + questionIndex + ", rIdx : " + responseIndex + ", kIdx : " + keywordIndex);
		
		final List<Integer> noKeywords = new ArrayList<>();
		
		String category = null;
		String question = null;
		String keyword = null;
		String response = null;
		
		for (rowIndex = 1; rowIndex < rows; rowIndex++) {
			// 첫 행은 헤더이므로 rowIndex=1(2행)부터 읽는다
			row = sheet.getRow(rowIndex);
			if (row != null) {
				
				HSSFCell cellC = row.getCell(category1Index);
				if ( cellC == null || cellC.getStringCellValue().equals("")) {
					// 카테고리(대분류) 컬럼에 내용이 없는 경우
					if ( category  == null ) {
						category = IConstants.Defaults.category;
					} else {
						// 병합된 셀의 경우 1번째 칸에만 내용이 들어있기 때문에 직전에 사용된 카테고리 재사용
					}
				} else {
					category = cellC.getStringCellValue();
				}
				
				HSSFCell cellQ = row.getCell(questionIndex);
				if ( cellQ == null || cellQ.getStringCellValue().equals("")) {
					// 질문 컬럼에 내용이 없으면 스킵
					continue;
				}
				question = cellQ.getStringCellValue();
				
				HSSFCell cellR = row.getCell(responseIndex);
				if ( cellR == null || cellR.getStringCellValue().equals("")) {
					// 답변 컬럼에 내용이 없으면 스킵
					continue;
				}
				response = cellR.getStringCellValue();
				
				HSSFCell cellK = row.getCell(keywordIndex);
				if ( cellK == null || cellK.getStringCellValue().equals("")) {
					// 질문, 답변 내용이 있는데 키워드 입력을 안한 경우에는 결과리포트에 보여준다
					noKeywords.add(rowIndex + 1);
					continue;
				}
				try {
					keyword = cellK.getStringCellValue();
				}catch(Throwable e) {
					System.out.println("rowIndex: " + rowIndex + ", " + (rowIndex+1) + "번째 행 읽을 때 오류 발생");
					throw e;
				}
				
				String[] keywords = keyword.split(Pattern.quote(","));
				for ( int i = 0 ; i < keywords.length ; i++ ) {
					keywords[i] = keywords[i].trim();
				}
				keywordsMap.put(rowIndex, keywords);
				categoryMap.put(rowIndex, category);
				questionMap.put(rowIndex, question);
				responseMap.put(rowIndex, response);
			}
		}
		report(keywordsMap.values(), noKeywords);
	}

	private static String escapeSpecialCharacters(String content) {
		content = content.replaceAll("&", "+");
		return content;
	}

	private static void report(Collection<String[]> result, List<Integer> noKeywords) {
		System.out.println("==========================================");
		System.out.println("Extracted Keywords Set : " + result.size());
		System.out.println("Not-Extracted Questions(NEQs) : " + noKeywords.size());
		System.out.println("NEQs rows : " + noKeywords);
		System.out.println("==========================================");
	}

}
