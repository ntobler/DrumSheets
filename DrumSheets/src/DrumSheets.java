import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class DrumSheets {

	private static DrumLineDrawer drumLineDrawer;
	private static TrackFrame trackFrame;
	private static TrackFrame.Canvas canvas;
	
	private static PageFormat pageFormat;
	
	public static void main(String [] args) throws Exception {
		
		
		
		pageFormat = getStandardPageFormat();
		
		drumLineDrawer = new DrumLineDrawer(pageFormat);
		drumLineDrawer.setDrumMap(getDrumMap());
		trackFrame = new TrackFrame(drumLineDrawer);
		
		trackFrame.setJMenuBar(setupMenuBar());
		
		trackFrame.getCanvas().addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotation = e.getWheelRotation();
				
				if (e.isControlDown()) {
					trackFrame.addZoomValue(rotation);
				}
				else if (e.isShiftDown()) {
					addScaleValue(rotation);
				}
				else if (e.isAltDown()) {
					addXScaleValue(rotation);
				}
				else {
					trackFrame.addScrollValue(-rotation);
				}
			
				canvas.revalidate();
		    	canvas.repaint();
				
			}
		});
		
		canvas = trackFrame.getCanvas();
		
		update();
	}
	
	private static void update() {
		canvas.revalidate();
    	canvas.repaint();
	}
	
	private static DrumMap getDrumMap() {
		
		DrumMap drumMap = new DrumMap();
		
    	/*drumMap.addKey(36, "BD", "Bass Drum", 5);
    	drumMap.addKey(38, "S", "Snare", 4);
    	drumMap.addKey(42, "X", "Closed Hi-Hat", 1);
    	drumMap.addKey(46, "O", "Open Hi-Hat", 1);
    	drumMap.addKey(49, "C1", "Crash 1", 0);
    	drumMap.addKey(57, "C2", "Crash 2", 0);
    	
    	DrumMapParser.write("drumMap.dmp", drumMap);
    	*/
    	drumMap = DrumMapParser.read("drumMap.dmp");
    	
    	return drumMap;
    }
	
	private static PageFormat getStandardPageFormat() {
		
		PageFormat pageFormat;
		
		pageFormat = PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		Paper paper = pageFormat.getPaper();
		double h = paper.getHeight();
		double w = paper.getWidth();
		paper.setImageableArea(36, 36, w-72, h-72);
		pageFormat.setPaper(paper);
		
		
		return pageFormat;
	}
	
	private static PageFormat getPrinterPageFormat() {
		
		PageFormat pageFormat;
		
		pageFormat = PrinterJob.getPrinterJob().defaultPage();
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		Paper paper = pageFormat.getPaper();
		double h = paper.getHeight();
		double w = paper.getWidth();
		paper.setImageableArea(0, 0, w, h);
		//paper.setImageableArea(36, 36, w-72, h-72);
		pageFormat.setPaper(paper);
		
		
		return pageFormat;
	}
	
	
	
	private static JMenuBar setupMenuBar() {
		
		JMenuBar menuBar;
		JMenu menu;
		
		//Create the menu bar.
		menuBar = new JMenuBar();
		
		//Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(menu);
		
		//a group of JMenuItems
		JMenuItem menuItem;
		menuItem= new JMenuItem("Save",  KeyEvent.VK_S);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
			
		});

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
			
		});
		
		menuItem = new JMenuItem("Print", KeyEvent.VK_P);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(drumLineDrawer, getPrinterPageFormat());
				
				if(job.printDialog()) {
					try {
						job.print();
					} catch (PrinterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		
		/*menu = new JMenu("Tracks");
		menu.setMnemonic(KeyEvent.VK_T);
		buildTracksMenu(menu);
		menuBar.add(menu);*/
		
		menu = new JMenu("Open");
		menu.setMnemonic(KeyEvent.VK_O);
		buildOpenMenu(menu);
		menuBar.add(menu);
		
//		menu = new JMenu("Size");
//		menu.setMnemonic(KeyEvent.VK_S);
//		buildSizeMenu(menu);
//		menuBar.add(menu);
		
//		menu = new JMenu("Measures per line");
//		menu.setMnemonic(KeyEvent.VK_M);
//		buildMeasuresPerLineMenu(menu);
//		menuBar.add(menu);

		menu = new JMenu("Drum map editor");
		menu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Edit", KeyEvent.VK_E);
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DrumMapEditor();
			}
			
		});
		
		
		return menuBar;
	}
	
	/*private static void buildTracksMenu(JMenu menu) {
		
		Track[] tracks = drumInterpreter.getTracks();
		
		int trackIndex = 0;
		
		for (Track track : tracks) {
			
			final int index = trackIndex;
			
			JMenuItem menuItem = new JMenuItem("Track "+ Integer.toString(trackIndex+1));
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					drumInterpreter.setTrack(index);
					update();
				}
				
			});
			
			trackIndex++;
		}
		
	}*/
	
	private static void buildOpenMenu(JMenu menu) {

		File dir = new File(System.getProperty("user.dir"));

    	File[] files = dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".mid"); }
    	});
		
		for (File file : files) {
			
			JMenuItem menuItem = new JMenuItem(file.getName());
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					drumLineDrawer.setFile(file);
					update();
				}
				
			});
		}
		
		JMenuItem menuItem = new JMenuItem("Refresh");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				menu.removeAll();
				buildOpenMenu(menu);
			}
			
		});
	}
	
	/*private static void buildSizeMenu(JMenu menu) {
	
		int[] sizes = {50,60,70,80,90,100,110,120,130,140,150,200,300};
		
		for (int size : sizes) {
			
			
			JMenuItem menuItem = new JMenuItem(Integer.toString(size) + "%");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					drumLineDrawer.setSize(size);
					update();
				}
				
			});
		}
	}*/
	
	/*private static void buildMeasuresPerLineMenu(JMenu menu) {
		
		double[] values = {0.5,0.75,1,1.25,1.5,1.75,2,};
		
		for (double value : values) {
			
			
			JMenuItem menuItem = new JMenuItem(Double.toString(value) + " per line");
			menu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					drumLineDrawer.setXFactor(value);
					update();
				}
				
			});
		}
	
	}*/
	
	private static int scale = 1;
	private static int xScale = 10;
	
	private static void addScaleValue(int value) {
		
		scale += value;
		
		double d = Math.pow(0.9, scale);
		
		drumLineDrawer.setSize(d);
	}
	
	private static void addXScaleValue(int value) {
		
		xScale += value;
		
		if (xScale < 1) xScale = 1;
		
		double d = xScale * 0.1;
		
		drumLineDrawer.setXFactor(d);
	}
	

}
