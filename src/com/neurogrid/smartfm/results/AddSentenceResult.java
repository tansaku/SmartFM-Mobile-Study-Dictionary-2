package com.neurogrid.smartfm.results;

import fm.smart.Result;

public class AddSentenceResult extends Result {

	public AddSentenceResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public AddSentenceResult(Result result) {
		super(result.status_code, result.http_response);
	}

	public String getTitle() {
		return "Add Sentence Result";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Added Sentence To List";
		} else {
			message = "Failed: " + this.status_code + ", " + this.http_response;
		}
		return message;
	}

	public boolean success() {
		return (this.status_code == 201);
	}

}
