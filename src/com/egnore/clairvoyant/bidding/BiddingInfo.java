package com.egnore.clairvoyant.bidding;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.apache.http.ParseException;
import org.htmlparser.util.ParserException;

public class BiddingInfo {
	protected String id;
	protected String customerName;
	protected String projectName;
	protected Date publicDate;
	protected Date deadlineDate = null;
	
	protected String[] types;
	protected String area;
	protected String[] industries;
	protected String uri;

	public boolean equals(BiddingInfo b) {
		return projectName.equals(b.projectName) && uri.equals(b.uri);
	}
	public void dump(PrintStream ps) {
		ps.println(uri + "=>" + projectName);
	}

	public void setProjectName(String name) {
		projectName = name;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public void setURI(String s) {
		uri = s;
	}

	public Date getDeadlineDate() {
		return deadlineDate;
	}

	public String getURI() {
		return uri;
	}

	public String getContentAsString() throws ParseException, IOException, ParserException {
		return "";
	}
	
	public String getContentAsHTML() throws ParseException, IOException, ParserException {
		return "";
	}
}