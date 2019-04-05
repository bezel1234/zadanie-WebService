package com.pawlocki.exercise;

public class HolidayApiStaus {

	public static final int SUCCESS = 200;
	public static final int FAILURE_HERE = 400;
	public static final int WRONG_API_KEY = 401;
	public static final int PAYMENT_REQUIRED = 402;
	public static final int FORBIDDEN = 403;
	public static final int RATE_LIMIT = 429;
	public static final int CRUSH = 500;
	
	public static String checkStatus(int status) {
		String message = "";
		switch(status) {
		
		case SUCCESS:
			message = SUCCESS_MESSAGE;
			break;
			
		case FAILURE_HERE:
			message = FAILURE_HERE_MESSAGE;
			break;
			
		case WRONG_API_KEY:
			message = WRONG_API_KEY_MESSAGE;
			break;
			
		case PAYMENT_REQUIRED:
			message = PAYMENT_REQUIRED_MESSAGE;
			break;
			
		case FORBIDDEN:
			message = FORBIDDEN_MESSAGE;
			break;
			
		case RATE_LIMIT:
			message = RATE_LIMIT_MESSAGE;
			break;
			
		case CRUSH:
			message = CRUSH_MESSAGE;
			break;
		}
		return message;
	}
	
	private static final String SUCCESS_MESSAGE = "Everything is ok";
	private static final String FAILURE_HERE_MESSAGE = "Something is wrong on your end";
	private static final String WRONG_API_KEY_MESSAGE = "Unauthorized attempt. Check API key.";
	private static final String PAYMENT_REQUIRED_MESSAGE = "Only historiacal dates are available";
	private static final String FORBIDDEN_MESSAGE = "API works only with HTTPS protocol";
	private static final String RATE_LIMIT_MESSAGE = "Limit for free account has been exceeded";
	private static final String CRUSH_MESSAGE = "Crush on your end";
}
