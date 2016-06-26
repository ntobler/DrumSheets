import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;

public class PageTransformer {

	private PageFormat pageFormat;
	
	/*private double pageHeigth;
	private double pageWidth;
    
	private double pageMarginX;
	private double pageMarginY;*/
    
	private AffineTransform transform;
    
	private double lastWidth;
	private double maxHeigth;
    
	private double availableXSpace;
	private double availableYSpace;
    
	private double columnTransformX;
	private double lineTransformY;
	private double pageTransformY;
    
	private int page;
	
	public interface PageCreatedListener {
		public void onPageCreated(int page);
	}
	
	private PageCreatedListener pageCreatedListener;
	
    public PageTransformer(PageFormat pageFormat, PageCreatedListener pageCreatedListener) {
    	
    	this.pageFormat = pageFormat;
    	this.pageCreatedListener = pageCreatedListener;
    	
    	transform = new AffineTransform();
    }

	public void setPageFormat(PageFormat pageFormat) {
    	
    	this.pageFormat = pageFormat;
    }
    
    public void goToFirstPage() {
    	
    	page = 1;
    	
    	lastWidth = 0;
    	maxHeigth = 0;
    	
    	availableXSpace = pageFormat.getImageableWidth();
    	availableYSpace = pageFormat.getImageableHeight();
    	
    	columnTransformX = 0;
    	lineTransformY = 0;
    	pageTransformY = 0;
    	
    	pageCreatedListener.onPageCreated(page);
    }
    
    public void transformSpace(double width, double height) {
    	
    	if (width > availableXSpace) {
    		columnTransformX = 0;
    		availableXSpace = pageFormat.getImageableWidth();
    		
    		transformToNewLine(maxHeigth);
    		
    		lastWidth = 0;
    		maxHeigth = 0;
    	}
    	
    	columnTransformX += lastWidth;
    	availableXSpace = pageFormat.getImageableWidth() - columnTransformX - width;
    	
    	lastWidth = width;
    	
    	if (maxHeigth < height) {
    		maxHeigth = height;
    	}
    	
    }
    
    public void transformToNewLine(double heigth) {
    	
    	if (heigth < availableYSpace) {
        	
    		lineTransformY += maxHeigth;
	    	availableYSpace = pageFormat.getImageableHeight() - lineTransformY - maxHeigth;
	    	
	    	columnTransformX = 0;
			availableXSpace = pageFormat.getImageableWidth();
			lastWidth = 0;
			maxHeigth = 0;
	    	
    	}
    	else {
    		transformToNewPage();
    	}
    }
    
    public void transformToNewPage() {
    	
    	page++;
    	
    	pageTransformY += pageFormat.getHeight();
    	
    	lineTransformY = 0;
		availableYSpace = pageFormat.getImageableHeight();
    	
    	columnTransformX = 0;
		availableXSpace = pageFormat.getImageableWidth();
		maxHeigth = 0;
		lastWidth = 0;
		
		pageCreatedListener.onPageCreated(page);
    }
    
    public AffineTransform getTransform() {
    	
    	double tx = pageFormat.getImageableX() + columnTransformX;
    	double ty = pageTransformY + pageFormat.getImageableY() + lineTransformY;
    			
    	transform.setToTranslation(tx, ty);
    	
    	return transform;
    }
    
    public AffineTransform getPageTransform() {
    	
    	transform.setToTranslation(0, pageTransformY);
    	return transform;
    }
    
    public AffineTransform translateToPageIndex(int pageIndex) {
    	
    	transform.setToTranslation(0, -pageIndex * pageFormat.getHeight());
		return transform;
    }
    
    public int getPageCount() {
    	return page;
    }
}
