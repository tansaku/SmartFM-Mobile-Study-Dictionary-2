package fm.smart.r1.results;

import fm.smart.Result;

public class LoginResult extends Result {

	public LoginResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public LoginResult(Result result) {
		super(result.status_code, result.http_response);
	}

	public String getTitle() {
		return "Login";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully logged in";// + http_response;
		} else {
			if (http_response.equals("Unauthorized")) {
				message = "Username or password incorrect ...";
			} else {
				message = "Failed: " + http_response;
			}
		}
		return message;
	}

	public boolean success() {
		return this.status_code == 200;
	}

}
