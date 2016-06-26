

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class Printer implements Printable {

	private final Paintable paintable;
	private final AffineTransform transform;
	
	public Printer(Paintable paintable, AffineTransform transform) {
		this.paintable = paintable;
		this.transform = transform;
	}
	
	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		
		if (page > 0) {
	         return NO_SUCH_PAGE;
	    }
		
		Graphics2D g2 = (Graphics2D)g;
		
	    g2.transform(transform);

	    paintable.paint(g2);
	    
	    return PAGE_EXISTS;
	}

}
