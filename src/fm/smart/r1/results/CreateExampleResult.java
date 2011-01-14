package fm.smart.r1.results;

import android.text.TextUtils;
import fm.smart.Result;

public class CreateExampleResult extends Result {

	public CreateExampleResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public CreateExampleResult(Result result) {
		super(result.status_code, result.http_response);
	}

	public String getTitle() {
		return "Create Example Result";
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if (this.success()) {
			message = "Successfully Created Example";
		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	private String prettifyResponse() {
		String message = this.status_code + ", " + this.http_response;
		if (this.http_response == null) {
			message = "Network Timeout, please try again later";
		}
		if (TextUtils.equals(this.http_response,
				"Translation translation-already-exists")) {
			message = "Apologies, this sentence already exists, possibly in association with another item.";
			// TODO kick off another request to add this sentence to this item
			// ...?
		}
		return message;
	}

}
