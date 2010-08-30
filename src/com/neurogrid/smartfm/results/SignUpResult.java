package com.neurogrid.smartfm.results;

import fm.smart.Result;

public class SignUpResult extends Result {

	public SignUpResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public SignUpResult(Result result) {
		super(result.status_code, result.http_response);
	}

	public String getTitle() {
		return "SignUp";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Signed Up";// + http_response;
		} else {
			message = "Failed: " + super.http_response;
		}
		return message;
	}

	public boolean success() {
		return super.status_code == 201;
	}

}
