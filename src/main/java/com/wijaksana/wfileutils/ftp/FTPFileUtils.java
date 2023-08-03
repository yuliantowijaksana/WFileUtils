package com.wijaksana.wfileutils.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.wijaksana.wfileutils.DirectoryInfo;
import com.wijaksana.wfileutils.FileInfo;
import com.wijaksana.wfileutils.IDirectoryInfo;
import com.wijaksana.wfileutils.IFileUtils;
import com.wijaksana.wfileutils.ServerDirectoryInfo;

/**
 * @author ant
 *
 */
public class FTPFileUtils implements IFileUtils {
	protected ServerDirectoryInfo directoryInfo;
	private int port = 21;
			
	public FTPFileUtils(IDirectoryInfo directoryInfo) throws Exception {
		if (directoryInfo instanceof ServerDirectoryInfo) {
			this.directoryInfo = (ServerDirectoryInfo) directoryInfo;
		} else {
			throw new Exception("FTPFileUtils: Missing type directoryInfo");
		}
	}

	public FTPClient connect() throws Exception {
		FTPClient ftpClient = new FTPClient();
		
		if (directoryInfo.getPort() > 0) {
            port = directoryInfo.getPort();
        }
		
		if (directoryInfo.getServer().isEmpty()) {
			throw new Exception("FTPFileUtils.connect: Server not set.");
		}
		
		ftpClient.connect(directoryInfo.getServer(), port);
        ftpClient.enterLocalPassiveMode();
        
        boolean login = ftpClient.login(directoryInfo.getUsername(), directoryInfo.getPassword());
        if (!login) {
            throw new Exception("FTPFileUtils.connect: userName= " + directoryInfo.getUsername() + ", password= " + directoryInfo.getPassword() + " can not login");
        }
        ftpClient.setBufferSize(1000);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
        	ftpClient.disconnect();
            throw new Exception("FTPFileUtils.connect: FTP server refused connection.");
        }
        
		return ftpClient;
	}
	
	public List<FileInfo> listFiles() throws Exception {
		List<FileInfo> files = new ArrayList<FileInfo>();
                
        FTPClient ftpClient = connect();
        String path = directoryInfo.getPath();
        ftpClient.changeWorkingDirectory(path);
        if (!path.equalsIgnoreCase(ftpClient.printWorkingDirectory()))
            throw new Exception(path + " not found");
        
        FTPFile[] sFiles = ftpClient.listFiles(path);
        if (sFiles.length > 0) {
            for (FTPFile fTPFile : sFiles) {
                if (fTPFile.isFile()) {
                    files.add(new FileInfo(fTPFile.getName(), fTPFile.getSize(), FileInfo.TYPES.FILE));
                } else {
                    files.add(new FileInfo(fTPFile.getName(), 0, FileInfo.TYPES.FOLDER));
                }
            }
        }
        
        closeFTPClient(ftpClient);
                
        return files;
	}

	public boolean isFileExist(String filename) throws Exception {
		boolean isExist = false;
		
		FTPClient ftpClient = connect();
        String file = directoryInfo.getPath() + "/" + filename;
        FTPFile[] files = ftpClient.listFiles(file);
        if (files.length > 0) {
            if (files[0].isFile())
            	isExist = true;
        }
        
        closeFTPClient(ftpClient);
        
        return isExist;
	}

	public ByteArrayOutputStream retrieveStreamFile(String filename) throws Exception {
		FTPClient ftpClient = connect();
        String filenameSrc = directoryInfo.getPath() + "/" + filename;
        
        InputStream inputStream = ftpClient.retrieveFileStream(filenameSrc);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);

        byteArrayOutputStream.close();
        inputStream.close();
        
        closeFTPClient(ftpClient);
        
        return byteArrayOutputStream;
	}

	public boolean storeStreamFile(InputStream inputStream, String filename) throws Exception {
		FTPClient ftpClient = connect();
        String filenameDst = directoryInfo.getPath() + "/" + filename;
        boolean success = ftpClient.storeFile(filenameDst, inputStream);
        
        closeFTPClient(ftpClient);
        
        return success;
	}

	public boolean deleteFile(String filename) throws Exception {
		FTPClient ftpClient = connect();
        String filenameDel = directoryInfo.getPath() + "/" + filename;
        boolean success = ftpClient.deleteFile(filenameDel);
        
        closeFTPClient(ftpClient);
        
		return success;
	}

	public boolean isFolderExist(String folderName) throws Exception {
		FTPClient ftpClient = connect();
        String folder = directoryInfo.getPath() + "/" + folderName;
        ftpClient.changeWorkingDirectory(folder);
        
        boolean success = false;
        int returnCode = ftpClient.getReplyCode();
        if (returnCode != 550)
            success = true;
        
        closeFTPClient(ftpClient);
        
		return success;
	}

	public boolean createFolder(String folderName) throws Exception {
		FTPClient ftpClient = connect();
        String folder = directoryInfo.getPath() + "/" + folderName;
        ftpClient.changeWorkingDirectory(folder);
        
        boolean success = false;
        int returnCode = ftpClient.getReplyCode();
        if (returnCode != 550) {
            success = true;
        } else {
            ftpClient.makeDirectory(folder);
            success = true;
        }
        
        closeFTPClient(ftpClient);
        
		return success;
	}

	public boolean deleteFolder(String folderName) throws Exception {
		FTPClient ftpClient = connect();
        String folder = directoryInfo.getPath() + "/" + folderName;
        deleteFolder(ftpClient, folder, "");
                                
        closeFTPClient(ftpClient);
        
		return true;
	}

	private void deleteFolder(FTPClient ftpClient, String parentFolder, String currFolder) throws Exception {
        String folderToList = parentFolder;
        if (!currFolder.equals("")) {
            folderToList += "/" + currFolder;
        }
        
        FTPFile[] subFiles = ftpClient.listFiles(folderToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currFilename = aFile.getName();
                if (currFilename.equals(".") || currFilename.equals("..")) {
                    continue;
                }
                
                String filePath = parentFolder + "/" + currFolder + "/" + currFilename;
                if (currFolder.equals("")) {
                    filePath = parentFolder + "/" + currFilename;
                }
                
                if (aFile.isDirectory()) {
                    deleteFolder(ftpClient, folderToList, currFilename);
                } else {
                    ftpClient.deleteFile(filePath);
                }
            }
        }
        
        ftpClient.removeDirectory(folderToList);
    }
	
	public IDirectoryInfo createClone(String path) throws Exception {
		ServerDirectoryInfo newDirectoryInfo = (ServerDirectoryInfo) directoryInfo.clone();
		newDirectoryInfo.setPath(newDirectoryInfo.getPath() + "/" + path);
		return newDirectoryInfo;
	}

	public boolean isSameEnvi(IDirectoryInfo iDirectoryInfo) throws Exception {
		if (DirectoryInfo.TYPES.FTP.equals(iDirectoryInfo.getType())) {
			return directoryInfo.isSameEnvi((ServerDirectoryInfo) iDirectoryInfo);
		}
		
		return false;
	}
	
	@Override
	public boolean copyFileSameEnvi(String filenameSrc, IDirectoryInfo iDirectoryInfo, String filenameDest)
			throws Exception {
		FTPClient ftpClient = connect();
		String src = directoryInfo.getPath() + "/" + filenameSrc;
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ftpClient.retrieveFile(src, outputStream);
        
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        String to = ((ServerDirectoryInfo) iDirectoryInfo).getPath() + "/" + filenameDest;
        boolean success = ftpClient.storeFile(to, inputStream);

        inputStream.close();
        
        closeFTPClient(ftpClient);
        
		return success;
	}
	
	public void closeFTPClient(FTPClient ftpClient) throws Exception {
		ftpClient.logout();
        ftpClient.disconnect();
	}
}
