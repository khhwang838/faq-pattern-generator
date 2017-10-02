package com.m2u.elbot;

public interface IConstants {

	String question = "질문";
	String keyword = "키워드";
	String response = "답변";
	String faqSheetName = "FAQ";
	
	interface Defaults {
		String category = "자동생성";
		String separator = ";";
	}
	
	interface WildCards {
		Object plus = "+";
		String plusWithSpace = "+ ";
		String asta = "{*}";
		String space = " ";
	}
	
}
