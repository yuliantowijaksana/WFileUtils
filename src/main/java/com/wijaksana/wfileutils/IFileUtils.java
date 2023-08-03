package com.wijaksana.wfileutils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author ant
 *
 */
public interface IFileUtils {
	public List<FileInfo> listFiles() throws Exception;
	public boolean isFileExist(String filename) throws Exception;
	public ByteArrayOutputStream retrieveStreamFile(String filename) throws Exception;
	public boolean storeStreamFile(InputStream inputStream, String filename) throws Exception;
	public boolean deleteFile(String filename) throws Exception;
    public boolean isFolderExist(String folderName) throws Exception;
	public boolean createFolder(String folderName) throws Exception;
    public boolean deleteFolder(String folderName) throws Exception;
    public IDirectoryInfo createClone(String path) throws Exception;
    public boolean isSameEnvi(IDirectoryInfo iDirectoryInfo) throws Exception;
    public boolean copyFileSameEnvi(String filenameSrc, IDirectoryInfo iDirectoryInfo, String filenameDest) throws Exception;
}
