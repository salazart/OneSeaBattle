package com.salazart.client;




/**
 * This class marked field around dead ship
 * @param madeShotsMap
 * @param shotsResultsMap
 * @return
 */
public class ShipsFieldService {
	
	public static int[][] markFieldAroundShip(int[][] madeShotsMap, 
			int[][] shotsResultsMap, int markeredShip, int fieldNote, int markeredField){
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (shotsResultsMap[i][j] == markeredShip){
					if (i - 1 >= 0 && madeShotsMap[i - 1][j] == markeredField)
						madeShotsMap[i - 1][j] = fieldNote;
					if (j - 1 >= 0 && madeShotsMap[i][j - 1] == markeredField)
						madeShotsMap[i][j - 1] = fieldNote;
					if (i + 1 < 10 && madeShotsMap[i + 1][j] == markeredField)
						madeShotsMap[i + 1][j] = fieldNote;
					if (j + 1 < 10 && madeShotsMap[i][j + 1] == markeredField)
						madeShotsMap[i][j + 1] = fieldNote;
					
					if (i + 1 < 10 && j + 1 < 10 && madeShotsMap[i + 1][j + 1] == markeredField)
						madeShotsMap[i + 1][j + 1] = fieldNote;
					if (i - 1 >= 0 && j - 1 >= 0 && madeShotsMap[i - 1][j - 1] == markeredField)
						madeShotsMap[i - 1][j - 1] = fieldNote;
					if (i + 1 < 10 && j - 1 >= 0 && madeShotsMap[i + 1][j - 1] == markeredField)
						madeShotsMap[i + 1][j - 1] = fieldNote;
					if (i - 1 >= 0 && j + 1 < 10 && madeShotsMap[i - 1][j + 1] == markeredField)
						madeShotsMap[i - 1][j + 1] = fieldNote;
				}
			}
		}
		return madeShotsMap;
	}
}
