package com.wijaksana.wfileutils.local;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.wijaksana.wfileutils.DirectoryInfo;
import com.wijaksana.wfileutils.FileInfo;
import com.wijaksana.wfileutils.IDirectoryInfo;
import com.wijaksana.wfileutils.IFileUtils;

/**
 * @author ant
 *
 */
public class LocalFileUtils implements IFileUtils {
	private final DirectoryInfo directoryInfo;
		
	public LocalFileUtils(IDirectoryInfo directoryInfo) throws Exception {
		if (directoryInfo instanceof DirectoryInfo) {
			this.directoryInfo = (DirectoryInfo) directoryInfo;
		} else {
			throw new Exception("LocalFileUtils: Missing type directoryInfo");
		}
	}

	public List<FileInfo> listFiles() throws Exception {
		List<FileInfo> files = new ArrayList<FileInfo>();
		
		File file = new File(directoryInfo.getPath());
		if (file.exists() && file.isDirectory()) {
			File[] sFiles = file.listFiles();
			for (File tFile : sFiles) {
                if (tFile.isFile()) {
                    files.add(new FileInfo(tFile.getName(), tFile.length(), FileInfo.TYPES.FILE));
                } else {
                    files.add(new FileInfo(tFile.getName(), 0, FileInfo.TYPES.FOLDER));
                }
            }
		}
		
		return files;
	}

	public boolean isFileExist(String filename) throws Exception {
		File file = new File(directoryInfo.getPath() + File.separator + filename);
		if (file.exists() && file.isFile()) {
			return true;
		}
		
		return false;
	}

	public ByteArrayOutputStream retrieveStreamFile(String filename) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		File file = new File(directoryInfo.getPath() + File.separator + filename);
		if (file.exists() && file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
                                    
            IOUtils.copy(fis, byteArrayOutputStream);
            
            fis.close();
            byteArrayOutputStream.close();
            
		} else {
			throw new Exception("LocalFileUtils.retrieveStreamFile: "+ filename + " not found.");
		}
		
		return byteArrayOutputStream;
	}

	public boolean storeStreamFile(InputStream inputStream, String filename) throws Exception {
		if (inputStream == null)
			throw new Exception("LocalFileUtils.storeStreamFile: inputStream is null.");
				        
        File dir = new File(directoryInfo.getPath());
        if (!dir.exists()) {
            dir.mkdir();
        } else if (dir.exists() && dir.isFile()) {
        	throw new Exception("LocalFileUtils.storeStreamFile: path is file not folder.");
        }
        
        File file = new File(directoryInfo.getPath() + File.separator + filename);
        OutputStream outStream = new FileOutputStream(file);
                
        IOUtils.copy(inputStream, outStream);
        
        outStream.close();
        
		return true;
	}

	public boolean deleteFile(String filename) throws Exception {
		File file = new File(directoryInfo.getPath() + File.separator + filename);
		if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
        	throw new Exception("LocalFileUtils.deleteFile: filename not found.");
        }
	}

	public boolean isFolderExist(String folderName) throws Exception {
		File dir = new File(directoryInfo.getPath() + File.separator + folderName);
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}
		
		return false;
	}

	public boolean createFolder(String folderName) throws Exception {
		File dir = new File(directoryInfo.getPath() + File.separator + folderName);
		if (!dir.exists()) {
			return dir.mkdir();
		} else {
        	throw new Exception("LocalFileUtils.createFolder: folderName already exist.");
        }
	}

	public boolean deleteFolder(String folderName) throws Exception {
		File dir = new File(directoryInfo.getPath() + File.separator + folderName);
		if (dir.exists() && dir.isDirectory()) {
			return dir.delete();
		} else {
        	throw new Exception("LocalFileUtils.deleteFolder: folderName not found.");
        }
	}

	public IDirectoryInfo createClone(String path) throws Exception {
		DirectoryInfo newDirectoryInfo = (DirectoryInfo) directoryInfo.clone();
		newDirectoryInfo.setPath(newDirectoryInfo.getPath() + "/" + path);
		return newDirectoryInfo;
	}
	
	public boolean isSameEnvi(IDirectoryInfo iDirectoryInfo) throws Exception {
		if (DirectoryInfo.TYPES.LOCAL.equals(iDirectoryInfo.getType())) {
			return true;
		}
		
		return false;
	}
	
	public boolean copyFileSameEnvi(String filenameSrc, IDirectoryInfo iDirectoryInfo, String filenameDest) throws Exception {
		File file = new File(directoryInfo.getPath() + File.separator + filenameSrc);
		File dstFile = new File(((DirectoryInfo) iDirectoryInfo).getPath() + File.separator + filenameDest);
		if (file.exists() && file.isFile()) {
			Files.copy(file.toPath(), dstFile.toPath());
            return dstFile.exists();
        } else {
        	throw new Exception("LocalFileUtils.deleteFile: filenameSrc not found.");
        }
	}

}
