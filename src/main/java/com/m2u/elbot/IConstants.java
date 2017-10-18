package com.m2u.elbot;

public interface IConstants {

	String catetory1 = "대분류";
	String catetory2 = "중분류";
	String catetory3 = "소분류";

	String question = "질문";
	String keyword = "키워드";
	String response = "답변";
	String faqSheetName = "FAQ";
	
	interface Defaults {
		String category = "기타";
		String separator = ";";
	}
	
	interface WildCards {
		Object plus = "+";
		String plusWithSpace = "+ ";
		String asta = "{*}";
		String space = " ";
	}
	
}
