package com.salazart.client;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NotRandomShotService {
	private static final int SIZE_FIELD = 10;
	private static final int MARK_DEAD_SHIP = 2;
	private static final int MARK_SPACE_FIELD = 0;
	
	private static final int COUNT_FOUR_DESK_SHIP = 1;
	private static final int COUNT_TRIPLE_DESK_SHIP = 2;
	private static final int COUNT_DOUBLE_DESK_SHIP = 3;
	private static final int COUNT_SINGLE_DESK_SHIP = 4;
	
	private int[][] madeShotsMap;
	private int[][] shotsResultsMap;
	
	private int countOfSingleDeck = 0;
	private int countOfDoubleDeck = 0;
	private int countOfTripleDeck = 0;
	private int countOfFourDeck = 0;
	
	public NotRandomShotService(int[][] madeShotsMap, int[][] shotsResultsMap){
		this.madeShotsMap = madeShotsMap;
		this.shotsResultsMap = shotsResultsMap;
	}
	
	public int[] getNotRandomShot(){
		int[] cell = null;
		
		checkShipsPosition(shotsResultsMap);
		
		if(countOfFourDeck < COUNT_FOUR_DESK_SHIP){
			cell = findSupposedShip(4);
		} else if (countOfTripleDeck < COUNT_TRIPLE_DESK_SHIP){
			cell = findSupposedShip(3);
		} else if (countOfDoubleDeck < COUNT_DOUBLE_DESK_SHIP){
			cell = findSupposedShip(2);
		} else if (countOfSingleDeck < COUNT_SINGLE_DESK_SHIP){
			cell = findSupposedShip(1);
		}
		
		if(cell == null)
			cell = generateRandomShot();
		
		return cell;
	}
	
	/**
	 * This method find ship through field diagonally
	 * @param defaultLenght
	 * @return
	 */
	private int[] findSupposedShip(int defaultLenght){
		for (int i = 0; i < SIZE_FIELD; i++) {
			for (int j = 0; j < SIZE_FIELD; j++) {
				if(((i+j)%defaultLenght) == defaultLenght-1){
					ShipLocationService shipLocation = new ShipLocationService(i, j, shotsResultsMap);
					shipLocation.setMarkValue(MARK_SPACE_FIELD);
					if(shipLocation.shipLocation().size() >= defaultLenght
							&& madeShotsMap[i][j] == MARK_SPACE_FIELD){
						int[] shot = { i, j };
						return shot;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * This method find all ship on field and cound type ship
	 * @param shotsResultsMap
	 */
	private void checkShipsPosition(int[][] shotsResultsMap) {
        ArrayList<int[]> allShipCoordinats = new ArrayList<int[]>();
        for (int i = 0; i < SIZE_FIELD; i++) {
            for (int j = 0; j < SIZE_FIELD; j++) {
                int[] cell = {i, j};
                if (shotsResultsMap[i][j] == MARK_DEAD_SHIP && !isCellProcessedBefore(cell, allShipCoordinats)) {
                	
                	ShipLocationService shipLocation = new ShipLocationService(i, j, shotsResultsMap);
                	shipLocation.setMarkValue(MARK_DEAD_SHIP);
                    List<int[]> shipCoordinats = shipLocation.shipLocation();
                    
                    allShipCoordinats.addAll(shipCoordinats);
                    if (shipCoordinats.size() == 1) {
                    	countOfDoubleDeck++;
                    } else if (shipCoordinats.size() == 2) {
                        countOfDoubleDeck++;
                    } else if (shipCoordinats.size() == 3) {
                        countOfTripleDeck++;
                    } else if (shipCoordinats.size() == 4) {
                        countOfFourDeck++;
                    }
                }
            }
        }
    }
	
    private boolean isCellProcessedBefore(int[] cell, ArrayList<int[]> allShipCoordinats) {
        for (Object coordinats : allShipCoordinats) {
            int[] xy = (int[]) coordinats;
            if (Arrays.equals(cell, xy)) {
                return true;
            }
        }
        return false;
    }
    
	private int[] generateRandomShot(){
		int x, y;
		Random rand = new Random();
		x = rand.nextInt(10);
		y = rand.nextInt(10);
		while (madeShotsMap[x][y] != 0) {
			x = rand.nextInt(10);
			y = rand.nextInt(10);
		}
		int[] cell = {x, y};
		return cell;
	}
}
