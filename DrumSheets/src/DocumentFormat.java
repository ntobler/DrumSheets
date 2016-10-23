import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.print.PageFormat;

public class DocumentFormat {

final int TRACK_LINE_COUNT = 6;
	
//	public double FACTOR;
//
//	public long TICKS_PER_MEASURE;
//	
	public double LINE_WIDTH;
//	
//    public double TRACK_LINE_SPACING;
//    public double TRACK_BLOCK_GAP;
//    
//    public double MEASURE_X_OFFSET;
//    
//    public double INDICATOR_OFFSET;
//    public double INDICATOR_SIZE;
//    public double INDICATOR_BAR_SIZE;
//    public double INDICATOR_BAR_DISTANCE;
//	
//    public long TICKS_PER_NOTE;
//    
//    public double INDICATOR_POS;
//    public double TRACK_BLOCK_LENGTH;
//    public double NOTE_SPACING_FACTOR;
//    public double TRACK_BLOCK_HEIGHT;
//	
    public Font TITLE_FONT;
    public Font TITLE_MINOR_FONT;
//    public Font TEMPO_FONT; 
//    public Font FRACTION_FONT;
//    public Font KEY_FONT;
    public Font LEGEND_FONT;
//    
    public BasicStroke STROKE;
	
	public DocumentFormat(double factor, PageFormat pageFormat) {
		
		LINE_WIDTH = 1;

		TITLE_FONT = new Font("Times New Roman", Font.BOLD, (int) (24 * factor));
		TITLE_MINOR_FONT = new Font("Times New Roman", Font.PLAIN, (int) (24 * factor));
		LEGEND_FONT = new Font("Calibri", Font.PLAIN, (int) (12 * factor));
		
		
		STROKE = new BasicStroke((float)(LINE_WIDTH * factor), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	}
	
}
