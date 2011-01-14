package fm.smart.r1;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import fm.smart.Result;
import fm.smart.r1.provider.SearchSuggestionSampleProvider;

public class SmartFmMenus {

	protected static final int MY_STUDY_GOALS_ID = Menu.FIRST;
	protected static final int MY_CREATED_GOALS_ID = Menu.FIRST + 1;
	protected static final int RECENT_GOALS_ID = Menu.FIRST + 2;
	protected static final int SEARCH_GOALS_ID = Menu.FIRST + 3;
	protected static final int SEARCH_ITEMS_ID = Menu.FIRST + 4;
	protected static final int SETTINGS_ID = Menu.FIRST + 5;
	protected static final int HELP_ID = Menu.FIRST + 6;
	protected static final int ABOUT_ID = Menu.FIRST + 7;
	protected static final int LOGIN = Menu.FIRST + 8;
	protected static final int LOGOUT = Menu.FIRST + 9;
	private static final String SEARCHTYPE = "search type";
	private static final String GOAL = "goal";
	private static final String ITEM = "item";

	private Activity activity;

	final DialogInterface.OnClickListener mAboutListener = new DialogInterface.OnClickListener() {
		public void onClick(android.content.DialogInterface dialogInterface,
				int i) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity
					.getString(R.string.smartfm_url)));
			activity.startActivity(intent);
		}
	};

	public SmartFmMenus(Activity activity) {
		this.activity = activity;
	}

	public boolean onOptionsItemSelected(MenuItem item, Activity activity) {
		switch (item.getItemId()) {
		case MY_STUDY_GOALS_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if (LoginActivity.isNotLoggedIn(activity)) {
				intent.setClassName(activity, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				LoginActivity.return_to = Main.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("goal_type", "my_study_goals");
			} else {
				intent.setClassName(activity, Main.class.getName());
				AndroidUtils.putExtra(intent, "goal_type", "my_study_goals");
			}
			activity.startActivity(intent);
			break;
		}
		case MY_CREATED_GOALS_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			if (LoginActivity.isNotLoggedIn(activity)) {
				intent.setClassName(activity, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				LoginActivity.return_to = Main.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("goal_type", "my_created_goals");
			} else {
				intent.setClassName(activity, Main.class.getName());
				AndroidUtils.putExtra(intent, "goal_type", "my_created_goals");
			}
			activity.startActivity(intent);
			break;
		}
		case RECENT_GOALS_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			AndroidUtils.putExtra(intent, "goal_type", "recent_goals");
			intent.setClassName(activity, Main.class.getName());
			activity.startActivity(intent);
			break;
		}
		case SEARCH_GOALS_ID: {
			Bundle bundle = new Bundle();
			bundle.putString(SEARCHTYPE, GOAL);
			activity.startSearch(null, false, bundle, false);
			break;
		}
		case SEARCH_ITEMS_ID: {
			try {
				((Main) activity).updateSpinner();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Bundle bundle = new Bundle();
			bundle.putString(SEARCHTYPE, ITEM);
			activity.startSearch(null, false, bundle, false);
			break;
		}
		case SETTINGS_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(activity, PreferenceActivity.class.getName());
			activity.startActivity(intent);
			break;
		}
		case HELP_ID: {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.title_help);
			builder.setMessage(R.string.msg_help);
			builder.setPositiveButton(R.string.button_ok, null);
			builder.show();
			break;
		}
		case ABOUT_ID: {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.title_about);
			builder.setMessage(activity.getString(R.string.msg_about) + "\n\n"
					+ activity.getString(R.string.smartfm_url));
			// builder.setIcon(R.drawable.smartfm_logo);
			builder.setPositiveButton(R.string.button_open_browser,
					mAboutListener);
			builder.setNegativeButton(R.string.button_cancel, null);
			builder.show();
			break;
		}
		case LOGIN: {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(activity, LoginActivity.class.getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			LoginActivity.return_to = Main.class.getName();
			activity.startActivity(intent);

			break;
		}
		case LOGOUT: {
			SharedPreferences.Editor editor = activity.getSharedPreferences(
					Main.PREF_REF, 0).edit();
			editor.putString("username", null);
			editor.putString("password", null);
			editor.commit();
			Main.default_study_goal_id = null;

			break;
		}
		}
		return true;

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		if (LoginActivity.isNotLoggedIn(activity)) {
			Log.d("DEBUG-MENU", "not logged in");
			menu.add(0, LOGIN, 0, R.string.menu_login).setIcon(
					android.R.drawable.ic_menu_call);
		} else {
			Log.d("DEBUG-MENU", "logged in");
			menu.add(0, LOGOUT, 0, R.string.menu_logout).setIcon(
					android.R.drawable.ic_menu_close_clear_cancel);
		}

		menu.add(0, SEARCH_ITEMS_ID, 0, R.string.menu_search_items).setIcon(
				android.R.drawable.ic_menu_search);
		menu.add(0, MY_CREATED_GOALS_ID, 0, R.string.menu_my_created_goals)
				.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, HELP_ID, 0, R.string.menu_help).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(0, MY_STUDY_GOALS_ID, 0, R.string.menu_my_study_goals)
				.setIcon(android.R.drawable.ic_menu_myplaces);
		menu.add(0, RECENT_GOALS_ID, 0, R.string.menu_recent_goals).setIcon(
				android.R.drawable.ic_menu_today);
		menu.add(0, SEARCH_GOALS_ID, 0, R.string.menu_search_goals).setIcon(
				android.R.drawable.ic_menu_search);
		// menu.add(0, SETTINGS_ID, 0, R.string.menu_settings).setIcon(
		// android.R.drawable.ic_menu_preferences);
		return true;
	}

	public void doSearchQuery(final Intent queryIntent,
			final String entryPoint, Activity activity) {
		// Report the method by which we were called.
		Log.d("DEBUG", entryPoint);
		// The search query is provided as an "extra" string in the query intent
		final String queryString = queryIntent
				.getStringExtra(SearchManager.QUERY);

		// Record the query string in the recent queries suggestions provider.
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
				activity, SearchSuggestionSampleProvider.AUTHORITY,
				SearchSuggestionSampleProvider.MODE);
		suggestions.saveRecentQuery(queryString, null);

		final Bundle appData = queryIntent
				.getBundleExtra(SearchManager.APP_DATA);
		if (appData == null) {
			Log.d("DEBUG", "<no app data bundle>");
		}
		if (appData.getString(SEARCHTYPE).equals(GOAL)) {
			ProgressDialog myProgressDialog = ProgressDialog.show(activity,
					"Please wait...", "Searching goals for " + queryString
							+ " ...", true, true);
			GoalDownload goal_download = new GoalDownload(activity,
					myProgressDialog, queryString) {

				@Override
				public Result downloadCall() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			goal_download.start();
		} else if (appData.getString(SEARCHTYPE).equals(ITEM)) {
			loadItems(activity, queryString, 1);
		} else {
			Log.e("ERROR",
					"unknown SEARCHTYPE: " + appData.getString(SEARCHTYPE));
		}

	}

	public static void loadItems(Activity activity, final String queryString,
			final int page) {
		ProgressDialog myProgressDialog = new ProgressDialog(activity);
		myProgressDialog.setTitle("Please Wait ...");
		String message = "Searching items for " + queryString + " ...";
		if (page > 1) {
			message = "Loading page " + Integer.toString(page) + " for "
					+ queryString + " ...";
		}
		myProgressDialog.setMessage(message);
		myProgressDialog.setIndeterminate(true);
		myProgressDialog.setCancelable(true);

		final ItemListDownload item_download = new ItemListDownload(activity,
				myProgressDialog, queryString, page);
		myProgressDialog.setButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						item_download.interrupt();
					}
				});
		OnCancelListener ocl = new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				item_download.interrupt();
			}
		};
		myProgressDialog.setOnCancelListener(ocl);
		myProgressDialog.show();
		item_download.start();
	}
}