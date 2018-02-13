package com.cpm.kelloggstacticalnew.getterSetter;

import java.util.ArrayList;

public class NonWorkingReasonGetterSetter {
	
	String nonworking_table;
	String informto;
	String username;
	String visitdate;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVisitdate() {
		return visitdate;
	}

	public void setVisitdate(String visitdate) {
		this.visitdate = visitdate;
	}

	ArrayList<String> reason_cd=new ArrayList<String>();
	ArrayList<String> reason=new ArrayList<String>();
	ArrayList<String> entry_allow=new ArrayList<>();
	ArrayList<String> IMAGE_ALLOW =new ArrayList<>();

	public ArrayList<String> getFOR_STORE() {
		return FOR_STORE;
	}

	public void setFOR_STORE(String FOR_STORE) {
		this.FOR_STORE.add(FOR_STORE);
	}

	public ArrayList<String> getFOR_ATT() {
		return FOR_ATT;
	}

	public void setFOR_ATT(String FOR_ATT) {
		this.FOR_ATT.add(FOR_ATT);
	}

	ArrayList<String> FOR_STORE =new ArrayList<>();
	ArrayList<String> FOR_ATT =new ArrayList<>();

	public ArrayList<String> getATTENDANCE_STATUS() {
		return ATTENDANCE_STATUS;
	}

	public void setATTENDANCE_STATUS(String ATTENDANCE_STATUS) {
		this.ATTENDANCE_STATUS.add(ATTENDANCE_STATUS);
	}

	ArrayList<String> ATTENDANCE_STATUS =new ArrayList<>();


	public String getInformto() {
		return informto;
	}

	public void setInformto(String informto) {
		this.informto = informto;
	}




	public ArrayList<String> getIMAGE_ALLOW() {
		return IMAGE_ALLOW;
	}

	public void setIMAGE_ALLOW(String IMAGE_ALLOW) {
		this.IMAGE_ALLOW.add(IMAGE_ALLOW);
	}


	public String getNonworking_table() {
		return nonworking_table;
	}
	public void setNonworking_table(String nonworking_table) {
		this.nonworking_table = nonworking_table;
	}
	public ArrayList<String> getReason_cd() {
		return reason_cd;
	}
	public void setReason_cd(String reason_cd) {
		this.reason_cd.add(reason_cd);
	}
	public ArrayList<String> getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason.add(reason);
	}

	public ArrayList<String> getEntry_allow() {
		return entry_allow;
	}

	public void setEntry_allow(String entry_allow) {
		this.entry_allow.add(entry_allow);
	}
}
