package com.salazart.client;



import javax.json.JsonObject;


public class ShotService {
	private static final int MARKER_DEAD_SHIP = 2;
	private static final int FIELD_NOTE = 1;
	private static final int MARKERED_FIELD = 0;

	private int[][] madeShotsMap = new int[10][10];
	private int[][] shotsResultsMap = new int[10][10];
	
	public ShotService(int[][] madeShotsMap, int[][] shotsResultsMap){
		this.madeShotsMap = madeShotsMap;
		this.shotsResultsMap = shotsResultsMap;
	}
	
	public JsonObject shot() {
		
	    madeShotsMap = ShipsFieldService.
	    		markFieldAroundShip(madeShotsMap, shotsResultsMap, MARKER_DEAD_SHIP, FIELD_NOTE, MARKERED_FIELD);
		
	    int[] cell = findDamagedShip();
		
		if(cell == null){
			NotRandomShotService randomShot = new NotRandomShotService(madeShotsMap, shotsResultsMap);
			cell = randomShot.getNotRandomShot();
		}
		
		int x = cell[0];
		int y = cell[1];
		
		JsonService jsonService = new JsonService();
		return jsonService.generateJsonResponse(x, y);
	}
	
	/**
	 * This method find damage ship and return his next coordinates
	 * @return
	 */
	private int[] findDamagedShip() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (shotsResultsMap[i][j] == 1) {
					int[] shot = null;
					
					ShipLocationService shipLocation = new ShipLocationService(i, j, shotsResultsMap);
					if(shipLocation.isHorisontalShip()){
						shot = findHorisontalShot(i, j);
					} else {
						shot = findVerticalShot(i, j);
					}
					
					if(shot != null)
						return shot;
				}
			}
		}
		return null;
	}
	
	private int[] findVerticalShot(int i, int j){
		if (i + 1 < 10 && madeShotsMap[i + 1][j] == 0) {
			int[] shot = { i + 1, j };
			return shot;
		} else if (i - 1 >= 0 && madeShotsMap[i - 1][j] == 0) {
			int[] shot = { i - 1, j };
			return shot;
		}
		return findHorisontalShot(i, j);
	}
	
	private int[] findHorisontalShot(int i, int j){
		if (j - 1 >= 0 && madeShotsMap[i][j - 1] == 0) {
			int[] shot = { i, j - 1 };
			return shot;
		} else if (j + 1 < 10 && madeShotsMap[i][j + 1] == 0) {
			int[] shot = { i, j + 1 };
			return shot;
		}
		return null;
	}
}
