package com.example;

import java.io.Serializable;

public class User implements Serializable{
	public static final long serialVersionUID = 1L;
	
	public String UserID;
	public String Name;
	public String Email;
	public String Password;
	public boolean Admin;
	
	public User(String UserID, String Name, String Email, String Password, boolean Admin ) {	
		this.UserID = UserID;
		this.Name = Name;
		this.Email = Email;
		this.Password = Password;
		this.Admin = Admin;
	}

}

