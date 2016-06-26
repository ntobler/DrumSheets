import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DrumMapParser {
	
	public static DrumMap read(String file) {

		DrumMap drumMap = new DrumMap();
		
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader(file));
	
			JSONObject jsonObject= (JSONObject) obj;
	
			JSONArray keys = (JSONArray) jsonObject.get("keys");
			
			Iterator<JSONObject> iterator = keys.iterator();
            while (iterator.hasNext()) {
            	JSONObject keyContainer = (JSONObject) iterator.next();
            	
            	long keyL = (long) keyContainer.get("key");
            	int key = (int)keyL;
            	String keyName = (String) keyContainer.get("keyName");
            	String keyDescription = (String) keyContainer.get("keyDescription");
            	long trackLineL = (long) keyContainer.get("trackLine");
            	int trackLine = (int)trackLineL;
            	drumMap.addKey(key, keyName, keyDescription, trackLine);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return drumMap;
	}
	
	public static void write(String file, DrumMap drumMap) {
		
		JSONObject jsonObject = new JSONObject();

		JSONArray keys = new JSONArray();
		
		    for (int key: drumMap.getKeySet()) {
		        
		    	JSONObject obj = new JSONObject();
		    	obj.put("key", key);
		    	obj.put("keyName", drumMap.getKeyName(key));
		    	obj.put("keyDescription", drumMap.getKeyDescription(key));
		    	obj.put("trackLine", drumMap.getTrackLine(key));
		    	keys.add(obj);
		    }
		
		jsonObject.put("keys", keys) ;

        try (FileWriter fileWriter = new FileWriter(file)) {
        	fileWriter.write(jsonObject.toJSONString());
		}
        catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
