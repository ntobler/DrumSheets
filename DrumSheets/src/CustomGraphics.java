import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class CustomGraphics {

	public static Shape circle(double x, double y, double radius) {
	   return new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2);
	}
	
	public static void drawProgressBar(Graphics2D g2, int x, int y, int width, int height, double progress) {
		g2.draw(new Rectangle2D.Double(x, y, width, height));
		g2.fill(new Rectangle2D.Double(x, y, width * progress, height));
	}
	
	public static void drawVerticalProgressBar(Graphics2D g2, int x, int y, int width, int height, double progress) {
		g2.draw(new Rectangle2D.Double(x, y, width, height));
		g2.fill(new Rectangle2D.Double(x, y + height * (1-progress), width, height * progress));
	}
	
	public static void drawCenteredProgressBar(Graphics2D g2, double x, double y, double width, double height, double progress) {
		
		g2.draw(new Rectangle2D.Double(x - (width/2), y - (height/2), width, height));
		g2.fill(new Rectangle2D.Double(x - (width/2), y - (height/2), (int) (width * progress), height));
	}
	
	public static void drawLockOn(Graphics2D g2, double radius) {
		
		double a = radius + 2;
		double l = 5;
		
		
		//upper right
		g2.draw(new Line2D.Double(a, a, a-l, a));	//left
		g2.draw(new Line2D.Double(a, a, a, a-l));	//down
		
		//lower right
		g2.draw(new Line2D.Double(a, -a, a, -a+l));	//up	
		g2.draw(new Line2D.Double(a, -a, a-l, -a));	//left
		
		//lower left
		g2.draw(new Line2D.Double(-a, -a, -a+l, -a));	//right	
		g2.draw(new Line2D.Double(-a, -a, -a, -a+l));	//up
		
		//upper left
		g2.draw(new Line2D.Double(-a, a, -a, a-l));	//down	
		g2.draw(new Line2D.Double(-a, a, -a+l, a));	//right
		
	}
	
	public static void drawCenteredString(Graphics2D g2, float x, float y, String text) {
	    FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    x -= metrics.stringWidth(text) / 2;
	    y += metrics.getAscent()/2;
	    g2.drawString(text, x, y);
	}
	
	
	
	public static enum HorizontalAlign {
		LEFT,
		CENTER,
		RIGHT
	}
	
	public static enum VerticalAlign {
		TOP,
		CENTER,
		BOTTOM
	}

	private static HorizontalAlign horizontalAlign = HorizontalAlign.LEFT;
	private static VerticalAlign verticalAlign = VerticalAlign.BOTTOM;
	
	public static void setStringAlign(HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
		CustomGraphics.horizontalAlign = horizontalAlign;
		CustomGraphics.verticalAlign = verticalAlign;
	}
	
	public static void drawAlignedString(Graphics2D g2, float x, float y, String text) {
	    FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    
	    switch (horizontalAlign) {
		case CENTER:
			x -= metrics.stringWidth(text) / 2;
			break;
		case LEFT:
			break;
		case RIGHT:
			x -= metrics.stringWidth(text);
			break;
		default:
			break;
	    }
	    
	    switch (verticalAlign) {
		case BOTTOM:
			break;
		case CENTER:
			y += metrics.getAscent()/2; 
			break;
		case TOP:
			y += metrics.getAscent();
			break;
		default:
			break;

	    }
	    g2.drawString(text, x, y);
	}
}
