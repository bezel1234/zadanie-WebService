package com.pawlocki.exercise;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.agogs.holidayapi.api.APIConsumer;
import com.github.agogs.holidayapi.api.impl.HolidayAPIConsumer;
import com.github.agogs.holidayapi.model.HolidayAPIResponse;
import com.github.agogs.holidayapi.model.QueryParams;
import com.github.agogs.holidayapi.model.Holiday;

public class Holidays {

	private static final String CONNECTION_URL = "https://holidayapi.com/v1/holidays";
	private static final String API_KEY = "60a56ae1-474f-447d-9145-89689fddc3ab";
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String SPECIAL_CHARS_PATTERN = ("[!@#$%&*()_+=|<>?{}\\[\\]~]");
	private static final int HOLIDAY_INDEX = 0;
	
	private APIConsumer apiConsumer;
	private QueryParams queryParams;
	
	private LocalDate providedDate;
	private LocalDate commonDate;
	
	private List<String> countryCodes;
	private int status;
	
	public Holidays() {
		apiConsumer = new HolidayAPIConsumer(CONNECTION_URL);
		queryParams = new QueryParams();
	}
	
	public boolean isProvidedDataCorrect(String code1, String code2, String date) {
		return (isCountryCodeProvidedCorrect(code1) && isCountryCodeProvidedCorrect(code2) && isDateCorrect(date));
	}
	
	private boolean isCountryCodeProvidedCorrect(String code) {
		if(!isCountryCodeExisting(code)) {
			return false;
			
		} else if(code == null) {
			return false;
			
		} else if(code.trim().isEmpty()) {
			return false;
			
		} else if(hasSpecialChars(code)) {
			return false;
			
		} else if(!areLettersCorrect(code)) {
			return false;
		
		} else {
			return true;
		}
	}
	
	private boolean areLettersCorrect(String code) {
		String subCode = code.toUpperCase();
		return code.equals(subCode);
	}
	
	private boolean hasSpecialChars(String code) {
		Pattern specialCharsPattern = Pattern.compile(SPECIAL_CHARS_PATTERN);
		Matcher specialCharsMatcher = specialCharsPattern.matcher(code);
		return specialCharsMatcher.find();
	}
	
	private boolean isCountryCodeExisting(String code) {
		for(QueryParams.Country country : QueryParams.Country.values()) {
			if(country.code().contentEquals(code)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isDateCorrect(String date) {
		return (isDateValid(date) && checkDate(date));
	}
	
	private boolean checkDate(String date) {
		return LocalDate.parse(date).isBefore(LocalDate.now());
	}
	
	public boolean isDateValid(String date) {
		try {
            DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
	}
	
	public void setParameters(List<String> countryCodes, String date) {
		this.countryCodes = countryCodes;
		providedDate = LocalDate.parse(date);
		commonDate = null;
	}
	
	public LocalDate getCommonDate() throws Exception {
		if(commonDate != null) {
			return commonDate;
		} else {
			throw new Exception(HolidayApiStaus.checkStatus(status));
		}
	}

	public List<String> getHolidayNames() throws Exception {
		return commonHolidaysForGivenDateAndCountry();
	}
	
	private List<String> commonHolidaysForGivenDateAndCountry() throws Exception{
		List<String> holidays = new ArrayList<String>();
		checkHolidayExistenceForCommonDate();
		if(commonDate != null) {
			for (String code : countryCodes) {
				String name = new String(getHolidays(code, commonDate).get(HOLIDAY_INDEX).getName().getBytes(), "UTF-8");
				holidays.add(name);
			}
		} else {
			throw new Exception(HolidayApiStaus.checkStatus(status));
		}
		return holidays;
	}
	
	private void checkHolidayExistenceForCommonDate() throws Exception{
		boolean notEmpty = false;
		for(LocalDate currentDate = providedDate; currentDate.isBefore(LocalDate.now()); currentDate = currentDate.plusDays(1)) {
			for (String code : countryCodes) {
				status = getStatus(code, currentDate);
				if(status == HolidayApiStaus.SUCCESS) {
					if(!getHolidays(code, currentDate).isEmpty()) {

						notEmpty = true;
					} else {
						notEmpty = false;
					}	
				} else {
					break;
				}
			}
			if(notEmpty) {
				
				commonDate = currentDate;
				break;
			} else {
				if(status != HolidayApiStaus.SUCCESS && status != HolidayApiStaus.PAYMENT_REQUIRED) {
					throw new Exception(HolidayApiStaus.checkStatus(status));					
				}
			}
		}
	}
	
	private List<Holiday> getHolidays(String countryCode, LocalDate date) throws IOException{
		HolidayAPIResponse apiResponse = apiConsumer.getHolidays(currentCountryParams(countryCode, date));
		return apiResponse.getHolidays();
	}
	
	private int getStatus(String countryCode, LocalDate date) throws IOException {
		HolidayAPIResponse apiResponse = apiConsumer.getHolidays(currentCountryParams(countryCode, date));
		return apiResponse.getStatus();
	}
	
	private QueryParams currentCountryParams(String countryCode, LocalDate date) {
		return queryParams.key(API_KEY).country(getCountry(countryCode))
				.year(date.getYear()).month(date.getMonth().getValue()).day(date.getDayOfMonth());
	}
	
	private QueryParams.Country getCountry(String countryCode) {
		for(QueryParams.Country country : QueryParams.Country.values()) {
			if(country.code().contentEquals(countryCode)) {
				return country;
			}
		}
		return null;
	}
}