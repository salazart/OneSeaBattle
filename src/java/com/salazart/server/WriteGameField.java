package com.salazart.server;




public class WriteGameField {
	public static void writeGameField(String message, int[][] field){
		System.out.println(message);
		for(int i = 0; i < field.length; i++){
			for (int j = 0; j < field.length; j++) {
				System.out.print(field[i][j]+" ");
			}
			System.out.println();
		}
	}
}
