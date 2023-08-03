package com.wijaksana.wfileutils;

/**
 * @author ant
 *
 */
public class DirectoryInfo implements IDirectoryInfo {
	public enum TYPES {LOCAL, FTP, FTPS, SFTP}
	
	private TYPES type;
	private String path;
	
	public DirectoryInfo() {
	}
	
	public DirectoryInfo(TYPES type) {
		this.type = type;
	}

	public DirectoryInfo(TYPES type, String path) {
		this.type = type;
		this.path = path;
	}

	public TYPES getType() {
		return type;
	}

	public void setType(TYPES type) {
		this.type = type;
	}

	public DirectoryInfo(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
