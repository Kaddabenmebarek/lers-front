package org.research.kadda.labinventory.core.model;

public class EmployeeModel {

	private String userName;
	private String password;
	private String errorLogin;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getErrorLogin() {
		return errorLogin;
	}
	public void setErrorLogin(String errorLogin) {
		this.errorLogin = errorLogin;
	}

}