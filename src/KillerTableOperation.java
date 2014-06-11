/*
 * @Author Chris Rorvig
 * Last updated 6/11/2014
 * This class contains Table modification and 
 * file IO operations
 */

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;


public class KillerTableOperation {
	
	//Remove all rows from the table
	public static void clearTable(DefaultTableModel table){
		table.setRowCount(0);
		table.fireTableDataChanged();
	}
	
	//Generate the list of copied files and populate the JTable
	public static void updateTable(DefaultTableModel table, File folder, int depth){
		clearTable(table);
		CopyKiller ck = new CopyKiller();
		Vector<Vector<String>> resultTable = ck.generateCopyTable(folder, depth);
		for (Vector<String> row : resultTable){
			table.addRow(row);
		}
		table.fireTableDataChanged();
	}	
	
	//Returns the file corresponding to position (row, col)
	public static File getFile(DefaultTableModel table, int row, int col){
		String path = (String)table.getValueAt(row, col);
		File file = new File(path);
		return file;
	}	
	
	//Delete file at (row, col)
	public static boolean deleteFile(DefaultTableModel table, int row, int col){
		String path = (String)table.getValueAt(row, col);
		File file = new File(path);
		return (file.exists()) ? file.delete(): false;	
	}
	
	//Open file at (row, col)
	public static void openFile(DefaultTableModel table, int row, int col){
		File file = getFile(table, row, col);
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			System.err.println("IOException encountered: could not open " + file.toString());
		}
	}
}
