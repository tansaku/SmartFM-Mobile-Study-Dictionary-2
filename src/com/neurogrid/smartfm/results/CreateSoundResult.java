package com.neurogrid.smartfm.results;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import fm.smart.Result;

public class CreateSoundResult extends Result {
	String location = "";
	public String sound_id = null;

	public CreateSoundResult(int status_code, String http_response,
			String location) {
		super(status_code, http_response);
		this.location = location;
		try {
			JSONObject json;
			json = new JSONObject(super.http_response);
			sound_id = json.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CreateSoundResult(Result result, String location) {
		super(result.status_code, result.http_response);
		this.location = location;
		try {
			JSONObject json;
			json = new JSONObject(super.http_response);
			sound_id = json.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return "Create Sound Result";
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if (this.success()) {
			message = "Successfully Created Sound";// + location;

		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	private String prettifyResponse() {
		String message = this.status_code + ", " + this.http_response;
		if (TextUtils.equals(this.http_response, "No access to modify Item.")) {
			message = "Apologies, at the moment sound can only be added to items you have created yourself.";
		}
		if (TextUtils.equals(this.http_response,
				"No access to modify Sentence.")) {
			message = "Apologies, at the moment sound can only be added to sentences you have created yourself.";
		}
		return message;
	}

}
