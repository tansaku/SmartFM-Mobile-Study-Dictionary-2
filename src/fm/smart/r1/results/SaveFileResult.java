package fm.smart.r1.results;

import java.io.FileDescriptor;

import fm.smart.Result;

public class SaveFileResult extends Result{
	FileDescriptor fd = null;

	public SaveFileResult(int status_code, String http_response,
			FileDescriptor fd) {
		super(status_code, http_response);
		this.fd = fd;
	}

	public String getTitle() {
		return "Download";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Downloaded Sound";// + http_response;
		} else if (super.status_code == 404) {
			message = "Failed: file not found";
		} else {
			message = "Failed:" + super.status_code;
		}
		return message;
	}

	public boolean success() {
		return super.status_code == 200;
	}

	public FileDescriptor getFileDescriptor() {
		return fd;
	}

}
