import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DrumMap {

	public Map<Integer, Integer> trackLineMap;
	public Map<Integer, String> keyNameMap;
	public Map<Integer, String> keyDescriptionMap;
	
	public DrumMap() {
		
		trackLineMap = new  HashMap<Integer, Integer>();
		keyNameMap = new  HashMap<Integer, String>();
		keyDescriptionMap = new  HashMap<Integer, String>();
		
	}
	
	public void addKey(int key, String keyName, String keyDescription, int trackLine) {
		
		keyNameMap.put(key, keyName);
		trackLineMap.put(key, trackLine);
		keyDescriptionMap.put(key, keyDescription);
	}
	
	public Set<Integer> getKeySet() {
		return trackLineMap.keySet();
	}
	
	public String getKeyString(int key) {
    	
		final String[] names = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#",  "G", "G#"};
		
		String keyName;
		
    	keyName = names[key % 12];
    	keyName += key / 12;

    	return keyName;
    }
	
	public String getKeyName(int key) {
    	
    	String keyName = keyNameMap.get(key);
    	
    	if (keyName == null) {
    		keyName = Integer.toString(key);
    	}
    	
    	return keyName;
    }
	
	public String getKeyDescription(int key) {
    	
    	String keyName = keyDescriptionMap.get(key);
    	
    	if (keyName == null) {
    		keyName = Integer.toString(key);
    	}
    	
    	return keyName;
    }
    
    public int getTrackLine(int key) {
    	
    	try {
    		return trackLineMap.get(key);
    	}
    	catch  (Exception e) {
    		return 3;
    	}
    }
}
