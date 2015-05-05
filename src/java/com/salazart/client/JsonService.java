package com.salazart.client;



import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.json.stream.JsonParser.Event;

/**
 * This class generating json response with input data
 * @author home
 *
 */
public class JsonService {
	private static final int COMMAND_LINE = 0;
	private static final int SIZE_FIELD = 10;
	private static final String EVENT_INIT = "init";
	private static final String EVENT_SHOT = "shot";
	
    public JsonObject generateJsonResponse(int [][] generatedMap) {
		JsonObject jsonObject = Json.createObjectBuilder()
                .add("start_position", getJsonArrayField(generatedMap))
                .build();
		return generateJsonResponse(jsonObject, EVENT_INIT);
    }
    
    public JsonObject generateJsonResponse(int x, int y) {
    	JsonObject jsonObject = Json.createObjectBuilder()
				.add("x", x)
				.add("y", y).build();
		return generateJsonResponse(jsonObject, EVENT_SHOT);
    }
	
	private JsonObject generateJsonResponse(JsonObject jsonObject, String typeEvent){
		JsonObject jsonMessage = Json.createObjectBuilder()
				.add("event", Json.createObjectBuilder()
								.add("name", typeEvent)
								.add("value", jsonObject))
				.build();
		return jsonMessage;
	}
	
    private JsonArray getJsonArrayField(int[][] field) {
    	JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (int i = 0; i < SIZE_FIELD; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                line.append(field[i][j]);
            }
            jsonArrayBuilder.add(line.toString());
        }
        return jsonArrayBuilder.build();
    }
    
    /**
	 * This method returning field of madedshots
	 * @return
	 */
	public int[][] setMadeShotsMap(String message) {
		ArrayList<String> json = parseJsonMessage(message);
		int[][] madeShotsMap = new int[10][10];
		if(json.size() <= 1){
			return madeShotsMap;
		} else {
			String line;
			for (int i = 1; i < 11; i++) {
				line = json.get(i);
				for (int j = 0; j < 10; j++) {
					int cell = Character.getNumericValue(line.charAt(j));
					madeShotsMap[i - 1][j] = cell;
				}
			}
			return madeShotsMap;
		}
	}
	
	/**
	 * This method returning field of dead ship
	 * @return
	 */
	public int[][] setShotsResultsMap(String message) {
		ArrayList<String> json = parseJsonMessage(message);
		int[][] shotsResultsMap = new int[10][10];
		if(json.size() <= 1){
			return shotsResultsMap;
		} else {
			String line;
			for (int i = 11; i < 21; i++) {
				line = json.get(i);
				for (int j = 0; j < 10; j++) {
					int cell = Character.getNumericValue(line.charAt(j));
					shotsResultsMap[i - 11][j] = cell;
				}
			}
			return shotsResultsMap;
		}
	}
	
	/**
	 * This method returning command for clients logic 
	 * @return
	 */
	public String getCommand(String message){
		ArrayList<String> command = parseJsonMessage(message);
		if(!command.isEmpty()){
			return command.get(COMMAND_LINE);
		} else {
			return new String();
		}
	}
    
    private ArrayList<String> parseJsonMessage(String message) {
		ArrayList<String> command = new ArrayList<String>();
		JsonParserFactory factory = Json.createParserFactory(null);
		JsonParser parser = factory.createParser(new StringReader(message));
		
		while (parser.hasNext()) {
			Event event = parser.next();
			label: switch (event) {
			case KEY_NAME:
				if (parser.getString().equals("name")) {
					parser.next();
					command.add(parser.getString());
				}
				break;
			case START_ARRAY:
				while (parser.hasNext()) {
					switch (parser.next()) {
					case VALUE_STRING:
						command.add(parser.getString());
						break;
					case END_ARRAY:
						break label;
					}
				}
				break;
			}
		}
		return command;
	}
}