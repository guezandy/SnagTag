package com.snag.snagtag;
import java.util.ArrayList;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class InternalData {
	private static final String TAG = "Internal Data";
	
	public static final String ENTITY_DELIM = "|";
	public static final String DETAIL_DELIM = "~";
	
	//Keys from setup page
	public static String FIRST_NAME_KEY ="firstName";
	public static String LAST_NAME_KEY ="lastName";
	public static String USER_EMAIL_KEY = "userEmail";
	public static String EMAIL_CONFIRMATION_KEY = "emailConfirmation";
	
	//Keys from personal Entitys
	public static String CART_KEY = "cart";
	public static String TO_DELETE_KEY ="toDelete";
	
	//Parses SharedPreference and returns an ArrayList of Entitys as strings
	public static ArrayList<String> parse(String key,SharedPreferences prefs){
		
		// get current data
		String data = prefs.getString(key,"");
		                 
		String[] tokens = data.split("["+ENTITY_DELIM+"]");
		ArrayList<String> parsedData= new ArrayList<String>();
		for(String token:tokens){
			if(!token.isEmpty()){
			parsedData.add(token);
			}
		}
	    return parsedData;
	}

	//Converts an Array of Strings into an Array of Entitys
	public static ArrayList<Entity> stringsToEntities(ArrayList<String> entityArray) {
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for(String entityString : entityArray ){
			String[] entityComponents = entityString.split(ENTITY_DELIM);
			Entity entity = new Entity();
			entity.put("Brand",entityComponents[0] );
			entity.put("Price",entityComponents[1] );
			entity.put("ImageUrl",entityComponents[2] );
			entities.add(entity);
			}
		return entities;
		
	}
	
	//Pulls internal String of data, parses it and converts it into objects:Entity
	public static ArrayList<Entity> getEntities(String key,SharedPreferences prefs){
		ArrayList<String> parsedString = parse(key,prefs);
		return stringsToEntities(parsedString);
	}

	public static void addNewEntity(String key, String entityString,Context context, SharedPreferences prefs){
		// get current Entitys
		String customEntitys = prefs.getString(key,"");
		// manipulate new Entity
		Boolean isValidEntity = !( customEntitys.contains(entityString));
		if(isValidEntity){
			addEntities(entityString,customEntitys,key,prefs);
		}else{
			
			Toast toast = Toast.makeText(context, "Invalid item.\nPlease select another.", 
					Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	public static void addEntities(String entitiesString,String currentEntities ,String key, SharedPreferences prefs){
		currentEntities += entitiesString;
		addItem(prefs, key, currentEntities);
		Log.i(TAG,"added Entity to "+key);
	}
	public static void removeToDeleteEntities(SharedPreferences prefs){
		// get current personal Entitys
		String entities = prefs.getString(CART_KEY,"");
		ArrayList<String> parsedToDelete = InternalData.parse(TO_DELETE_KEY,prefs);
		Log.i(TAG,"deleteSelectedEntitys, saved internal data: "+entities);
		
		for(String entityString: parsedToDelete){
			Log.i(TAG,"deleteSelectedEntitys, customEntitys before replace :"+entities);
			entities = entities.replace(entityString+ENTITY_DELIM,"");
			Log.i(TAG,"deleteSelectedEntitys, customEntitys after replace :"+entities);
		}
		
		Log.i(TAG,"deleteSelectedEntitys, finished for loop :"+entities);

		//Adds new custom Entitys to Entitys string
		Editor editor = prefs.edit();
		editor.putString(CART_KEY, entities);
		editor.putString(TO_DELETE_KEY, "");
		editor.commit();
	}
	public static void addItem(SharedPreferences prefs, String key, String toAdd){
		// manipulate new Entity
		Editor editor = prefs.edit();
		editor.putString(key, toAdd);
		editor.commit();
	}
	
	//Parses SharedPreference and returns an ArrayList of strings
		public static ArrayList<String> parseInternalData(String delim,String key,SharedPreferences prefs){
			
			// get current data
			String data = prefs.getString(key,"");
			                 
			String[] tokens = data.split("["+delim+"]");
			ArrayList<String> parsedData= new ArrayList<String>();
			for(String token:tokens){
				if(!token.isEmpty()){
				parsedData.add(token);
				}
			}
		    return parsedData;
		}
		
		
		//pulls internal String of data, parses it and converts it into objects:Entity
		public static ArrayList<Entity> extractUserData( String key,SharedPreferences prefs,String entityDelim,String detailDelim){
			ArrayList<String> parsedString = parseInternalData(entityDelim,key,prefs);
			return stringsToEntities(parsedString);
		}
	
}
