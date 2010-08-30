package com.neurogrid.smartfm.results;

import android.text.TextUtils;
import fm.smart.Result;

public class AddImageResult extends Result {

	public AddImageResult(int status_code, String http_response) {
		super(status_code,http_response);
	}

	public AddImageResult(Result result) {
		super(result.status_code,result.http_response);
	}

	public String getTitle() {
		return "Add Image Result";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Added Image To Sentence";
		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	private String prettifyResponse() {
		String message = this.status_code + ", " + this.http_response;
		if (TextUtils
				.equals(this.http_response, "No access to modify Sentence.")) {
			message = "Apologies, at the moment images can only be added to sentences you have created";
		}
		return message;
	}

	public boolean success() {
		return this.status_code == 201;
	}

}
