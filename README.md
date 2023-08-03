# WFileUtils
This is a repository for Java engines or libraries that function for file transfer or file management with the sftp, ftp, ftps &amp; local machine protocols.

### Function
This Library have 7 function:
* **listFiles** return List&lt;FileInfo&gt;

	List<FileInfo> listFiles = FileUtils.listFiles(localDir);

* **isFileExist** return boolean

	boolean isExist = FileUtils.isFileExist(localDir, filename)	

* **copyFile** return boolean

	boolean isSuccess = FileUtils.copyFile(localDir, filenameSrc, ftpDir, filenameDst);

* **deleteFile** return boolean

	boolean isDeleted = FileUtils.deleteFile(localDir, filenameDst);

* **isFolderExist** return boolean

	boolean isExist = FileUtils.isFolderExist(localDir, folderName);

* **copyFolder** return boolean

	boolean isSuccess = FileUtils.copyFolder(localDir, folderNameSrc, ftpDir, folderNameDst);

* **deleteFolder** return boolean

	boolean isDeleted = FileUtils.deleteFolder(localDir, folderName);

### Dependencies
This library use below dependency:
* **[commons-net version 3.9.0](https://commons.apache.org/proper/commons-net/)**
* **[sshj version 0.35.0](https://github.com/hierynomus/sshj)**
* **[commons-io version 2.11.0](https://commons.apache.org/proper/commons-io/)**
* **[slf4j-api version 2.0.7](http://www.slf4j.org/)**
* **[slf4j-simple version 2.0.7](http://www.slf4j.org/)**
* **[junit-jupiter-engine version 5.9.3](https://junit.org/junit5/)**

### Website
For detail article can access this url 

**[Copy file dengan java lebih mudah dengan library WFileUtils](https://wijaksana.com/2023/08/03/copy-file-dengan-java-lebih-mudah-dengan-library-wfileutils/)**
