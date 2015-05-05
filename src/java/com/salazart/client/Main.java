package com.salazart.client;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Main {
	private static final String EVENT_INIT = "init";
	private static final String EVENT_SHOT = "shot";

	public static void main(String[] args) {
		JsonReader jsonReader = Json.createReader(System.in);
        JsonObject inJsonObject = jsonReader.readObject();
        jsonReader.close();
        
		JsonService jsonService = new JsonService();
        int[][] madeShotsMap = jsonService.setMadeShotsMap(inJsonObject.toString());
		int[][] shotsResultsMap = jsonService.setShotsResultsMap(inJsonObject.toString());
		
		switch (jsonService.getCommand(inJsonObject.toString())) {
		case EVENT_INIT:
			ShipsMapService shipsMap = new ShipsMapService();
			int [][] generatedMap = shipsMap.generateShipsMap();
			
			JsonService shipsPositions = new JsonService();
			System.out.println(shipsPositions.generateJsonResponse(generatedMap));
		case EVENT_SHOT:
			ShotService shot = new ShotService(madeShotsMap, shotsResultsMap);
			System.out.println(shot.shot());
		}
	}
}
