package org.yawlfoundation.yawl.editor.ui.specification;

public class GitParas {

	private static String uri="https://github.com/hhluci/yawl.git";
	private static String name="hhluci";
	private static String pwd="Trudy258369";
	private static String localPath="D:/repo";
	
	public static String getLocalPath() {
		return localPath;
	}
	public static void setLocalPath(String localPath) {
		GitParas.localPath = localPath;
	}
	public static String getUri() {
		return uri;
	}
	public  static void setUri(String uri) {
		GitParas.uri = uri;
	}
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		GitParas.name = name;
	}
	public static String getPwd() {
		return pwd;
	}
	public static void setPwd(String pwd) {
		GitParas.pwd = pwd;
	}
	public static boolean isEmpty() {
		boolean isFlag = false;
		if(uri==null || uri.trim().length()==0 || name==null || name.trim().length()==0
				|| pwd==null || pwd.length()==0) {
			isFlag = true;
		}
		return isFlag;
	}
}
