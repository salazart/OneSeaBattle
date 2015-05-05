package com.salazart;

import java.io.File;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.salazart.server.ServerLogic;

public class Main {

	public static void main(String[] args) {
		ClientSimulator clientSimulator = new ClientSimulator();
		
		File file = new File(System.getProperty("user.dir")
				+ File.separator + "src" 
				+ File.separator + "java");
		
		String compileComand = "javac -cp libs/javax.json.jar;. com/salazart/client/*.java";
		String runComand = "java -cp libs/javax.json.jar;. com/salazart/client/Main";
		
		clientSimulator.executePerSeconds(compileComand, 5, file);
		//System.out.println("CLIENT: compile is sucessfully" + clientSimulator.executePerSeconds(compileComand, 5, file));
		JsonObject json = null;
		String response = null;
		ServerLogic serverLogic = new ServerLogic();
		
		json = serverLogic.message(json);
		while(json != null){
			
			response = clientSimulator.executePerSeconds(runComand, 5, file, json);
			System.out.println("CLIENT: " + response);
			
			JsonReader reader = Json.createReader(new StringReader(response));
			json = serverLogic.message(reader.readObject());
			if(json != null)
				System.out.println("SERVER: " + json.toString());
		};
		
		
	}
}
