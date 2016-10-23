import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiDecode {
	
	public List<Measure> measures;
	public int resolution;
	
	
	private final int NOTE_ON = 0x90;
	//private final int NOTE_OFF = 0x80;
	private final int TEMPO = 0x51;
	private final int TIME_SIGNATURE = 0x58;
	
	public MidiDecode () {
		measures = new ArrayList<Measure>();
	}
	
	public void getFromMidiFile(File file) throws InvalidMidiDataException, IOException {
		
        Sequence sequence = MidiSystem.getSequence(file);

        resolution = sequence.getResolution();
        
        System.out.println(String.format("Resolution: %d", resolution));

    	Track tracks[] =  sequence.getTracks();
        
    	buildMeasures(tracks[0]);
    	getKeys(tracks[1]);

	}
	
	private void getKeys(Track track) {
		
		int measureIndex = 0;
		
		Measure currentMeasure = measures.get(measureIndex);
		
		int trackSize = track.size();
        for (int i = 0; i < trackSize; i++) {
        	
            MidiEvent event = track.get(i);
            long tick = event.getTick();
            
            tick = roundTick(event.getTick());
            
            while (tick >= (currentMeasure.tickStart + currentMeasure.tickLength)) {
            	measureIndex++;
            	try {
            		currentMeasure = measures.get(measureIndex);
            	}
            	catch (Exception e) {
            		currentMeasure = newMesure(currentMeasure);
            	}
            }
            
            MidiMessage message = event.getMessage();
            
            
            
            if (message instanceof ShortMessage) {
            	
                ShortMessage sm = (ShortMessage) message;
                
                //int channel = sm.getChannel();
                int command = sm.getCommand();
                
                if (command == NOTE_ON) {
                	Key key = new Key();
                	
                	key.tick = tick;
                	key.key = sm.getData1();
                	
                	currentMeasure.keys.add(key);
                	
                	//int velocity = sm.getData2();
                }
            }
        }

       //Delete rest
       measures.subList(measureIndex + 1, measures.size()).clear();
	}
	
	private void buildMeasures(Track track) {
		
		Measure lastValues = getFirstMesure();
		
		Measure currentMeasure = lastValues;
		
		//long measureTick = track.get(0).getTick();
        //int measure = 0;
        
        int trackSize = track.size();
        for (int i = 0; i < trackSize; i++) {
        	
            MidiEvent event = track.get(i);
            long tick = event.getTick();
            
            while (tick >= (currentMeasure.tickStart + currentMeasure.tickLength)) {
            	lastValues = currentMeasure;
            	currentMeasure = newMesure(lastValues);
            }
            
            MidiMessage message = event.getMessage();
            if (message instanceof MetaMessage) {
            	
            	MetaMessage mm = (MetaMessage) message;
            	int type = mm.getType();
            
            	if (type == TIME_SIGNATURE) {
            		byte[] data = mm.getData();
            		currentMeasure.numerator = data[0];
            		currentMeasure.denominator = data[1];
            		currentMeasure.tickLength = getTickLength(currentMeasure.numerator, currentMeasure.denominator);
            		//currentMeasure.clocksPerClick = data[2];
            		//currentMeasure.notated32ndNotesPerBeat = data[3];
            	}
            	
            	if (type == TEMPO) {
            		byte[] data = mm.getData();
            		
            		Tempo tempo = new Tempo();
            		
            		tempo.tick = tick;
            		
            		int microseconds = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
            		tempo.bpm = 60000000 / microseconds;
            		
            		currentMeasure.tempos.add(tempo);
            	}
            	else {
            		System.out.println(String.format("%d type %d", tick, type));
            	}
            }
        }
	}
	
	private Measure getFirstMesure() {
		
		Measure m = new Measure();
		measures.add(m);
		
		m.denominator = 1;
		m.numerator = 1;
		m.tickLength = getTickLength(m.numerator, m.denominator);
		
		m.keys = new ArrayList<Key>();
		m.tempos = new ArrayList<Tempo>();
		
		
		
		m.tickStart = 0;
		
		m.number = 1;
		
		return m;
	}
	
	private Measure newMesure(Measure lastValues) {
		
		Measure newMeasure = new Measure();
		measures.add(newMeasure);
		
		newMeasure.denominator = lastValues.denominator;
		newMeasure.numerator = lastValues.numerator;
		
		newMeasure.keys = new ArrayList<Key>();
		newMeasure.tempos = new ArrayList<Tempo>();
		
		newMeasure.tickLength = lastValues.tickLength;
		
		newMeasure.tickStart = lastValues.tickStart;
		newMeasure.tickStart += lastValues.tickLength;
		
		newMeasure.number = lastValues.number;
		newMeasure.number++;
		
		return newMeasure;
	}
	
	private long roundTick(long tick) {
		
		long round = resolution / 8 / 3;
		
		return (tick + (round/2)) / round * round;
	}
	
	private long getTickLength(int numer, int denom) {
		return numer * resolution;			// / denom;
	}
	
	
}
