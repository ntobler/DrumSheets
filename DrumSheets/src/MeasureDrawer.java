import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class MeasureDrawer {
	
	private MidiDecode midi;
	
	private DrumFormat f;
	private DrumMap drumMap;
	
	private int MeasureIndex;
	private Measure currentMeasure;
	private Measure lastMeasure;
	private Tempo lastTempo;
	private long lastKeyTick;
	

	public MeasureDrawer(DrumFormat dlf, DrumMap drumMap) {
		this.f = dlf;
		this.drumMap = drumMap;
		lastMeasure = null; 

		lastKeyTick = 0;
	}
	
	public double getSpace(Measure measure) {
		
		return getTickPos(measure.tickLength) + f.MEASURE_X_OFFSET;
		
	}
	
	public void drawMeasure(Graphics2D g2, Measure measure) {
		
		currentMeasure = measure;
		
		drawMeasureLines(g2);
		drawFraction(g2);
		drawTempo(g2);

		g2.translate(f.MEASURE_X_OFFSET, 0);
		
		for (Key key: currentMeasure.keys) {
			
			drawKey(g2, key);
			drawTickIndicator(g2, key);
		}
		
		lastMeasure = currentMeasure;
	}
	
	public void drawFraction(Graphics2D g2) {
		
		if ((lastMeasure == null) ||
			(currentMeasure.numerator != lastMeasure.numerator) ||
			(currentMeasure.denominator != lastMeasure.denominator)) {
			
			g2.setColor(Color.BLACK);
	    	g2.setFont(f.FRACTION_FONT);
	    	g2.drawString(Integer.toString(currentMeasure.numerator), (float) (4 * f.Y_FACTOR), (float) (2*12  * f.Y_FACTOR));
	    	g2.drawString(Integer.toString(currentMeasure.denominator), (float) (4 * f.Y_FACTOR), (float) (2*20  * f.Y_FACTOR));
			
		}
		
	}
	
	private void drawTempo(Graphics2D g2) {
		
		for (Tempo currentTempo: currentMeasure.tempos)
		
		if (currentTempo != lastTempo) {
			
			lastTempo = currentTempo;
			
			g2.setColor(Color.BLACK);
	    	g2.setFont(f.TEMPO_FONT);
	    	g2.drawString(
	    			"\u266A = " + Integer.toString(currentTempo.bpm) + "bpm",
	    			(float) getRelativeTickPos(currentTempo.tick),
	    			-10f);
		}
	}
	
	private double getTickPos(long tick) {
		return tick * f.NOTE_SPACING_FACTOR;
	}
	
	private long getRelativeTick(long tick) {
		return (tick - currentMeasure.tickStart) ;
	}
	
	private double getRelativeTickPos(long tick) {
		return getRelativeTick(tick) * f.NOTE_SPACING_FACTOR;
	}
	
	private void drawMeasureLines(Graphics2D g2) {
    	
		double length = getTickPos(currentMeasure.tickLength) + f.MEASURE_X_OFFSET;
		
    	g2.setColor(Color.GRAY);
    	
    	for (int i = 0; i < f.TRACK_LINE_COUNT; i++) {
    		
    		double y = i * f.TRACK_LINE_SPACING;
    		g2.draw(new Line2D.Double(0, y, length, y));
    	}
    	
    	g2.draw(new Line2D.Double(0, 0, 0, (f.TRACK_LINE_COUNT-1) * f.TRACK_LINE_SPACING));
    	g2.draw(new Line2D.Double(length, 0, length, (f.TRACK_LINE_COUNT-1) * f.TRACK_LINE_SPACING));
    	
    	g2.setFont(f.KEY_FONT);
    	g2.drawString(Integer.toString(currentMeasure.number), 0, -4);
    	
    }
	
	private void drawKey(Graphics2D g2, Key key) {
		
		
    	double x = getRelativeTickPos(key.tick);
    	double y = drumMap.getTrackLine(key.key) * f.TRACK_LINE_SPACING -1;
    	
    	final double PADDING = 0;
    	
    	g2.setFont(f.KEY_FONT);
    	
    	String text = drumMap.getKeyName(key.key);
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
    }
	
	
	private void drawTickIndicator(Graphics2D g2, Key key) {
    	
		long tick = getRelativeTick(key.tick);
		long tickDif;
		
		long lastNoteTick = (tick / f.TICKS_PER_NOTE) * f.TICKS_PER_NOTE;
		
		/*if ((lastKeyTick < lastNoteTick) ||
			(lastKeyTick > tick)) {
			lastKeyTick = lastNoteTick;
		}
		else {
			tickDif = tick - lastKeyTick;
		}*/

		tickDif = tick - lastKeyTick;
		
    	g2.setColor(Color.GRAY);
    	double x = getTickPos(tick);
    	g2.draw(new Line2D.Double(x, f.INDICATOR_POS, x, f.INDICATOR_POS + f.INDICATOR_SIZE));

    	if (tick % (f.TICKS_PER_NOTE*4 / 4) == 0) {		//4th

		}
    	else if (tick % (f.TICKS_PER_NOTE*4 / 8) == 0) {		//8th
    		drawIndicatorBar(g2, f.TICKS_PER_NOTE / 2, tick, 0);
		}
		else if (tick % (f.TICKS_PER_NOTE*4 / 16) == 0) {	//16th
			drawIndicatorBar(g2, f.TICKS_PER_NOTE / 4, tick, 0);
    		drawIndicatorBar(g2, f.TICKS_PER_NOTE / 4, tick, 1);
		}
    	
    	
		else if (tick % (f.TICKS_PER_NOTE*8/3 / 4) == 0) {		//4th
			drawTripletIndicator(g2, f.TICKS_PER_NOTE*8/3 / 4, tick);
		}
    	else if (tick % (f.TICKS_PER_NOTE*8/3 / 8) == 0) {		//8th
    		drawIndicatorBar(g2, f.TICKS_PER_NOTE*8/3 / 8, tick, 0);
    		drawTripletIndicator(g2, f.TICKS_PER_NOTE*8/3 / 8, tick);
		}
    	else if (tick % (f.TICKS_PER_NOTE*8/3 / 8) == 0) {		//8th
    		drawIndicatorBar(g2, f.TICKS_PER_NOTE*8/3 / 8, tick, 0);
    		drawIndicatorBar(g2, f.TICKS_PER_NOTE*8/3 / 8, tick, 1);
    		drawTripletIndicator(g2, f.TICKS_PER_NOTE*8/3 / 8, tick);
		}
    	

    	if (tickDif == (f.TICKS_PER_NOTE * 3/2 / 1 )) {
    		drawDotted(g2, lastKeyTick, 0);
		}
    	else if (tickDif == (f.TICKS_PER_NOTE * 3/2 / 2)) {
    		drawDotted(g2, lastKeyTick, 1);
		}
		else if (tickDif == (f.TICKS_PER_NOTE * 3/2 / 4)) {
			drawDotted(g2, lastKeyTick, 2);
		}

    	lastKeyTick = tick;
    	
    }
	
	private void drawIndicatorBar(Graphics2D g2, long dif, long endTick, int level) {
		
		double a = getTickPos(endTick - dif);
		
		Rectangle2D r = new Rectangle2D.Double(
				a,
				f.INDICATOR_POS + f.INDICATOR_SIZE - f.INDICATOR_BAR_SIZE - (level*f.INDICATOR_BAR_DISTANCE),
				getTickPos(dif),
				f.INDICATOR_BAR_SIZE);
		g2.draw(r);
		g2.fill(r);
	}
	
	private void drawTripletIndicator(Graphics2D g2, long dif, long endTick) {

		CustomGraphics.drawCenteredString(
				g2,
				(float) getTickPos(endTick - (dif/2)),
				(float) (f.INDICATOR_POS + f.INDICATOR_SIZE + (6*f.Y_FACTOR)),
				"3");
	}
	
	
	private void drawDotted(Graphics2D g2, long tick, int level) {

		Ellipse2D r = new Ellipse2D.Double(
				getTickPos(tick) + (2 * f.X_FACTOR),
				f.INDICATOR_POS + f.INDICATOR_SIZE - (4*f.Y_FACTOR) - (level*f.INDICATOR_BAR_DISTANCE),
				4 * f.Y_FACTOR,
				4 * f.Y_FACTOR);
		g2.fill(r);
	}
	
}
