package fm.smart.r1;

import java.util.Vector;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import fm.smart.Result;
import fm.smart.Utils;

public abstract class GoalDownload extends Thread {
	final Bitmap mIcon2;
	Activity context;
	ProgressDialog progress_dialog;
	private String query_string;

	public GoalDownload(Activity context, ProgressDialog progress_dialog,
			String query_string) {
		this.context = context;
		this.progress_dialog = progress_dialog;
		this.query_string = query_string;
		mIcon2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_goal_icon);
	}

	public abstract Result downloadCall() throws Exception;

	public void run() {
		JSONObject json = null;
		String message = "Please try again later";
		try {
			if (!this.isInterrupted()) {
				Result result = null;
				if (Utils.isEmpty(query_string)) {
					result = downloadCall();
				} else {
					result = Main.lookup.searchGoals(Main.transport,
							query_string);
				}
				json = new JSONObject(result.http_response);
				GoalsList.items = json.getJSONArray("goals");

				JSONObject goal = null;
				JSONObject author = null;
				String icon = null;
				GoalsList.icons = new Vector<Bitmap>();
				for (int i = 0; i < GoalsList.items.length(); i++) {
					goal = GoalsList.items.getJSONObject(i);
					author = goal.getJSONObject("author");
					try {
						icon = author.getString("icon");
					} catch (Exception e) {
						e.printStackTrace();
					}
					GoalsList.icons.addElement(MediaUtility.getRemoteImage(
							icon, mIcon2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		final String dialog_message = message;
		progress_dialog.dismiss();
		if (GoalsList.items == null) {
			// some sort of failure
			Main.showErrorDialog(dialog_message, context);
		} else if (!this.isInterrupted()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, GoalsList.class.getName());
			context.startActivity(intent);

			// context.startActivity(new Intent(Intent.ACTION_VIEW,
			// SmartFm.Lists.CONTENT_URI));
		}
	}

}
