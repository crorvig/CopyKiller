/*
 * @Author Chris Rorvig
 * Last updated: 6/11/2014
 * 
 * This class contains the file browsing algorithms.  An arraylist of 
 * FileInfo objects is filled by fillFileTree(), and 
 * generateCopyTable() creates a table structure to be used by 
 * KillerTableOperation.updateTable()
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;


public class CopyKiller {
	private ArrayList<FileInfo> fileList;
	
	public CopyKiller(){
		fileList = new ArrayList<FileInfo>();
	}
	
	/*	Gets all the files in a folder up to depth {levels} 
	 *  and stores the a FileInfo object for each file encountered
	 *  NullPointerExceptions occur with invalid directory names and
	 *  lack of read Permissions
	 */
	public void fillFileTree(File file, int levels){
		if (file.isDirectory()){
			File[] files = file.listFiles();
			try {
				for (int i = 0; i < files.length; i++){
					if (files[i].isFile()) fileList.add(new FileInfo(files[i]));
					else if (files[i].isDirectory() && levels > 0) fillFileTree(files[i], levels-1);
				}
			}
			catch (NullPointerException npe){
				System.err.println("NullPointerException found with File :" + file.toString());
			}
		}
	}
		
	//sorts the file List by MD5 string values
	void sortByHash(){
		Collections.sort(fileList, FileInfo.FileMD5Comparator);
	}
	
	//sorts the file list by file length, and generates MD5 hashes for 
	//items with the same size
	void sortByLength(){
		Collections.sort(fileList);
		for (int i = 0; i < fileList.size()-1; i++){
			if (fileList.get(i).getLength() == fileList.get(i+1).getLength()){
				fileList.get(i).setMD5();
				fileList.get(i+1).setMD5();
			}
		}
	}
	
	/*
	 * Sorts then iterates through the File List to find identical MD5 hashes,
	 * then returns a Vector of duplicate file pairs
	 */
	public Vector<Vector<String>> generateCopyTable(File folder, int depth){
		Vector<Vector<String>> table = new Vector<Vector<String>>();
		
		fileList.clear();
		fillFileTree(folder, depth);
		sortByLength();
		sortByHash();
		
		for (int i = 0; i < fileList.size()-1; i++){
			if (fileList.get(i).getMD5().equals(fileList.get(i+1).getMD5()) 
					&& !fileList.get(i).getMD5().equals("")){
				Vector<String> v = new Vector<String>();
				v.add(fileList.get(i).getPath());
				v.add(fileList.get(i+1).getPath());
				table.add(v);
			}
		}
		return table;
	}
}
