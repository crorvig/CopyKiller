import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;

import org.apache.commons.codec.digest.DigestUtils;


public class FileInfo implements Comparable<FileInfo>{
	public File file;
	public String md5hash;
	public long length;
	
	public FileInfo(File file){
		this.file = file;
		length = file.length();
		md5hash = "";
		//if (file.isFile()) setMD5();
	}
	
	public File getFile(){
		return file;
	}
	
	public String getPath(){
		return file.toString();
	}
	
	public long getLength(){
		return length;
	}
	public String getMD5(){
		return md5hash;
	}
	
	public void setMD5(){
		try {
			FileInputStream fileStream = new FileInputStream(file);
			//md5hash = DigestUtils.md5Hex(Files.readAllBytes(file.toPath()));
			md5hash = DigestUtils.md5Hex(fileStream);
			fileStream.close();
			}
		catch (IOException e){
				e.printStackTrace();
		}
	}
	
	public int compareTo(FileInfo fileInfo2) {
		if (length > fileInfo2.getLength()) return 1;
		else if (length < fileInfo2.getLength()) return -1;
		else return 0;
	}
	
	public static Comparator<FileInfo> FileMD5Comparator = new Comparator<FileInfo>(){
		public int compare(FileInfo fileInfo, FileInfo fileInfo2){
			return fileInfo.getMD5().compareTo(fileInfo2.getMD5());
		}
	};
}
