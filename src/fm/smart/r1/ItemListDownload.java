package fm.smart.r1;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import fm.smart.Result;
import fm.smart.Utils;

public class ItemListDownload extends Thread {
	Activity context;
	ProgressDialog progress_dialog;
	String query;
	int page;

	public ItemListDownload(Activity context, ProgressDialog progress_dialog,
			String query, int page) {
		this.context = context;
		this.progress_dialog = progress_dialog;
		this.query = query;
		this.page = page;
	}

	public void run() {
		JSONObject json = null;
		String message = "Please try again later";
		try {
			if (!this.isInterrupted()) {
				Result result = Main.lookup.searchItems(Main.transport, query,
						page, Main.search_lang, Main.result_lang);
				if (!TextUtils.isEmpty(result.http_response)) {
					json = new JSONObject(result.http_response);
					ItemListActivity.items = json.getJSONArray("items");
					ItemListActivity.number_results = json
							.getInt("totalResults");
					ItemListActivity.start_index = json.getInt("startIndex");
					ItemListActivity.items_per_page = json
							.getInt("itemsPerPage");
					ItemListActivity.query_string = query;
					ItemListActivity.cue_language = Utils.INV_LANGUAGE_MAP
							.get(ItemListActivity.items.getJSONObject(0)
									.getJSONObject("cue")
									.getJSONObject("related")
									.getString("language"));
					ItemListActivity.response_language = Utils.INV_LANGUAGE_MAP
							.get(ItemListActivity.items.getJSONObject(0)
									.getJSONObject("response")
									.getJSONObject("related")
									.getString("language"));
				} else {
					message = "HTTP response empty, UnknownHostException?";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		final String dialog_message = message;
		progress_dialog.dismiss();

		if (json == null || ItemListActivity.items == null) {
			// some sort of failure
			Main.showErrorDialog(dialog_message, context);
		} else if (!this.isInterrupted()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, ItemListActivity.class.getName());
			context.startActivity(intent);
		}
	}

}
