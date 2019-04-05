package com.pawlocki.exercise;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;


@Path("/HolidayWebService")
public class HolidayService {
	
	private static final String INITIAL_INFORMATION = "Provide date in format YYYY-MM-DD and two country codes";
	private static final int INDENT_FACTOR = 4;
	private static final String NAME_KEY = "name";
	private static final String DATE_KEY = "date";
	
	@GET
	public Response displayInitialInformation() {
		
		return Response.status(200).entity(INITIAL_INFORMATION).build();
	}
	
	@Path("/holidays")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAndDisplayFirstCountryCode(@QueryParam("code1") String firstCountryCode,
			@QueryParam("code2") String secondCountryCode, @QueryParam("date") String date) throws JSONException {		
		Holidays holiday = new Holidays();
		
		if(holiday.isProvidedDataCorrect(firstCountryCode, secondCountryCode, date)) {
			holiday.setParameters(getCountryCodeList(firstCountryCode, secondCountryCode), date);
			
			try {
				List<String> holidaysNames = holiday.getHolidayNames();
				JSONObject jsonObject = new JSONObject();
				for (int i = 0; i < holidaysNames.size(); i++) {
					jsonObject.put(NAME_KEY + (i + 1), holidaysNames.get(i));
				}
				jsonObject.put(DATE_KEY, holiday.getCommonDate());
				return Response.status(Response.Status.OK).entity(returnJSON(jsonObject)).build();
			} catch (Exception e) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}	
	}
	
	private List<String> getCountryCodeList(String code1, String code2){
		List<String> countryCodes = new ArrayList<String>();
		countryCodes.add(code1);
		countryCodes.add(code2);
		return countryCodes;
	}
	
	private String returnJSON(JSONObject jsonObject) {
		return jsonObject.toString(INDENT_FACTOR);
	}	
}
