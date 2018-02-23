package com.example;

import java.io.Serializable;

public class Candidate implements Serializable{
	public static final long serialVersionUID = 1L;
	
	public int VoteNum;
	public int CandidateID;
	public String CandidateName;

	
	public Candidate(int VoteNum, int CandidateID, String CandidateName) {	
		this.VoteNum = VoteNum;
		this.CandidateID = CandidateID;
		this.CandidateName = CandidateName;
	}



}
