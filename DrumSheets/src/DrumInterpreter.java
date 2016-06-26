import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class DrumInterpreter implements Paintable, Printable, PageTransformer.PageCreatedListener {

	private final int ROUND = 120;
	private int measuresPerLine = 4;
	private double factor = 0.5;

	Graphics2D g2;
    
	private DrumMap drumMap;
	private AffineTransform standardTransform;
	private PageTransformer pageTransformer;
	private PageFormat pageFormat;
	
	private DrumLineFormat dlf;
	
	private int trackIndex;
	private int tempoTrakIndex;
	
	private File file;
	
	public DrumInterpreter(PageFormat pageFormat) {
		
		this.pageFormat = pageFormat;
		this.trackIndex = 0;
		this.file = new File("drum.mid");
	}
	
	public void setDrumMap(DrumMap drumMap) {
		this.drumMap = drumMap;
	}

	private final int NOTE_ON = 0x90;
	//private final int NOTE_OFF = 0x80;
	private final int SET_TEMPO = 0x51;
	
	
	public Track[] getTracks() {
		
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(file);
			return sequence.getTracks();
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setTrack(int trackIndex, int tempoTrakIndex) {
		this.trackIndex = trackIndex;
		this.tempoTrakIndex = tempoTrakIndex;
	}
	
	public void setSize(int size) {
		this.factor = 0.75/(((double)size)/100);
	}
	
	public void setMeasuresPerLine(int measuresPerLine) {
		this.measuresPerLine = measuresPerLine;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void draw(PageFormat pageFormat) throws Exception {
		
        Sequence sequence = MidiSystem.getSequence(file);
        dlf = new DrumLineFormat(factor, pageFormat, sequence.getResolution(), measuresPerLine);
        g2.setStroke(dlf.STROKE);
        
        standardTransform = g2.getTransform();
		
		pageTransformer = new PageTransformer(pageFormat, this);
		
		pageTransformer.goToFirstPage();
        
		int bpm = getTempo(sequence, tempoTrakIndex);
		
        drawTitle(bpm);
        
        //long tickLength = sequence.getTickLength();
        ///long msLength = sequence.getMicrosecondLength();

    	Track track =  sequence.getTracks()[trackIndex];
        
        int trackSize = track.size();

        long measureTick = track.get(0).getTick();
        int measure = 0;
        drawNewMeasure(measure);
        
        drawTempo(bpm);
        
        for (int i = 0; i < trackSize; i++) {
        	
            MidiEvent event = track.get(i);
            
            MidiMessage message = event.getMessage();
            
            
            if (message instanceof ShortMessage) {
            	
                ShortMessage sm = (ShortMessage) message;
                
                int channel = sm.getChannel();
                
                int command = sm.getCommand();
                
                if (command == NOTE_ON) {
                
                	long tick = event.getTick();
                    
                    tick = roundTick(event.getTick());
                    
                    tick -= measureTick;
                    
                    if (tick >= dlf.TICKS_PER_MEASURE) {
                    	tick -= dlf.TICKS_PER_MEASURE;
                    	measureTick += dlf.TICKS_PER_MEASURE;
                    	measure++;
                    	drawNewMeasure(measure);
                    }
                	
                    int key = sm.getData1();
                    int velocity = sm.getData2();
                    
                    drawKey(channel, tick, key, velocity);
                }
            }
            /*else if (message instanceof MetaMessage) {
            	
            	MetaMessage mm = (MetaMessage) message;
            	int type = mm.getType();
            
            	if (type == SET_TEMPO) {
            		byte[] data = mm.getData();
            		int tempo = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
            		int bpm = 60000000 / tempo;
            		//System.out.println(String.format("tempo: %d", bpm));
            		drawTempo(bpm);
            	}
            	
            }*/
        }

    }
	
	private int getTempo(Sequence sequence, int tempoTrackIndex) throws InvalidMidiDataException, IOException {
		
    	Track track =  sequence.getTracks()[tempoTrackIndex];
        
        int trackSize = track.size();

        for (int i = 0; i < trackSize; i++) {
        	
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();
            if (message instanceof MetaMessage) {
            	
            	MetaMessage mm = (MetaMessage) message;
            	int type = mm.getType();
            
            	if (type == SET_TEMPO) {
            		byte[] data = mm.getData();
            		int tempo = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
            		int bpm = 60000000 / tempo;
            		return bpm;
            	}
            }
        }
		return 0;
	}
    
	private long roundTick(long tick) {
		
		return (tick + (ROUND/2)) / ROUND * ROUND;
	}
	
    private void drawKey(int channel, long tick, int key, int velocity) {
    	
    	double x = tick * dlf.NOTE_SPACING_FACTOR;
    	double y = drumMap.getTrackLine(key) * dlf.TRACK_LINE_SPACING -1;
    	
    	final double PADDING = 0;
    	
    	g2.setFont(dlf.KEY_FONT);
    	
    	String text = drumMap.getKeyName(key);
    	FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    double strWidth = metrics.stringWidth(text);
	    double strAscent = metrics.getAscent();
	    
	    x -= strWidth/2;
	    double y1 = y + strAscent/2;
	    double y2 = y - strAscent/3;
    	
	    g2.setColor(Color.WHITE);
    	g2.fill(new Rectangle2D.Double(x - PADDING, y2, strWidth + (PADDING*2), strAscent));
    	
	    g2.setColor(Color.BLACK);
	    g2.drawString(text, (float)x, (float)y1);
	    
    	drawTickIndicators(tick);
    }
    
    private long lastTick = 0;
    
    private void drawTickIndicators(long tick) {
    	
    	if (tick < lastTick) {
    		lastTick = tick;
    	}
    	
    	g2.setColor(Color.GRAY);
    	
    	double x = tick * dlf.NOTE_SPACING_FACTOR;
    	g2.draw(new Line2D.Double(x, dlf.INDICATOR_POS, x, dlf.INDICATOR_POS+dlf.INDICATOR_SIZE));
    	
    	long thisLastTick = lastTick;
    	
    	long b = (tick / (dlf.TICKS_PER_MEASURE/4)) * (dlf.TICKS_PER_MEASURE/4);
    	
    	if (b > thisLastTick) {
    		thisLastTick = b;
    	}
    	
    	long dif = (tick - thisLastTick);
    	
    	if (dif == dlf.TICKS_PER_MEASURE/8) {
    		double a = (tick - dif) * dlf.NOTE_SPACING_FACTOR;
    		
    		Rectangle2D r = new Rectangle2D.Double(a, dlf.INDICATOR_POS+dlf.INDICATOR_SIZE-dlf.INDICATOR_BAR_SIZE, dif * dlf.NOTE_SPACING_FACTOR, dlf.INDICATOR_BAR_SIZE);
    		g2.draw(r);
    		g2.fill(r);
    	}
    	
    	if (dif == dlf.TICKS_PER_MEASURE/16) {
    		double a = (tick - dif) * dlf.NOTE_SPACING_FACTOR;
    		
    		Rectangle2D r = new Rectangle2D.Double(a, dlf.INDICATOR_POS+dlf.INDICATOR_SIZE-dlf.INDICATOR_BAR_SIZE, dif * dlf.NOTE_SPACING_FACTOR, dlf.INDICATOR_BAR_SIZE);
    		g2.draw(r);
    		g2.fill(r);
    		
    		Rectangle2D r2 = new Rectangle2D.Double(a, dlf.INDICATOR_POS+dlf.INDICATOR_SIZE-dlf.INDICATOR_BAR_SIZE-dlf.INDICATOR_BAR_DISTANCE, dif * dlf.NOTE_SPACING_FACTOR, dlf.INDICATOR_BAR_SIZE);
    		g2.draw(r2);
    		g2.fill(r2);
    	}

    	lastTick = tick;
    	
    }
    
    private void drawTitle(int bpm) {
    	
    	double width = pageFormat.getImageableWidth();
    	
    	pageTransformer.transformSpace(width, 150);
    	setTransform(pageTransformer.getTransform());
    	
    	MetaDataEditor editor = new MetaDataEditor();
    	
    	g2.setColor(Color.BLACK);
    	
    	g2.setFont(dlf.TITLE_FONT);
    	CustomGraphics.drawCenteredString(g2, (float)width / 2, 50, editor.getTitle());
    
    	g2.setFont(dlf.TITLE_MINOR_FONT);
    	CustomGraphics.drawCenteredString(g2, (float)width / 2, 100, editor.getAuthor());
    	
    	drawLegend();
    	
    }
    
    private void drawLegend() {
    	
    	int line = 0;
    	
    	g2.setFont(dlf.LEGEND_FONT);
    	
    	FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    double strHeigth = metrics.getHeight();
    	
    	for (int key: drumMap.getKeySet()) {
	        
	    	g2.drawString(
	    			drumMap.getKeyName(key) + " = " + drumMap.getKeyDescription(key),
	    			0f,
	    			(float)strHeigth * line );
	    	
	    	
	    	line++;
	    }
    }
    
    
    private void drawPage(int page) {
    	
    	setTransform(pageTransformer.getPageTransform());
    	
    	g2.setColor(Color.GRAY);
    	
    	g2.draw(new Rectangle2D.Double(0, 0, pageFormat.getWidth(), pageFormat.getHeight()));
    	/*g2.draw(new Rectangle2D.Double(
    			pageFormat.getImageableX(),
    			pageFormat.getImageableY(),
    			pageFormat.getImageableWidth(),
    			pageFormat.getImageableHeight()));*/
    	
    	g2.setColor(Color.BLACK);
    	g2.setFont(dlf.PAGE_NUMBER_FONT);
    	g2.drawString(
    			Integer.toString(page),
    			(float) (pageFormat.getImageableWidth()/2 + pageFormat.getImageableX()),
    			(float) (pageFormat.getImageableHeight() + pageFormat.getImageableY()));
    	
    }
    
    private void setTransform(AffineTransform transform) {
    	
    	g2.setTransform(standardTransform);
    	g2.transform(transform);

    }
    
    private void translateToNewMeasure(int measure) {
    	
    	pageTransformer.transformSpace(dlf.TRACK_BLOCK_LENGTH, dlf.TRACK_BLOCK_HEIGHT);
    	setTransform(pageTransformer.getTransform());
    }
    
    private void drawNewMeasure(int measure) {
    	
    	translateToNewMeasure(measure);
    	
    	g2.setColor(Color.GRAY);
    	
    	for (int i = 0; i < dlf.TRACK_LINE_COUNT; i++) {
    		
    		double y = i * dlf.TRACK_LINE_SPACING;
    		g2.draw(new Line2D.Double(0, y, dlf.TRACK_BLOCK_LENGTH, y));
    	}
    	
    	g2.draw(new Line2D.Double(0, 0, 0, (dlf.TRACK_LINE_COUNT-1) * dlf.TRACK_LINE_SPACING));
    	g2.draw(new Line2D.Double(dlf.TRACK_BLOCK_LENGTH, 0, dlf.TRACK_BLOCK_LENGTH, (dlf.TRACK_LINE_COUNT-1) * dlf.TRACK_LINE_SPACING));
    	
    	g2.setFont(dlf.KEY_FONT);
    	g2.drawString(Integer.toString(measure+1), 0, -7);    	
    	
    }
    
    private void drawTempo(int tempo) {
    	
    	g2.setColor(Color.BLACK);
    	g2.setFont(dlf.TEMPO_FONT);
    	g2.drawString("\u266A = " + Integer.toString(tempo) + "bpm", 0, -18);
    }
	
	@Override
	public void paint(Graphics2D g2) {

		this.g2 = g2;
		g2.translate(-pageFormat.getWidth()/2, 0);
		
		try {
			draw(pageFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setImageDimension(Dimension dimension) {
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		
		g2 = (Graphics2D)g;

		g2.transform(pageTransformer.translateToPageIndex(pageIndex));
		
	    try {
			draw(this.pageFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    if (pageIndex > (pageTransformer.getPageCount() - 1)) {
	         return NO_SUCH_PAGE;
	    }
	    
	    return PAGE_EXISTS;
	}

	@Override
	public void onPageCreated(int page) {
		drawPage(page);
	}

}
