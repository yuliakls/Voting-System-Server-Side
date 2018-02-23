package com.example;

import java.io.Serializable;

public class Result implements Serializable{
	public static final long serialVersionUID = 1L;

	public String Name;
	public String Amount;

	public Result( String Name, String Amount ) {	
		this.Name = Name;
		this.Amount = Amount;
	}

}
