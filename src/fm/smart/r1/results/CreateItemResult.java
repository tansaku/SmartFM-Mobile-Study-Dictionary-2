package fm.smart.r1.results;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import fm.smart.Result;

public class CreateItemResult extends Result {

	String goal_id = null;
	public String item_id;

	public CreateItemResult(int status_code, String http_response,
			String goal_id) {
		super(status_code, http_response);
		this.goal_id = goal_id;
	}

	public CreateItemResult(Result result, String goal_id) {
		super(result.status_code, result.http_response);
		this.goal_id = goal_id;
	}

	public String getTitle() {
		return "Create Item Result";
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if (this.success()) {
			message = "Successfully Created Item";// + http_response;

			try {
				// TODO this not working yet ....
				JSONObject json = new JSONObject(this.http_response);
				this.item_id = json.getString("id");
			} catch (JSONException e) {
				message = "Failed: unable to get id from json response";
				e.printStackTrace();
			}
		} else {
			message = "Failed: " + prettifyResponse();
		}
		// if (!TextUtils.isEmpty(list_id)) {
		// message += ", for list: " + list_id;
		// }
		return message;
	}

	public boolean languagePairMismatch() {
		return TextUtils.equals(this.http_response,
				"incompatible-languages-on-item-and-list");
	}

	public boolean noAccess() {
		return TextUtils.equals(this.http_response,
				"No access to modify Course.");
		// suspect we need to handle json now
		// {"error":{"message":"Goal is not accessible to user.","code":403}}
	}

	public boolean alreadyInList() {
		return TextUtils.equals(this.http_response, "item-already-in-list");
	}

	// if not logged in {"error":{"message":"Unauthorized","code":401}}
	
	// here's a new one: {"error":{"message":"Missing either 'text' or 'image_id' attribute.","code":400}}


	private String prettifyResponse() {
		if (languagePairMismatch())
			return "Apologies, the current system only supports one language pair for saving study items.";
		if (noAccess())
			return "Apologies, it seems we do not have access rights to modify your current study goal.";
		if (alreadyInList())
			return "This item already exists and is in your study goal.";
		return this.http_response;
	}

}
