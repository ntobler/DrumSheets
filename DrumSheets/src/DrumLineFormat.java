import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.print.PageFormat;

public class DrumLineFormat {

	final int TRACK_LINE_COUNT = 6;
	
	public long TICKS_PER_MEASURE;
	
	public double LINE_WIDTH;
	
    public double TRACK_LINE_SPACING;
    public double TRACK_BLOCK_GAP;
    
    public double INDICATOR_OFFSET;
    public double INDICATOR_SIZE;
    public double INDICATOR_BAR_SIZE;
    public double INDICATOR_BAR_DISTANCE;
	
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
	
	public DrumLineFormat(double factor, PageFormat pageFormat, int midiResolution, int measuresPerLine) {
		
		TICKS_PER_MEASURE = midiResolution * 4;
		
		LINE_WIDTH = 1;
		
		TRACK_LINE_SPACING = 10 * factor;
	    TRACK_BLOCK_GAP = 64 * factor;
	    
	    INDICATOR_OFFSET = 10 *factor;
		INDICATOR_SIZE = 15 * factor;
		INDICATOR_BAR_SIZE = 2 * factor;
		INDICATOR_BAR_DISTANCE = 4 * factor;
		
		INDICATOR_POS = TRACK_LINE_SPACING * (TRACK_LINE_COUNT-1) + INDICATOR_OFFSET;
	    TRACK_BLOCK_LENGTH =  pageFormat.getImageableWidth() / measuresPerLine;
	    NOTE_SPACING_FACTOR = TRACK_BLOCK_LENGTH / TICKS_PER_MEASURE;
	    TRACK_BLOCK_HEIGHT = TRACK_LINE_SPACING * (TRACK_LINE_COUNT-1) + TRACK_BLOCK_GAP;
		
		TITLE_FONT = new Font("Times New Roman", Font.BOLD, (int) (24 * factor));
		TITLE_MINOR_FONT = new Font("Times New Roman", Font.PLAIN, (int) (24 * factor));
		TEMPO_FONT = new Font("Times New Roman", Font.ITALIC, (int) (12 * factor));
		FRACTION_FONT = new Font("Times New Roman", Font.BOLD, (int) (20 * factor));
		KEY_FONT = new Font("Calibri", Font.PLAIN, (int) (12 * factor));
		LEGEND_FONT = new Font("Calibri", Font.PLAIN, (int) (12 * factor));
		
		
		STROKE = new BasicStroke((float)(LINE_WIDTH * factor), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	}
	
}
