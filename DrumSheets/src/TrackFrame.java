import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class TrackFrame extends JFrame {
	
	private Canvas canvas;
	
	private Paintable paintable;
	
	private int scrollValue;
	private int zoomValue;
	
	public TrackFrame(Paintable paintable) {
		
		this.paintable = paintable;
		
		scrollValue = 0;
		zoomValue = 0;
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new GUIDispatcher());

		canvas = new Canvas();
		add(canvas);
		
		addComponentListener(new ComponentAdapter() {  
	        public void componentResized(ComponentEvent evt) {
	            paintable.setImageDimension(canvas.getSize());
	        }
		});
		
		setVisible(true);
		
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
	}
	
	public class Canvas extends JPanel {
		
		@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        g.setColor(Color.WHITE);
	        g.fillRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.BLACK);
	        
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	        
	        transform(g2);
	        
	        paintable.paint(g2);
	        
	        g2.dispose();
	    }
		
	}
	
	private void transform(Graphics2D g2) {
		
		int canvasWidth = this.getWidth();
		int canvasHeight = this.getHeight();
		
		double zoom = Math.pow(0.5, zoomValue);
		
		double tx = canvasWidth / 2;
		double ty = canvasHeight / 2 + 100 * scrollValue * zoom;
		
		g2.translate(tx, ty);
		g2.scale(zoom, zoom);
	}
	
	public void addScrollValue(int value) {
		scrollValue += value;
	}
	
	public void addZoomValue(int value) {
		zoomValue += value;
	}
	
	private class GUIDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
        	if (e.getID() == KeyEvent.KEY_PRESSED) {
        		onKeyPressed(e);
            }
            return false;
        }
    }
	
	private void onKeyPressed(KeyEvent e) {
		
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
	
	
}
