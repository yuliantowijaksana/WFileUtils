package com.wijaksana.wfileutils.sftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.wijaksana.wfileutils.DirectoryInfo;
import com.wijaksana.wfileutils.FileInfo;
import com.wijaksana.wfileutils.IDirectoryInfo;
import com.wijaksana.wfileutils.IFileUtils;
import com.wijaksana.wfileutils.ServerDirectoryInfo;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

/**
 * @author ant
 *
 */
public class SFTPFileUtils implements IFileUtils {
	protected ServerDirectoryInfo directoryInfo;
	private int port = 22;
	
	public SFTPFileUtils(IDirectoryInfo directoryInfo) throws Exception {
		if (directoryInfo instanceof ServerDirectoryInfo) {
			this.directoryInfo = (ServerDirectoryInfo) directoryInfo;
		} else {
			throw new Exception("SFTPFileUtils: Missing type directoryInfo");
		}
	}

	public SSHClient connect() throws Exception {
		if (directoryInfo.getPort() > 0) {
            port = directoryInfo.getPort();
        }
		
		if (directoryInfo.getServer().isEmpty()) {
			throw new Exception("SFTPFileUtils.connect: Server not set.");
		}
		
		SSHClient client = new SSHClient();
	    client.addHostKeyVerifier(new PromiscuousVerifier());
	    client.connect(directoryInfo.getServer(), port);
	    client.authPassword(directoryInfo.getUsername(), directoryInfo.getPassword());
	    
	    return client;
	}
	
	public List<FileInfo> listFiles() throws Exception {
		List<FileInfo> files = new ArrayList<FileInfo>();
		
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	 
	    String path = directoryInfo.getPath();
	    List<RemoteResourceInfo> sFiles = sftpClient.ls(path);
	    if (sFiles.size() > 0) {
            for (RemoteResourceInfo fTPFile : sFiles) {
            	if (fTPFile.isRegularFile()) {
                    files.add(new FileInfo(fTPFile.getName(), fTPFile.getAttributes().getSize(), FileInfo.TYPES.FILE));
                } else if (fTPFile.isDirectory()) {
                    files.add(new FileInfo(fTPFile.getName(), 0, FileInfo.TYPES.FOLDER));
                }
            }
	    }
	    
	    closeSFTPClient(sshClient, sftpClient);
	    
		return files;
	}

	public boolean isFileExist(String filename) throws Exception {
		boolean isExist = false;
		
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	 
	    String file = directoryInfo.getPath() + "/" + filename;
	    try {
	    	FileAttributes fileAttr = sftpClient.lstat(file);
		    
		    if (fileAttr != null)
	            if (fileAttr.getType() == FileMode.Type.REGULAR)
	            	isExist = true;
		} catch (Exception e) {
		}
	    
	    closeSFTPClient(sshClient, sftpClient);
	    
		return isExist;
	}

	public ByteArrayOutputStream retrieveStreamFile(String filename) throws Exception {
		SSHClient sshClient = connect();
		SFTPClient sftpClient = sshClient.newSFTPClient();

		String filenameSrc = directoryInfo.getPath() + "/" + filename;

		RemoteFile openFile = sftpClient.open(filenameSrc);
		InputStream inputStream = openFile.new RemoteFileInputStream();		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, byteArrayOutputStream);
		
		byteArrayOutputStream.close();
		inputStream.close();
		openFile.close();

		closeSFTPClient(sshClient, sftpClient);
		
		return byteArrayOutputStream;
	}

	public boolean storeStreamFile(InputStream inputStream, String filename) throws Exception {
		SSHClient sshClient = connect();
		SFTPClient sftpClient = sshClient.newSFTPClient();

		String filenameDst = directoryInfo.getPath() + "/" + filename;
		
		RemoteFile remoteFile = sftpClient.getSFTPEngine().open(filenameDst, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
		OutputStream outputStream = remoteFile.new RemoteFileOutputStream();
				
		IOUtils.copy(inputStream, outputStream);
		
		outputStream.close();
		remoteFile.close();
		
		return true;
	}

	public boolean deleteFile(String filename) throws Exception {
		SSHClient sshClient = connect();
		SFTPClient sftpClient = sshClient.newSFTPClient();
		String filenameDel = directoryInfo.getPath() + "/" + filename;
		
		sftpClient.rm(filenameDel);
		
		return true;
	}

	public boolean isFolderExist(String folderName) throws Exception {
		boolean isExist = false;
		
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	 
	    String folder = directoryInfo.getPath() + "/" + folderName;
	    try {
	    	FileAttributes fileAttr = sftpClient.lstat(folder);
		    
		    if (fileAttr != null)
	            if (fileAttr.getType() == FileMode.Type.DIRECTORY)
	            	isExist = true;
		} catch (Exception e) {
		}
	    
	    closeSFTPClient(sshClient, sftpClient);
	    
		return isExist;
	}

	public boolean createFolder(String folderName) throws Exception {
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	    
	    String folder = directoryInfo.getPath() + "/" + folderName;
	    sftpClient.mkdir(folder);
	    
	    closeSFTPClient(sshClient, sftpClient);
	    
		return true;
	}

	public boolean deleteFolder(String folderName) throws Exception {
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	    
	    String folder = directoryInfo.getPath() + "/" + folderName;
	    deleteFolder(sftpClient, folder, "");
	    
	    closeSFTPClient(sshClient, sftpClient);
	    
		return true;
	}
	
	private void deleteFolder(SFTPClient sftpClient, String parentFolder, String currFolder) throws Exception {
        String folderToList = parentFolder;
        if (!currFolder.equals("")) {
            folderToList += "/" + currFolder;
        }
                
        List<RemoteResourceInfo> subFiles = sftpClient.ls(folderToList);
        if (subFiles != null && subFiles.size() > 0) {
            for (RemoteResourceInfo aFile : subFiles) {
                String currFilename = aFile.getName();
                if (currFilename.equals(".") || currFilename.equals("..")) {
                    continue;
                }
                
                String filePath = parentFolder + "/" + currFolder + "/" + currFilename;
                if (currFolder.equals("")) {
                    filePath = parentFolder + "/" + currFilename;
                }
                
                if (aFile.isDirectory()) {
                    deleteFolder(sftpClient, folderToList, currFilename);
                } else if (aFile.isRegularFile()) {
                	sftpClient.rm(filePath);
                }
            }
        }
        
        sftpClient.rmdir(folderToList);
    }
	
	public IDirectoryInfo createClone(String path) throws Exception {
		ServerDirectoryInfo newDirectoryInfo = (ServerDirectoryInfo) directoryInfo.clone();
		newDirectoryInfo.setPath(newDirectoryInfo.getPath() + "/" + path);
		return newDirectoryInfo;
	}
	
	public boolean isSameEnvi(IDirectoryInfo iDirectoryInfo) throws Exception {
		if (DirectoryInfo.TYPES.SFTP.equals(iDirectoryInfo.getType())) {
			return directoryInfo.isSameEnvi((ServerDirectoryInfo) iDirectoryInfo);
		}
		
		return false;
	}
	
	@Override
	public boolean copyFileSameEnvi(String filenameSrc, IDirectoryInfo iDirectoryInfo, String filenameDest)
			throws Exception {
		SSHClient sshClient = connect();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	    
	    String src = directoryInfo.getPath() + "/" + filenameSrc;
	    String to = ((ServerDirectoryInfo) iDirectoryInfo).getPath() + "/" + filenameDest;
	    
	    RemoteFile openFile = sftpClient.open(src);
		InputStream inputStream = openFile.new RemoteFileInputStream();
	    
		RemoteFile remoteFile = sftpClient.getSFTPEngine().open(to, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
		OutputStream outputStream = remoteFile.new RemoteFileOutputStream();
				
		IOUtils.copy(inputStream, outputStream);
		
		outputStream.close();
		remoteFile.close();
		inputStream.close();
		openFile.close();
		
	    closeSFTPClient(sshClient, sftpClient);
		
	    return true;
	}
	
	public void closeSFTPClient(SSHClient sshClient, SFTPClient sftpClient) throws Exception {
		sftpClient.close();
	    sshClient.disconnect();
	}
}
