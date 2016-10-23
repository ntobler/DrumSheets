import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.print.PageFormat;

public class DrumFormat {

	final int TRACK_LINE_COUNT = 6;
	
	public double X_FACTOR;
	public double Y_FACTOR;

	public long TICKS_PER_MEASURE;
	
	public double LINE_WIDTH;
	
    public double TRACK_LINE_SPACING;
    public double TRACK_BLOCK_GAP;
    
    public double MEASURE_X_OFFSET;
    
    public double INDICATOR_OFFSET;
    public double INDICATOR_SIZE;
    public double INDICATOR_BAR_SIZE;
    public double INDICATOR_BAR_DISTANCE;
	
    public long TICKS_PER_NOTE;
    
    public double INDICATOR_POS;
    public double TRACK_BLOCK_LENGTH;
    public double NOTE_SPACING_FACTOR;
    public double TRACK_BLOCK_HEIGHT;
	
    public Font TITLE_FONT;
    public Font TITLE_MINOR_FONT;
    public Font TEMPO_FONT; 
    public Font FRACTION_FONT;
    public Font KEY_FONT;
    public Font LEGEND_FONT;
    
    public BasicStroke STROKE;
	
	public DrumFormat(double yFactor, PageFormat pageFormat, double xFactor, MidiDecode midi) {
		
		Y_FACTOR = yFactor;
		X_FACTOR = xFactor;
		
		LINE_WIDTH = 1;
		
		TRACK_LINE_SPACING = 10 * yFactor;
	    TRACK_BLOCK_GAP = 64 * yFactor;
	    
	    MEASURE_X_OFFSET = 24 * xFactor;
	    
	    INDICATOR_OFFSET = 10 *yFactor;
		INDICATOR_SIZE = 15 * yFactor;
		INDICATOR_BAR_SIZE = 2 * yFactor;
		INDICATOR_BAR_DISTANCE = 4 * yFactor;
		
		TICKS_PER_NOTE = midi.resolution;
		
		INDICATOR_POS = TRACK_LINE_SPACING * (TRACK_LINE_COUNT-1) + INDICATOR_OFFSET;
	    NOTE_SPACING_FACTOR = xFactor * 32 / midi.resolution;
	    TRACK_BLOCK_HEIGHT = TRACK_LINE_SPACING * (TRACK_LINE_COUNT-1) + TRACK_BLOCK_GAP;
		
		//TITLE_FONT = new Font("Times New Roman", Font.BOLD, (int) (24 * yFactor));
		//TITLE_MINOR_FONT = new Font("Times New Roman", Font.PLAIN, (int) (24 * yFactor));
		TEMPO_FONT = new Font("Times New Roman", Font.ITALIC, (int) (12 * yFactor));
		FRACTION_FONT = new Font("Times New Roman", Font.BOLD, (int) (20 * yFactor));
		KEY_FONT = new Font("Calibri", Font.PLAIN, (int) (12 * yFactor));
		//LEGEND_FONT = new Font("Calibri", Font.PLAIN, (int) (12 * yFactor));
		
		
		STROKE = new BasicStroke((float)(LINE_WIDTH * yFactor), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	}
	
}
