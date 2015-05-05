package com.salazart.server;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParserFactory;

public class ServerLogic {
	private static final int SIZE_FIELD = 10;
	private static final String EVENT_INIT = "init";
	private static final String EVENT_SHOT = "shot";
	
	private ArrayList buisnessData;
	private boolean flagStartGame = false;
	
	private int[][] positionOfShips = new int[10][10];
	private int[][] madeShotsMap = new int[10][10];
	private int[][] shotsResultsMap = new int[10][10];
	
    public JsonObject message(JsonObject message) {
    	if(message == null && flagStartGame == false){
    		return generateJsonResponse(null, null);
    	} else {
    		flagStartGame = true;
    		loadResult();
    		return factoryEvent(message);
    	}
    }
    
    private JsonObject factoryEvent(JsonObject message){
        try {
            ArrayList<String> parsedJson = parseJsonMessage(message.toString());
            switch (parsedJson.get(0)) {
                case EVENT_INIT:
                	if(processEventInit(parsedJson)){
                		return generateJsonResponse(madeShotsMap, shotsResultsMap);
                	} else {
                		return null;
                	}
                	
                case EVENT_SHOT:
                    int x = Integer.parseInt(parsedJson.get(1));
                    int y = Integer.parseInt(parsedJson.get(2));
                    ArrayList enemyData = (ArrayList) buisnessData;
                    int[][] enemyShips = (int[][]) enemyData.get(0);
                    if (x >= 0 && y >= 0 && x < 10 && y < 10) {
                        shotsResultsMap = processShot(x, y, enemyShips, shotsResultsMap);
                        madeShotsMap[x][y] = 1;
                        storeResult();
                    } else {
                    	//result 0
                    }
                    if (!isPlayerWin(shotsResultsMap)) {
                        return generateJsonResponse(madeShotsMap, shotsResultsMap);
                    } else {
                        return null;
                    }
            }

        } catch (Exception ex) {
            return null;
        }
        return null;
    }
    
    /**
     * This method processing event "INIT", check shipsposition and return the respons to client
     * @param parsedJson
     * @return
     */
    private boolean processEventInit(ArrayList<String> parsedJson){
    	int[][] field = shipPositionsOfPlayer(parsedJson);
        if (checkShipsPosition(field)) {
            positionOfShips = field;
            madeShotsMap = new int[10][10];
            shotsResultsMap = new int[10][10];
            for (int[] row : madeShotsMap)
            	Arrays.fill(row, 0);
            
            for (int[] row : shotsResultsMap)
                Arrays.fill(row, 0);

            storeResult();
            return true;
        } else {
            return false;
        }
    }
    
    private int[][] shipPositionsOfPlayer(ArrayList<String> json) {
        int[][] field = new int[10][10];
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 1; i < json.size(); i++) {
            lines.add(json.get(i));
        }
        for (int i = 0; i < 10; i++) {
            String line = lines.get(i);
            for (int j = 0; j < 10; j++) {
                int cell = Character.getNumericValue(line.charAt(j));
                field[i][j] = cell;
            }
        }
        return field;
    }
    
    public ArrayList<String> parseJsonMessage(String message) throws Exception {
        ArrayList list = new ArrayList<>();
        JsonParserFactory factory = Json.createParserFactory(null);
        JsonParser parser = factory.createParser(new StringReader(message));
        while (parser.hasNext()) {
            Event event = parser.next();
            switch (event) {
                case KEY_NAME: {
                    if (parser.getString().equals("name")) {
                        parser.next();
                        list.add(parser.getString());
                        parser.next();
                    }
                }
                break;
                case VALUE_NUMBER: {
                    list.add("" + parser.getInt());
                    parser.next();
                }
                break;
                case START_ARRAY: {
                    while (parser.hasNext()) {
                        event = parser.next();
                        switch (event) {
                            case VALUE_STRING:
                                list.add(parser.getString());
                                break;
                            default:
                                parser.next();
                        }
                    }
                }
                break;
                default:
                    break;
            }
        }
        return list;
    }

	
    public JsonObject generateJsonResponse(int[][] madeShotsMap, int[][] shotsResultsMap) {
    	
    	if(madeShotsMap == null || shotsResultsMap == null){
    		JsonObject jsonMessage = Json.createObjectBuilder()
                    .add("event", Json.createObjectBuilder()
                            .add("name", EVENT_INIT)
                            .add("value", ""))
                    .build();
    		return jsonMessage;
    	} else {
    		JsonObject jsonObject = Json.createObjectBuilder()
        			.add("shot_array", Json.createArrayBuilder()
        					.add(getJsonArrayField(madeShotsMap))
        					.add(getJsonArrayField(shotsResultsMap))).build();
    		
    		JsonObject jsonMessage = Json.createObjectBuilder()
                    .add("event", Json.createObjectBuilder()
                            .add("name", EVENT_SHOT)
                            .add("value", jsonObject))
                    .build();
    		return jsonMessage;
    	}
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
    
    private int[][] processShot(int x, int y, int[][] enemyShips, int[][] shotsResultsMap) {
        if (enemyShips[x][y] == 1 && shotsResultsMap[x][y] == 0) {
            shotsResultsMap = isShipDead(x, y, enemyShips, shotsResultsMap);
            return shotsResultsMap;
        } else {
            return shotsResultsMap;
        }
    }

    private int[][] isShipDead(int x, int y, int[][] enemyShips, int[][] shotsResultsMap) {
        int result = 2;
        ArrayList shipCoordinats = getShipCoordinats(x, y, enemyShips);
        if (shipCoordinats.size() > 1) {
            for (Object cell : shipCoordinats.subList(1, shipCoordinats.size())) {
                int[] xy = (int[]) cell;
                if (shotsResultsMap[xy[0]][xy[1]] == 0) {
                    result = 1;
                }
            }
        }
        if (result == 2) {
            for (Object cell : shipCoordinats) {
                int[] xy = (int[]) cell;
                shotsResultsMap[xy[0]][xy[1]] = 2;
            }
        } else {
            shotsResultsMap[x][y] = 1;
        }
        return shotsResultsMap;
    }

    private void storeResult() {
    	buisnessData = new ArrayList();
        buisnessData.add(positionOfShips);
        buisnessData.add(madeShotsMap);
        buisnessData.add(shotsResultsMap);
    }
    
    private void loadResult(){
    	if (buisnessData != null) {
            positionOfShips = (int[][]) buisnessData.get(0);
            madeShotsMap = (int[][]) buisnessData.get(1);
            shotsResultsMap = (int[][]) buisnessData.get(2);
        }
    }

    private boolean checkShipsPosition(int[][] field) {
        ArrayList allShipCoordinats = new ArrayList();
        int countOfSingleDeck = 0;
        int countOfDoubleDeck = 0;
        int countOfTripleDeck = 0;
        int countOfFourDeck = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int[] cell = {i, j};
                if (field[i][j] == 1 && !isCellProcessedBefore(cell, allShipCoordinats)) {
                    ArrayList shipCoordinats = getShipCoordinats(i, j, field);
                    allShipCoordinats.addAll(shipCoordinats);
                    if (shipCoordinats.size() == 1) {
                        countOfSingleDeck++;
                    }
                    if (shipCoordinats.size() == 2) {
                        countOfDoubleDeck++;
                    }
                    if (shipCoordinats.size() == 3) {
                        countOfTripleDeck++;
                    }
                    if (shipCoordinats.size() == 4) {
                        countOfFourDeck++;
                    }
                }
            }
        }
        return countOfSingleDeck == 4 && countOfDoubleDeck == 3 && countOfTripleDeck == 2 && countOfFourDeck == 1;
    }

    private boolean isCellProcessedBefore(int[] cell, ArrayList allShipCoordinats) {
        for (Object coordinats : allShipCoordinats) {
            int[] xy = (int[]) coordinats;
            if (Arrays.equals(cell, xy)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList getShipCoordinats(int x, int y, int[][] field) {
    	WriteGameField.writeGameField("field", field);
    	int MAX_DECK_SHIP = 4;
    	int MIN_DECK_SHIP = 1;
        boolean isVertical = false;
        boolean isHorizontal = false;
        
        ArrayList shipCoordinats = new ArrayList<int[]>();
        int[] firstCell = {x, y};
        shipCoordinats.add(firstCell);
        
        System.out.println("x = " + x + " y = " + y + " value = " + field[x][y]);
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (y + i < SIZE_FIELD && field[x][y + i] == 1) {
                int[] cell = {x, y + i};
                shipCoordinats.add(cell);
                System.out.println(1);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (y - i >= 0 && field[x][y - i] == 1) {
                int[] cell = {x, y - i};
                shipCoordinats.add(cell);
                System.out.println(2);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (x + i < SIZE_FIELD && field[x + i][y] == 1) {
                int[] cell = {x + i, y};
                shipCoordinats.add(cell);
                System.out.println(3);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (x - i >= 0 && field[x - i][y] == 1) {
                int[] cell = {x - i, y};
                shipCoordinats.add(cell);
                System.out.println(4);
            }
        }
        
        isVertical = findLocation(shipCoordinats, 0, x);
        isHorizontal = findLocation(shipCoordinats, 1, y);
        
        System.out.println("size = " + shipCoordinats.size());
        System.out.println("isVertical = " + isVertical);
        System.out.println("isHorizontal = " + isHorizontal);
        if (shipCoordinats.size() > 1) {
            if (isVertical && isHorizontal) {
                return new ArrayList();
            }
        }
        
        return shipCoordinats;
    }
    
    private boolean findLocation(List<int[]> shipCoordinats, int indexCoordinate, int valueCoordinate){
    	for (int[] cell : shipCoordinats) {
            if (cell[indexCoordinate] != valueCoordinate) {
            	return true;
            }
        }
    	return false;
    }

    //возвращает все координаты ячеек одного корабля
    /*private ArrayList getShipCoordinatsd(int x, int y, int[][] field) {
        boolean isVertical = false;
        boolean isHorizontal = false;
        ArrayList shipCoordinats = new ArrayList();
        ArrayList<Integer> xCoords = new ArrayList<>();
        ArrayList<Integer> yCoords = new ArrayList<>();
        int[] firstCell = {x, y};
        shipCoordinats.add(firstCell);
        
        //check vertically down
        for (int i = 1; i < 4; i++) {
        	//если следующая ячейка предполагаемого корабля не выходит за приделы игрового поля
        	//и есть продолжение корабля
            if (y + i < 10 && field[x][y + i] == 1) {
                int[] cell = {x, y + i};
                xCoords.add(x);
                yCoords.add(y + i);
                shipCoordinats.add(cell);
            } else {
                break;
            }
        }
        
        //check vertically up
        for (int i = 1; i < 4; i++) {
            if (y - i >= 0 && field[x][y - i] == 1) {
                int[] cell = {x, y - i};
                xCoords.add(x);
                yCoords.add(y - i);
                shipCoordinats.add(cell);
            } else {
                break;
            }
        }
        
      //check horizontally right
        for (int i = 1; i < 4; i++) {
            if (x + i < 10 && field[x + i][y] == 1) {
                int[] cell = {x + i, y};
                xCoords.add(x + i);
                yCoords.add(y);
                shipCoordinats.add(cell);
            } else {
                break;
            }
        }
        
      //check horizontally left
        for (int i = 1; i < 4; i++) {
            if (x - i >= 0 && field[x - i][y] == 1) {
                int[] cell = {x - i, y};
                xCoords.add(x - i);
                yCoords.add(y);
                shipCoordinats.add(cell);
            } else {
                break;
            }
        }
        for (Integer n : xCoords) {
            if (n != x) {
                isVertical = true;
            }
        }
        for (Integer n : yCoords) {
            if (n != y) {
                isHorizontal = true;
            }
        }
        //ship can't be horizontal and vertical at the same time
        if (shipCoordinats.size() > 1) {
            if (isVertical && isHorizontal) {
                return new ArrayList();
            }
        }
        return shipCoordinats;
    }*/

    private boolean isPlayerWin(int[][] shotsResultsMap) {
        int deadShipsCount = 0;
        for (int[] line : shotsResultsMap) {
            for (int cell : line) {
                if (cell == 2) {
                    deadShipsCount++;
                }
            }
        }
        return deadShipsCount == 20;
    }

    public String getInfo() {
        return " Battlefield 10x10 cells. Amount of ships(size of ship, cells): 1(4), 2(3), 3(2), 4(1) ";
    }

    public boolean isMultiple() {
        return false;
    }
}
