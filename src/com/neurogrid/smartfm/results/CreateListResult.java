package com.neurogrid.smartfm.results;

import fm.smart.Result;

public class CreateListResult extends Result{

	public CreateListResult(int status_code, String http_response) {
		super(status_code, http_response);
	}

	public String getTitle() {
		return "Create List Result";
	}
	
	public boolean success(){
		return (super.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if(this.success()){
		  message = "Successfully Created List";
		}
		else{
	      message = "Failed: "+ getHttpResponse();
		}	
		return message;
	}

	public String getHttpResponse() {
		return super.http_response;
	}

}
