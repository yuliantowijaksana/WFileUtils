package com.wijaksana.wfileutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.wijaksana.wfileutils.ftp.FTPFileUtils;
import com.wijaksana.wfileutils.ftps.FTPSFileUtils;
import com.wijaksana.wfileutils.local.LocalFileUtils;
import com.wijaksana.wfileutils.sftp.SFTPFileUtils;

/**
 * @author ant
 *
 */
public class FileUtils {
	private static IFileUtils getFileUtils(IDirectoryInfo directoryInfo) throws Exception {
		if (DirectoryInfo.TYPES.LOCAL.equals(directoryInfo.getType())) {
			return new LocalFileUtils(directoryInfo);
		} else if (DirectoryInfo.TYPES.FTP.equals(directoryInfo.getType())) {
			return new FTPFileUtils(directoryInfo);
		} else if (DirectoryInfo.TYPES.FTPS.equals(directoryInfo.getType())) {
			return new FTPSFileUtils(directoryInfo);
		} else if (DirectoryInfo.TYPES.SFTP.equals(directoryInfo.getType())) {
			return new SFTPFileUtils(directoryInfo);
		}
		
		throw new Exception("FileUtils.getFileUtils: Type not supported.");
	}
	
	public static List<FileInfo> listFiles(IDirectoryInfo directoryInfo) throws Exception {
		IFileUtils fileUtils = getFileUtils(directoryInfo);
				
		return fileUtils.listFiles();		
	}

	public static boolean isFileExist(IDirectoryInfo directoryInfo, String filename) throws Exception {
		IFileUtils fileUtils = getFileUtils(directoryInfo);
                
        return fileUtils.isFileExist(filename);
    }
	
	public static boolean copyFile(IDirectoryInfo directoryInfoSrc, String filenameSrc, 
			IDirectoryInfo directoryInfoDest, String filenameDest) throws Exception {
		IFileUtils fileUtilsSrc = getFileUtils(directoryInfoSrc);
		IFileUtils fileUtilsDst = getFileUtils(directoryInfoDest);
		
		if (fileUtilsSrc.isSameEnvi(directoryInfoDest)) {
			return fileUtilsSrc.copyFileSameEnvi(filenameSrc, directoryInfoDest, filenameDest);
		} else {
			ByteArrayOutputStream bos = fileUtilsSrc.retrieveStreamFile(filenameSrc);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());    
			return fileUtilsDst.storeStreamFile(bis, filenameDest);
		}
    }
	
	public static boolean deleteFile(IDirectoryInfo directoryInfo, String filename) throws Exception {
		IFileUtils fileUtils = getFileUtils(directoryInfo);
                
        return fileUtils.deleteFile(filename);
    }
	
	public static boolean isFolderExist(IDirectoryInfo directoryInfo, String folderName) throws Exception {
		IFileUtils fileUtils = getFileUtils(directoryInfo);
                
        return fileUtils.isFolderExist(folderName);
    }
	
	public static boolean copyFolder(IDirectoryInfo directoryInfoSrc, String folderNameSrc, 
			IDirectoryInfo directoryInfoDest, String folderNameDest) throws Exception {
		IFileUtils fileUtilsSrc = getFileUtils(directoryInfoSrc);
		IFileUtils fileUtilsDst = getFileUtils(directoryInfoDest);
        
		IDirectoryInfo newDirectoryInfoSrc = (IDirectoryInfo) fileUtilsSrc.createClone(folderNameSrc);
		IDirectoryInfo newDirectoryInfoDest = (IDirectoryInfo) fileUtilsDst.createClone(folderNameDest);
                
		fileUtilsDst.createFolder(folderNameDest);
		
		List<FileInfo> listFiles = listFiles(newDirectoryInfoSrc);
		for (FileInfo fileInfo : listFiles) {
            if (FileInfo.TYPES.FILE.equals(fileInfo.getType())) {
                copyFile(newDirectoryInfoSrc, fileInfo.getName(), newDirectoryInfoDest, fileInfo.getName());
            } else if (FileInfo.TYPES.FOLDER.equals(fileInfo.getType())) {
                copyFolder(newDirectoryInfoSrc, fileInfo.getName(), newDirectoryInfoDest, fileInfo.getName());
            }
        }
        
        return true;
    }
	
	public static boolean deleteFolder(IDirectoryInfo directoryInfo, String folderName) throws Exception {
		IFileUtils fileUtils = getFileUtils(directoryInfo);
        
        return fileUtils.deleteFolder(folderName);
    }
}
