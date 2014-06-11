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
	public JFileChooser jfc = new JFileChooser();
	private DefaultTableModel dtm;
	private int depth = 3;
	private JTable table;
	private final JLabel statusLabel = new JLabel("Browsing Depth: 3");
	File startDir;
	
	
	public KillerGui(){
		initTable();
		
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);	
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JMenuBar menuBar = initMenuBar();
		getContentPane().add(menuBar,BorderLayout.NORTH);
		getContentPane().add(statusLabel, BorderLayout.SOUTH);
		setTitle("Copy Killer");
		setSize(720, 480);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initTable(){
		dtm = initTableModel();
		table = new JTable(dtm);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					JTable tab = (JTable)e.getSource();
					int row = tab.getSelectedRow();
					int col = tab.getSelectedColumn();
					KillerTableOperation.openFile(dtm, row, col);
				}
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
	
	
	private void callPopupMenu(MouseEvent e){
		JPopupMenu tableRMenu = new JPopupMenu();
		JMenuItem tblRDelete = new JMenuItem("Delete item");
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
		tableRMenu.add(tblRDelete);
		tableRMenu.show(this, e.getXOnScreen(), e.getYOnScreen());
	}
	
	private JMenuBar initMenuBar(){
		JMenuBar menuBar = new JMenuBar();		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);		
		JMenuItem mntmOpenFolder = new JMenuItem("Open Folder");
		mntmOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{					
					boolean gotStartingDirectory = chooseFile();
					if (gotStartingDirectory){
						statusLabel.setText("Loading Table Rows...");
						KillerTableOperation.updateTable(dtm, startDir, depth);
						statusLabel.setText(dtm.getRowCount() + " Rows Updated: Browsing Depth: " + depth);
					}
				}
				catch (NullPointerException npe){
					System.err.println("NullPointerException: Invalid File or Folder");
					statusLabel.setText("Browsing Depth: " + depth);
				}
			}
		});
		mnFile.add(mntmOpenFolder);
		
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
		mnFile.add(mntmDepth);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		mnFile.add(mntmQuit);
		return menuBar;
	}
	
	private DefaultTableModel initTableModel(){
		Vector<String> columnTitles = new Vector<String>();
		Vector<Vector<String>> tableData = new Vector<Vector<String>>();
		columnTitles.add("File");
		columnTitles.add("Duplicate File");	
		DefaultTableModel dtm = new DefaultTableModel(tableData, columnTitles){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		
		return dtm;
	}

	public boolean chooseFile(){
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = jfc.showOpenDialog(jfc);;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            startDir = jfc.getSelectedFile();
            return true;
        }
        else return false;
	}
	
	public static void main(String[] args){
		KillerGui kg = new KillerGui();
	}

}
