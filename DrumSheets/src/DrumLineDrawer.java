import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
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


public class DrumLineDrawer  implements Paintable, Printable, PageTransformer.PageCreatedListener{

	private final int ROUND = 120;
	private double xFactor = 1;
	private double factor = 0.5;

	Graphics2D g2;
    
	private DrumMap drumMap;
	private AffineTransform standardTransform;
	private PageTransformer pageTransformer;
	private PageFormat pageFormat;
	
	private DrumFormat drumF;
	private DocumentFormat docF;
	
	private int trackIndex;
	private int tempoTrakIndex;
	
	private File file;
	
	public DrumLineDrawer(PageFormat pageFormat) {
		
		this.pageFormat = pageFormat;
		this.trackIndex = 0;
		this.file = null;
	}
	
	public void setDrumMap(DrumMap drumMap) {
		this.drumMap = drumMap;
	}

	private final int NOTE_ON = 0x90;
	//private final int NOTE_OFF = 0x80;
	private final int TEMPO = 0x51;
	private final int TIME_SIGNATURE = 0x58;
	
	
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
	
	public void setSize(double size) {
		this.factor = 0.75 / size;
	}
	
	public void setXFactor(double xFactor) {
		this.xFactor = xFactor;
	}

	public void setFile(File file) {
		this.file = file;
	}

	private void draw() {
		
		standardTransform = g2.getTransform();
		pageTransformer = new PageTransformer(pageFormat, this);
		pageTransformer.goToFirstPage();
		
		if (file == null) {
			drawNoFileOpened();
			return;
		}
		
		MidiDecode midi = new MidiDecode();
		try {
			midi.getFromMidiFile(file);
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		drumF = new DrumFormat(factor, pageFormat, xFactor, midi);
		docF = new DocumentFormat(0.75, pageFormat);
		
		MeasureDrawer measureDrawer = new MeasureDrawer(drumF, drumMap);

        
        g2.setStroke(docF.STROKE);

        drawTitle();
        
        for (Measure measure: midi.measures) {
        	
        	pageTransformer.transformSpace(measureDrawer.getSpace(measure), drumF.TRACK_BLOCK_HEIGHT);
        	setTransform(pageTransformer.getTransform());
        	
        	measureDrawer.drawMeasure(g2, measure);
        }
	}

    private void drawTitle() {
    	
    	double width = pageFormat.getImageableWidth();
    	
    	pageTransformer.transformSpace(width, 150);
    	setTransform(pageTransformer.getTransform());
    	
    	MetaDataEditor editor = new MetaDataEditor();
    	
    	g2.setColor(Color.BLACK);
    	
    	g2.setFont(docF.LEGEND_FONT);
    	CustomGraphics.drawCenteredString(g2, (float)width / 2, 24, String.format("Format: %f, %f", xFactor, factor));
    
    	
    	g2.setFont(docF.TITLE_FONT);
    	CustomGraphics.drawCenteredString(g2, (float)width / 2, 50, file.getName());//editor.getTitle());
    
    	g2.setFont(docF.TITLE_MINOR_FONT);
    	CustomGraphics.drawCenteredString(g2, (float)width / 2, 100, editor.getAuthor());
    	
    	drawLegend();
    	
    }
    
    private void drawLegend() {
    	
    	int line = 0;
    	
    	g2.setFont(docF.LEGEND_FONT);
    	
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
    
    private final Font NO_FILE_LOADED_FONT = new Font("Calibri", Font.BOLD, 36);
    private final Font PAGE_NUMBER_FONT = new Font("Times New Roman", Font.PLAIN, (int) (24 * factor));
    
    private void drawNoFileOpened() {
    	
    	setTransform(pageTransformer.getPageTransform());
    	
    	g2.setColor(Color.GRAY);

    	g2.setFont(NO_FILE_LOADED_FONT);
    	
    	CustomGraphics.drawCenteredString(
    			g2,
    			(float) pageFormat.getWidth()/2,
    			(float) pageFormat.getHeight()/2,
    			"NO FILE LOADED");

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
    	g2.setFont(PAGE_NUMBER_FONT);
    	g2.drawString(
    			Integer.toString(page),
    			(float) (pageFormat.getImageableWidth()/2 + pageFormat.getImageableX()),
    			(float) (pageFormat.getImageableHeight() + pageFormat.getImageableY()));
    	
    }
    
    private void setTransform(AffineTransform transform) {
    	
    	g2.setTransform(standardTransform);
    	g2.transform(transform);

    }
	
	@Override
	public void paint(Graphics2D g2) {

		this.g2 = g2;
		g2.translate(-pageFormat.getWidth()/2, 0);
		
		try {
			draw();
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
	    	//this.pageFormat = pageFormat;
			draw();
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
