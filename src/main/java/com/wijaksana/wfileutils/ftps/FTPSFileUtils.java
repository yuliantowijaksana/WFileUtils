package com.wijaksana.wfileutils.ftps;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import com.wijaksana.wfileutils.DirectoryInfo;
import com.wijaksana.wfileutils.IDirectoryInfo;
import com.wijaksana.wfileutils.IFileUtils;
import com.wijaksana.wfileutils.ServerDirectoryInfo;
import com.wijaksana.wfileutils.ftp.FTPFileUtils;

/**
 * @author ant
 *
 */
public class FTPSFileUtils extends FTPFileUtils implements IFileUtils {
	private int port = 990;
	
	public FTPSFileUtils(IDirectoryInfo directoryInfo) throws Exception {
		super(directoryInfo);
	}
	
	public FTPClient connect() throws Exception {
		FTPClient ftpClient;
		if (!directoryInfo.getProtocol().isEmpty()) {
			ftpClient = new FTPSClient(directoryInfo.getProtocol());
		} else {
			ftpClient = new FTPSClient();
		}
		
		if (!directoryInfo.getServerType().isEmpty()) {
			ftpClient.configure(new FTPClientConfig(directoryInfo.getServerType()));
		} else {
			ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
		}
		
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
        ((FTPSClient) ftpClient).execPBSZ(0);
        ((FTPSClient) ftpClient).execPROT("P");
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
        	ftpClient.disconnect();
            throw new Exception("FTPFileUtils.connect: FTP server refused connection.");
        }
        
		return ftpClient;
	}
	
	public boolean isSameEnvi(IDirectoryInfo iDirectoryInfo) throws Exception {
		if (DirectoryInfo.TYPES.FTPS.equals(iDirectoryInfo.getType())) {
			return directoryInfo.isSameEnvi((ServerDirectoryInfo) iDirectoryInfo);
		}
		
		return false;
	}
	
	@Override
	public boolean copyFileSameEnvi(String filenameSrc, IDirectoryInfo iDirectoryInfo, String filenameDest)
			throws Exception {
		if (DirectoryInfo.TYPES.FTPS.equals(iDirectoryInfo.getType())) {
			return directoryInfo.isSameEnvi((ServerDirectoryInfo) iDirectoryInfo);
		}
		
		return false;
	}
	
}
