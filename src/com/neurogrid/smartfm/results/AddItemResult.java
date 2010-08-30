package com.neurogrid.smartfm.results;

import android.text.TextUtils;
import fm.smart.Result;

public class AddItemResult extends Result {

	public AddItemResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public AddItemResult(Result result) {
		super(result.status_code, result.http_response);
	}

	public String getTitle() {
		return "Add Item Result";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Added Item To Goal"; // success produces
															// empty body at
															// present, leaving
															// for debug
															// purposes
		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public boolean alreadyInList() {
		return TextUtils.equals(this.http_response, "item-already-in-list");
	}

	private String prettifyResponse() {
		if (alreadyInList())
			return "Item already in list";
		return this.http_response;
	}

}
