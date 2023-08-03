package com.wijaksana.wfileutils;

/**
 * @author ant
 *
 */
public class ServerDirectoryInfo extends DirectoryInfo {
	private String server;
	private int port;
	private String username = null;
	private String password = null;
	private String protocol;
	private String serverType;
	
	public ServerDirectoryInfo() {
		super();
	}

	public ServerDirectoryInfo(String path) {
		super(path);
	}

	public ServerDirectoryInfo(TYPES type, String path) {
		super(type, path);
	}

	public ServerDirectoryInfo(TYPES type) {
		super(type);
	}

	public ServerDirectoryInfo(TYPES type, String path, String server, int port, 
			String username, String password) {
		super(type, path);
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public ServerDirectoryInfo(TYPES type, String path, String server, int port, 
			String username, String password, String protocol, String serverType) {
		super(type, path);
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
		this.protocol = protocol;
		this.serverType = serverType;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean isSameEnvi(ServerDirectoryInfo directoryInfo) throws Exception {
		if (server.equals(directoryInfo.getServer()) && port == directoryInfo.getPort()
				&& username.equals(directoryInfo.getUsername()) && password.equals(directoryInfo.getPassword())
				&& (protocol == null || protocol.equals(directoryInfo.getProtocol())) 
				&& (serverType == null || serverType.equals(directoryInfo.getServerType()))) {
			return true;
		}
		
		return false;
	}
}
