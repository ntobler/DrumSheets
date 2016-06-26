import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class DrumMapEditor extends JFrame{
	
	private static final long serialVersionUID = -6420861426405481264L;
	
	private JTable table;
	
	public DrumMapEditor() {
		
	    JPanel panel = new JPanel();
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    this.add(panel);
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	    DefaultTableModel model = populateTable();
	    table = new JTable(model);
	    panel.add(new JScrollPane(table));
	    
	    JTableHeader th = table.getTableHeader();
	    TableColumnModel tcm = th.getColumnModel();
	    
	    tcm.getColumn(0).setHeaderValue("Key");
	    tcm.getColumn(1).setHeaderValue("Key name");
	    tcm.getColumn(2).setHeaderValue("Key description");
	    tcm.getColumn(3).setHeaderValue("Track line");

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(buttonPanel);
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	    
	    JButton newEntryButton = new JButton("New entry");
	    buttonPanel.add(newEntryButton);
	    newEntryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.addRow(new Object[]{"", "", "", ""});
			}
		});
	    
	    JButton deleteEntryButton = new JButton("Delete entry");
	    buttonPanel.add(deleteEntryButton);
	    deleteEntryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int selectedRow = table.getSelectedRow();
				
				if (selectedRow >= 0) {
					model.removeRow(selectedRow);
				}
			}
		});
	    
	    JButton newLineButton = new JButton("Save");
	    buttonPanel.add(newLineButton);
	    newLineButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrumMap();
			}
		});
	    
		pack();
		setVisible(true);
	}
	
	private DefaultTableModel populateTable() {
		
		DrumMap drumMap = DrumMapParser.read("drumMap.dmp");
		
		DefaultTableModel model = new DefaultTableModel();

	    model.setColumnCount(4);
	    
	    for (int key: drumMap.getKeySet()) {
	    	
	    	String[] row = new String[4];
	    	
	    	row[0] = drumMap.getKeyString(key);
	    	row[1] = drumMap.getKeyName(key);
	    	row[2] = drumMap.getKeyDescription(key);
	    	row[3] = Integer.toString(drumMap.getTrackLine(key));
	    	
	    	model.addRow(row);
	    }

	    return model;
	}
	
	private void saveDrumMap() {
		
		try {
		
		DrumMap drumMap = new DrumMap();
		
		int rowCount = table.getRowCount();
		
		for (int i = 0; i < rowCount; i++) {
			
			int key = keyStringToInt((String) table.getValueAt(i, 0));
			String keyName = (String) table.getValueAt(i, 1);
			String keyDescription = (String) table.getValueAt(i, 2);
			int trackLine = Integer.parseInt((String) table.getValueAt(i, 3));
			
			drumMap.addKey(key, keyName, keyDescription, trackLine);
		}

		DrumMapParser.write("drumMap.dmp", drumMap);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
			    "Parsing error. Please check table",
			    "Error",
			    JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	
	public static final Map<String,Integer> TONE_STR_TO_INT_MAP;
    static {
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	map.put("A", 0);
		map.put("A#", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("C#", 4);
		map.put("D", 5);
		map.put("D#", 6);
		map.put("E", 7);
		map.put("F", 8);
		map.put("F#", 9);
		map.put("G", 10);
		map.put("G#", 11);
		TONE_STR_TO_INT_MAP = Collections.unmodifiableMap(map);
    }
	
	int keyStringToInt(String keyString) {
		
		char tone = keyString.charAt(0);
		String toneString;
		String scaleString;
		if (keyString.charAt(1) == '#') {
			toneString = tone + "#";
			scaleString = keyString.charAt(2) + "";
		}
		else {
			toneString = tone + "";
			scaleString = keyString.charAt(1) + "";
		}
		
		int key = TONE_STR_TO_INT_MAP.get(toneString) + Integer.parseInt(scaleString) * 12;
		
		return key;
	}
}
