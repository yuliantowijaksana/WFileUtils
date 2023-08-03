package com.wijaksana.wfileutils;

/**
 * @author ant
 *
 */
public class FileInfo {
	public enum TYPES {FILE, FOLDER}
	
	private String name;
    private long size;
    private TYPES type;
    
    public FileInfo() {
	}

	public FileInfo(String name, long size, TYPES type) {
		this.name = name;
		this.size = size;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public TYPES getType() {
		return type;
	}

	public void setType(TYPES type) {
		this.type = type;
	}
    
}
