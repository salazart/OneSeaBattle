package com.salazart.client;



import java.util.ArrayList;
import java.util.List;

/**
 * This class define ship location on field
 * @author home
 *
 */
public class ShipLocationService {
	private static final int SIZE_FIELD = 10;
	private static final int MAX_DECK_SHIP = 4;
	private static final int MIN_DECK_SHIP = 1;
	private static final int DEFAULT_MARK_VALUE = 1;
	private int[][] shotsResultsMap;
	private int x;
	private int y;
    private boolean isVertical = false;
    private boolean isHorizontal = false;
    
    private int markValue = DEFAULT_MARK_VALUE;
	
	public ShipLocationService(int x, int y, int[][] shotsResultsMap){
		this.shotsResultsMap = shotsResultsMap;
		this.x = x;
		this.y = y;
	}
	
	public boolean isHorisontalShip(){
		shipLocation();
		return isHorizontal == true;
	}
	
	public boolean isVerticalShip(){
		shipLocation();
		return isVertical == true;
	}
	
    public List<int[]> shipLocation() {
        List<int[]> shipCoordinats = new ArrayList<int[]>();
        int[] firstCell = {x, y};
        shipCoordinats.add(firstCell);
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (y + i < SIZE_FIELD && shotsResultsMap[x][y + i] == markValue) {
                int[] cell = {x, y + i};
                shipCoordinats.add(cell);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (y - i >= 0 && shotsResultsMap[x][y - i] == markValue) {
                int[] cell = {x, y - i};
                shipCoordinats.add(cell);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (x + i < SIZE_FIELD && shotsResultsMap[x + i][y] == markValue) {
                int[] cell = {x + i, y};
                shipCoordinats.add(cell);
            }
        }
        
        for (int i = MIN_DECK_SHIP; i < MAX_DECK_SHIP; i++) {
            if (x - i >= 0 && shotsResultsMap[x - i][y] == markValue) {
                int[] cell = {x - i, y};
                shipCoordinats.add(cell);
            }
        }
        
        isVertical = findLocation(shipCoordinats, 0, x);
        isHorizontal = findLocation(shipCoordinats, 1, y);
        
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

	public void setMarkValue(int markValue) {
		this.markValue = markValue;
	}
}
