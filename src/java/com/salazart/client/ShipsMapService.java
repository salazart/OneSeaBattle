package com.salazart.client;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class create shipsfield of random position
 */
public class ShipsMapService {
	private static final int MARKERED_SHIP = 1;
	private static final int FIELDS_MARKER = 2;
	private static final int MARKERED_FIELD = 0;
	private static final int MAX_DECKER_SHIP = 4;
	private static final int MAX_SIZE_FIELD = 10;
	
	private int[][] shipsMap;
	
	/**
	 * This method generate new field with ships and
	 * @return shipsMap
	 */
    public int[][] generateShipsMap(){
    	shipsMap = new int[MAX_SIZE_FIELD][MAX_SIZE_FIELD];
    	for (int[] row : shipsMap) {
            Arrays.fill(row, 0);
        }
    	
		for(int i = 0; i < MAX_DECKER_SHIP; i++){
			for(int j = 0; j <= i; j++){
				createShip(MAX_DECKER_SHIP-i);
				shipsMap = ShipsFieldService.markFieldAroundShip(shipsMap, shipsMap, 
						MARKERED_SHIP, FIELDS_MARKER, MARKERED_FIELD);
			}
		}
		
		for(int i = 0; i < MAX_SIZE_FIELD; i++)
			for(int j = 0; j < MAX_SIZE_FIELD; j++){
				if(shipsMap[i][j] == FIELDS_MARKER){
					shipsMap[i][j] = MARKERED_FIELD;
				}
			}
    	return shipsMap;
    }
    
    /**
     * This method create one ship on field
     * @param countDesk count desk current ship
     * @return 
     */
    private boolean createShip(int countDesk){
    	List<Integer> xCoords = createIndexes();
    	while(xCoords.size() != 0){
			Random rand = new Random();
			int indexX = rand.nextInt(xCoords.size());
			
			List<Integer> yCoords = createIndexes();
			while(yCoords.size() != 0){
    			int indexY = rand.nextInt(yCoords.size());
    			
    			if(tryCreateShip(countDesk, xCoords.get(indexX), yCoords.get(indexY))){
    				return true;
    			} else {
    				yCoords.remove(indexY);
    			}
    		}
    		xCoords.remove(indexX);
    	}
    	return false;
    }
    
    /**
     * This method try create ship on field with coordinates x y
     * @param countDesk count the desk current ship
     * @param x coordinate on field
     * @param y coordinate on field
     * @return true is ship created successfully or false anyway
     */
    private boolean tryCreateShip(int countDesk, int x, int y){
    	Random rand = new Random();
    	if(rand.nextBoolean()){
			if(tryCreateVerticalShip(countDesk, x, y)){
				return true;
			} else {
				return tryCreateHorisontalShip(countDesk, x, y);
			}
    	} else {
    		if(tryCreateHorisontalShip(countDesk, x, y)){
    			return true;
    		} else {
    			return tryCreateVerticalShip(countDesk, x, y);
    		}
    	}
    }
    
    /**
     * Create list indexes of field by that create ship of random position
     */
    private List<Integer> createIndexes(){
    	List<Integer> indexes = new ArrayList<Integer>();
    	for (int i = 0; i < 10; i++) {
    		indexes.add(i);
		}
    	return indexes;
    }
    
    /**
     * This method check can create new vertical ship with coordinate and if true create it
     */
    private boolean tryCreateVerticalShip(int countDesk, int x, int y){
    	List<int[]> shipCoordinats = new ArrayList<int[]>();
    	for (int i = 0; i < countDesk; i++) {
            if (x + i < MAX_SIZE_FIELD && shipsMap[x + i][y] == 0) {
                int[] cell = {x + i, y};
                shipCoordinats.add(cell);
            } else {
                break;
            }
        }
    	
    	if(shipCoordinats.size() >= countDesk){
    		for (int i = 0; i < countDesk; i++) {
    			if (x + i < MAX_SIZE_FIELD && shipsMap[x + i][y] == 0) {
    				shipsMap[x + i][y] = 1;
    			}
    		}
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    
    /**
     * This method check can create new horizontal ship with coordinate and if true create it
     */
    private boolean tryCreateHorisontalShip(int countDesk, int x, int y){
    	List<int[]> shipCoordinats = new ArrayList<int[]>();
    	 for (int i = 0; i < countDesk; i++) {
             if (y + i < MAX_SIZE_FIELD && shipsMap[x][y + i] == 0) {
                 int[] cell = {x, y + i};
                 shipCoordinats.add(cell);
             } else {
                 break;
             }
         }
    	
    	if(shipCoordinats.size() >= countDesk){
    		for (int i = 0; i < countDesk; i++) {
    			if (y + i < MAX_SIZE_FIELD && shipsMap[x][y + i] == 0) {
    				shipsMap[x][y + i] = 1;
    			}
    		}
    		return true;
    	} else {
    		return false;
    	}
    }
}
