package com.wijaksana.wfileutils;

import java.util.List;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {
	private static DirectoryInfo localDir;
	private static ServerDirectoryInfo ftpDir;
	private static ServerDirectoryInfo ftpsDir;
	private static ServerDirectoryInfo sftpDir;
	private static String filenameSrc = "banner.txt";
	private static String filenameDst = "banner_new.txt";
	private static String filenameNotExist = "not_exist.txt";

	@BeforeAll
	public static void setUpBeforeAll() throws Exception {
		localDir = new DirectoryInfo(DirectoryInfo.TYPES.LOCAL, "/home/ant/temp/local");
		ftpDir = new ServerDirectoryInfo(DirectoryInfo.TYPES.FTP, "/temp/ftp", "localhost", 21, "username"
				, "password");
		ftpsDir = new ServerDirectoryInfo(DirectoryInfo.TYPES.FTPS, "/temp/ftps", "localhost", 990, "username"
				, "password", "TLS", FTPClientConfig.SYST_UNIX);
		sftpDir = new ServerDirectoryInfo(DirectoryInfo.TYPES.SFTP, "/home/ant/temp/sftp", "localhost", 22, "username"
				, "password");
	}

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	@DisplayName("List File")
	@Disabled
	public void listFile() throws Exception {
		List<FileInfo> listFiles = FileUtils.listFiles(localDir);
		for (FileInfo fileInfo : listFiles) {
			System.out.println(fileInfo.getName() + " : " + fileInfo.getType() + " : " +  fileInfo.getSize());
		}
		
		listFiles = FileUtils.listFiles(ftpDir);
		for (FileInfo fileInfo : listFiles) {
			System.out.println(fileInfo.getName() + " : " + fileInfo.getType() + " : " +  fileInfo.getSize());
		}
		
		listFiles = FileUtils.listFiles(ftpsDir);
		for (FileInfo fileInfo : listFiles) {
			System.out.println(fileInfo.getName() + " : " + fileInfo.getType() + " : " +  fileInfo.getSize());
		}
		
		listFiles = FileUtils.listFiles(sftpDir);
		for (FileInfo fileInfo : listFiles) {
			System.out.println(fileInfo.getName() + " : " + fileInfo.getType() + " : " +  fileInfo.getSize());
		}
	}
	
	@Test
	@DisplayName("Exist File")
	@Disabled
	public void existFile() throws Exception {
		System.out.println(filenameSrc + " : " + FileUtils.isFileExist(localDir, filenameSrc));
		System.out.println(filenameNotExist + " : " + FileUtils.isFileExist(localDir, filenameNotExist));
		
		System.out.println(filenameSrc + " : " + FileUtils.isFileExist(ftpDir, filenameSrc));
		System.out.println(filenameNotExist + " : " + FileUtils.isFileExist(ftpDir, filenameNotExist));
		
		System.out.println(filenameSrc + " : " + FileUtils.isFileExist(ftpsDir, filenameSrc));
		System.out.println(filenameNotExist + " : " + FileUtils.isFileExist(ftpsDir, filenameNotExist));
		
		System.out.println(filenameSrc + " : " + FileUtils.isFileExist(sftpDir, filenameSrc));
		System.out.println(filenameNotExist + " : " + FileUtils.isFileExist(sftpDir, filenameNotExist));
				
	}
	
	@Test
	@DisplayName("DELETE File")
	@Disabled
	public void deleteFile() throws Exception {
		FileUtils.deleteFile(localDir, filenameDst);
		FileUtils.deleteFile(ftpDir, filenameDst);
		FileUtils.deleteFile(ftpsDir, filenameDst);
		FileUtils.deleteFile(sftpDir, filenameDst);
	}
	
	@Test()
	@DisplayName("COPY File")
	@Disabled
	public void copyFile() throws Exception {
		FileUtils.copyFile(localDir, filenameSrc, localDir, filenameDst);
		
		ServerDirectoryInfo ftpDirClone = (ServerDirectoryInfo) ftpDir.clone();
		ftpDirClone.setPath(ftpDirClone.getPath() + "/temp");
		FileUtils.copyFile(ftpDir, filenameSrc, ftpDirClone, filenameDst);
		
		ServerDirectoryInfo ftpsDirClone = (ServerDirectoryInfo) ftpsDir.clone();
		ftpsDirClone.setPath(ftpsDirClone.getPath() + "/temp");
		
		FileUtils.copyFile(localDir, filenameSrc, ftpDirClone, filenameDst);
		FileUtils.copyFile(ftpDirClone, filenameDst, ftpsDirClone, filenameDst);
		
		FileUtils.copyFile(sftpDir, filenameSrc, ftpsDir, filenameDst);
		FileUtils.copyFile(sftpDir, filenameSrc, sftpDir, filenameDst);
	}
	
	@Test
	@DisplayName("DELETE Folder")
	@Disabled
	public void deleteFolder() throws Exception {
		FileUtils.deleteFolder(localDir, "temp");
		FileUtils.deleteFolder(ftpDir, "temp");
		FileUtils.deleteFolder(sftpDir, "temp2");
	}
	
	@Test
	@DisplayName("EXIST Folder")
	@Disabled
	public void existFolder() throws Exception {
		System.out.println(FileUtils.isFolderExist(localDir, "temp"));
		System.out.println(FileUtils.isFolderExist(ftpDir, "temp"));
		System.out.println(FileUtils.isFolderExist(sftpDir, "temp"));
	}

	@Test()
	@DisplayName("COPY Folder")
	@Disabled
	public void copyFolder() throws Exception {
		FileUtils.copyFile(localDir, filenameSrc, localDir, filenameDst);
		
		ServerDirectoryInfo ftpDirClone = (ServerDirectoryInfo) ftpDir.clone();
		ftpDirClone.setPath(ftpDirClone.getPath() + "/temp");
		FileUtils.copyFile(ftpDir, filenameSrc, ftpDirClone, filenameDst);
		
		ServerDirectoryInfo ftpsDirClone = (ServerDirectoryInfo) ftpsDir.clone();
		ftpsDirClone.setPath(ftpsDirClone.getPath() + "/temp");
		
		FileUtils.copyFile(localDir, filenameSrc, ftpDirClone, filenameDst);
		FileUtils.copyFile(ftpDirClone, filenameDst, ftpsDirClone, filenameDst);
		
		FileUtils.copyFile(sftpDir, filenameSrc, ftpsDir, filenameDst);
		FileUtils.copyFolder(sftpDir, "temp", sftpDir, "temp2");
	}
}
