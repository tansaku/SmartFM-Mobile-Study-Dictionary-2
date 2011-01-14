package fm.smart.r1.results;

import android.text.TextUtils;
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
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	private String prettifyResponse() {
		if (usernameTaken())
			return "Apologies, that username is already taken";
		if (emailTaken())
			return "Apologies, that email address already has an account associated with it";
		if (usernameAndEmailTaken())
			return "Apologies, both username and email are already being used";
		return this.http_response;
	}

	public boolean success() {
		return super.status_code == 201;
	}

	public boolean usernameAndEmailTaken() {
		return TextUtils
				.equals(this.http_response,
						"{\"error\":{\"message\":\"Username error-username-already-taken, Email error-email-already-taken\",\"code\":400}}");
	}

	public boolean usernameTaken() {
		return TextUtils
				.equals(this.http_response,
						"{\"error\":{\"message\":\"Username error-username-already-taken\",\"code\":400}}");
	}

	public boolean emailTaken() {
		return TextUtils
				.equals(this.http_response,
						"{\"error\":{\"message\":\"Email error-email-already-taken\",\"code\":400}}");
	}

}
