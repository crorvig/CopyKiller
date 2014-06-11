/*
 * @Author Chris Rorvig
 * Last updated: 6/11/2014
 * This class contains all the GUI components as well as the FileChooser
 * and Depth Setting manipulation functions
 */

import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;


public class KillerGui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 123752233;
	private JFileChooser jfc = new JFileChooser();
	private JPopupMenu tableRMenu;
	private DefaultTableModel dtm;
	private int depth = 3;
	private JTable table;
	private final JLabel statusLabel = new JLabel("Browsing Depth: 3");
	File startDir;
	
	//Construct the window, initialize the gui components
	public KillerGui(){
		initTable();
		initPopupMenu();
		
		JMenuBar menuBar = initMenuBar();
	
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);	
		
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(menuBar,BorderLayout.NORTH);
		getContentPane().add(statusLabel, BorderLayout.SOUTH);
		
		setTitle("Copy Killer");
		setSize(720, 480);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//Set up JTable and DataTableModel
	private void initTable(){
		
		dtm = initTableModel();
		
		table = new JTable(dtm);
		
		//Add right click and double click functions
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				//Double Click : Open File
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					JTable tab = (JTable)e.getSource();
					int row = tab.getSelectedRow();
					int col = tab.getSelectedColumn();
					KillerTableOperation.openFile(dtm, row, col);
				}

				//Right Click : Popup Menu
				else if (e.getButton() == MouseEvent.BUTTON3){
					Point p = e.getPoint();
					int row = table.rowAtPoint(p);
					int col = table.columnAtPoint(p);
					table.changeSelection(row, col, false, false);
					callPopupMenu(e);
				}
			}
		});
	}
	
	//popup initialization for constructor
	private void initPopupMenu(){
		tableRMenu = new JPopupMenu();
		
		JMenuItem tblRDelete = new JMenuItem("Delete File");
		tblRDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				boolean deleted = KillerTableOperation.deleteFile(dtm,row,col);
				if (deleted) {
					dtm.setValueAt("", row, col);
					statusLabel.setText("File Successfully Deleted:  Current Browsing Depth: " + depth);
					dtm.fireTableDataChanged();
				}
				else statusLabel.setText("Failed to Delete:  Current Browsing Depth: " + depth);
			}
		});
		
		JMenuItem tblROpen = new JMenuItem("Open File");
		tblROpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				KillerTableOperation.openFile(dtm, row, col);
			}
		});
		
		tableRMenu.add(tblROpen);
		tableRMenu.add(tblRDelete);
	}
	
	//opens the PopupMenu
	private void callPopupMenu(MouseEvent e){
		Point p = e.getPoint();
		tableRMenu.show(this, p.x, p.y);
	}
	
	//Initializes the menu bar
	private JMenuBar initMenuBar(){
		JMenuBar menuBar = new JMenuBar();		
		JMenu mnFile = new JMenu("File");
		
		//Open Folder - Implements updateTable()
		JMenuItem mntmOpenFolder = new JMenuItem("Open Folder");
		mntmOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					
					statusLabel.setText("Loading Table Rows...");
					boolean gotStartingDirectory = chooseFile();
					
					//file chosen
					if (gotStartingDirectory){
						KillerTableOperation.updateTable(dtm, startDir, depth);
						statusLabel.setText(dtm.getRowCount() + " Rows Updated: Browsing Depth: " + depth);
					}
					
					//else reset status
					else statusLabel.setText("Browsing Depth: " + depth);
				}
				catch (NullPointerException npe){
					System.err.println("NullPointerException: Invalid File or Folder");
					statusLabel.setText("Browsing Depth: " + depth);
				}
			}
		});

			
		//Set Browsing Depth
		JMenuItem mntmDepth = new JMenuItem("Set Browsing Depth");
		mntmDepth.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String dp = JOptionPane.showInputDialog("Enter Browsing Depth: ");
				try {
					depth = Math.max(Integer.parseInt(dp), 0);
				}
				
				//If invalid number, defaults to zero
				catch (NumberFormatException nfe){
					depth = 0;
				}
				statusLabel.setText("Browsing Depth: " + depth);
			}
		});
		
		JMenuItem mntmDup = new JMenuItem("Remove All Duplicates");
		mntmDup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				KillerTableOperation.deleteAllDuplicates(dtm);
			}
		});
		//Quit button
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showConfirmDialog(rootPane, "CopyKiller v0.11\nCopyright 2014 by Chris Rorvig", "CopyKiller v0.11", JOptionPane.OK_OPTION);
			}
		});
		
		mnFile.add(mntmOpenFolder);
		mnFile.add(mntmDepth);
		mnFile.add(mntmDup);
		mnFile.add(mntmAbout);
		mnFile.add(mntmQuit);
		menuBar.add(mnFile);
		
		return menuBar;
	}
	
	//Initializes the DefaultTableModel object
	private DefaultTableModel initTableModel(){
		Vector<String> columnTitles = new Vector<String>();
		
		Vector<Vector<String>> tableData = new Vector<Vector<String>>();
		columnTitles.add("File");
		columnTitles.add("Duplicate File");	
		
		DefaultTableModel dtm = new DefaultTableModel(tableData, columnTitles){
			private static final long serialVersionUID = 4045087527715574481L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		
		return dtm;
	}

	//JFileChooser : Returns true if a folder is chosen, false otherwise
	public boolean chooseFile(){
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = jfc.showOpenDialog(jfc);;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            startDir = jfc.getSelectedFile();
            return true;
        }
        else return false;
	}
	
	//Main method
	public static void main(String[] args){
		KillerGui kg = new KillerGui();
	}

}
