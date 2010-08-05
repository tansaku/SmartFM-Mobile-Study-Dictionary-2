package com.neurogrid.smartfm;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.Node;

public class ItemListActivity extends ListActivity {

	private static final int CREATE_ITEM_ID = Menu.FIRST;
	private static final int LOAD_MORE = Menu.FIRST + 1;

	public static int number_results = 0;
	public static int start_index = 0;
	public static int items_per_page = 0;
	public static String query_string = "";
	public static String cue_language = "";
	public static String response_language = "";
	public static JSONArray items;
	public static MediaPlayer mMediaPlayer = null;
	private String list_id = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		this.setContentView(R.layout.item_list);
		// Display an indeterminate Progress-Dialog
		Intent intent = getIntent();

		if (intent.getAction() == null)
			intent.setAction(Intent.ACTION_VIEW);

		Uri uri = intent.getData();
		try {
			list_id = uri.getPathSegments().get(1);
		} catch (Exception e) {
			// no id if we have performed a query search - could append search
			// term?
		}
		Log.d("ItemListActivity", "The current instance list_id is " + list_id);
		if (items != null && items.length() > 0) {
			this.setListAdapter(new EfficientAdapter(this, items));
		} else {
			// here could take user directly to item add screen, but should tell
			// them what we are doing.
			// offer a dialogue?

			// TODO remove for the moment
			/*
			 * AlertDialog dialog = new AlertDialog.Builder(this).create();
			 * dialog.setTitle("Add new item?");
			 * dialog.setMessage("No match - care to add a new item?");
			 * dialog.setButton("OK", new DialogInterface.OnClickListener() {
			 * public void onClick(DialogInterface dialog, int which) {
			 * cue_language = Main.search_lang; response_language =
			 * Main.result_lang; create_new_item(); } });
			 * dialog.setButton2("Cancel", new DialogInterface.OnClickListener()
			 * { public void onClick(DialogInterface dialog, int which) { Intent
			 * intent = new Intent(Intent.ACTION_VIEW);
			 * intent.setClassName(ItemListActivity.this, Main.class
			 * .getName()); Utils.putExtra(intent, "query_string",
			 * query_string); ItemListActivity.this.startActivity(intent); } });
			 * dialog.show();
			 */
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// menu.add(0, CREATE_ITEM_ID, 0, R.string.menu_create_item).setIcon(
		// android.R.drawable.ic_menu_add);
		if (items != null && items.length() > 0
				&& number_results > items_per_page) {
			menu.add(0, LOAD_MORE, 0, R.string.menu_load_more).setIcon(
					android.R.drawable.ic_menu_more);
		}

		return true;
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case CREATE_ITEM_ID: { create_new_item(); break; }
	 * case LOAD_MORE: { SmartFmMenus.loadItems(this, query_string, start_index
	 * / items_per_page + 2); break; } } return
	 * super.onOptionsItemSelected(item); }
	 */
	// TODO best setup would be to allow user to add data locally
	// have all data cached, and reflect changes locally, but then
	// remind user that they need to login/signup to persist data on
	// server, but then we want to make sure as much data as possible
	// comes back to server - and caching is beyond current project scope

	// TODO other approach is to have login request appear after user
	// has used creation tool, that way people who don't bother to login
	// get to see that tool ... of course irritating to be asked to login
	// after you have created data, but at least then you have the user
	// invested in the login/signup process, because they have something to
	// lose if they don't login/signup ...
	/*
	 * private void create_new_item() { Intent intent = new
	 * Intent(Intent.ACTION_VIEW); if (list_id == null) { list_id =
	 * Main.default_study_list_id; }
	 * 
	 * intent.setClassName(this, CreateItemActivity.class.getName());
	 * Utils.putExtra(intent, "list_id", list_id); Utils.putExtra(intent, "cue",
	 * query_string.replaceAll("\\++", " "));
	 * 
	 * Utils.putExtra(intent, "cue_language", cue_language == null ?
	 * Main.search_lang : cue_language); Utils.putExtra(intent,
	 * "response_language", response_language == null ? Main.result_lang :
	 * response_language);
	 * 
	 * startActivity(intent); }
	 */
	// so I think we need to have an item page that displays all extra info
	// about item
	// and that is where we could upload sentences with images etc.

	// however it would be nice if we could still play the sounds on clicking
	// the sound icon,
	// but go to the item page if we click on the item itself ...
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// playSound(position);
		/*
		 * Intent intent = new Intent(Intent.ACTION_VIEW);
		 * intent.setClassName(this, ItemActivity.class.getName()); // ShowItem
		 * could be an expandable list - then you could test yourself // on
		 * items - sort of ... putExtra(intent, "item_position_id",
		 * Long.toString(id)); putExtra(intent, "list_id", list_id);
		 * startActivity(intent);
		 */
		// TODO got null pointer here once - could be more careful grabbing this
		//loadItem(this, ItemListActivity.items.elementAt(position).atts
		//		.get("id").toString());
	}
/*
	public static void loadItem(Activity activity, final String item_id) {
		ProgressDialog myProgressDialog = new ProgressDialog(activity);
		myProgressDialog.setTitle("Please Wait ...");
		myProgressDialog.setMessage("Loading item ...");
		myProgressDialog.setIndeterminate(true);
		myProgressDialog.setCancelable(true);

		final ItemDownload item_download = new ItemDownload(activity,
				myProgressDialog) {
			public Vector<Node> downloadCall(SmartFmLookup lookup) {
				return lookup.item(item_id);
			}
		};
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
	*/

	private static void playSound(int position, Context context) {
		String sound_url = null;
		try {

			// TODO recently I don't see any sound in the item lists - wonder if
			// it has been removed?
			sound_url = items.getJSONObject(position).getJSONObject("cue")
					.getJSONObject("content").getString("sound");
		} catch (Exception e) {
			Log.d("ItemListActivity", "No sound for this item");
			e.printStackTrace();
			return;
		}
		// TODO removed for the moment
		// Main.playSound(sound_url, ItemListActivity.mMediaPlayer, context);

	}

	public static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap play_sound;
		private Bitmap play_sound_disabled;
		private JSONArray items;
		private Context context;

		public EfficientAdapter(Context context, JSONArray items) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.items = items;
			this.context = context;

			// Icons bound to the rows.
			play_sound = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.active_sound);
			play_sound_disabled = BitmapFactory.decodeResource(context
					.getResources(), R.drawable.inactive_sound);
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return this.items.length();
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique list_id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_icon_text,
						null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.

			String cue_text = "";
			String cue_type;
			String character = "";
			JSONObject cue;
			JSONObject content;
			try {
				cue = items.getJSONObject(position).getJSONObject("cue");
				content = cue.getJSONObject("content");
				cue_text = content.getString("text");

				if (content.has("character")) {
					character = content.getString("character");
					if (!character.equals("")) {
						character = "Åu" + character + "Åv";
						cue_text += character;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				cue_text = "error";
			}
			String response = "";
			try {
				response = items.getJSONObject(position).getJSONObject(
						"response").getJSONObject("content").getString("text");

			} catch (Exception e) {
				response = "error";
			}
			int no_of_examples = 0;
			try {
				no_of_examples = items.getJSONObject(position).getJSONObject(
						"cue").getJSONObject("related").getJSONArray(
						"sentences").length();

			} catch (Exception e) {
				Log.d("SMART-FM", "Trying to get number of sentences: "
						+ e.getMessage());
			}
			holder.text.setText(cue_text + " - " + response + " ("
					+ no_of_examples + " example"
					+ (no_of_examples == 1 ? "" : "s") + ")");

			String sound_url = null;
			try {
				sound_url = items.getJSONObject(position).getJSONObject("cue")
						.getJSONObject("content").getString("sound");
			} catch (Exception e) {
				Log.d("ItemListActivity", "No sound for this item");
				// e.printStackTrace();
			}

			if (sound_url != null && !sound_url.equals("")) {
				holder.icon.setImageBitmap(play_sound);
				OnClickListener listener = new OnClickListener() {
					public void onClick(View v) {
						playSound(position, context);
					}
				};
				holder.icon.setOnClickListener(listener);
			} else {
				holder.icon.setImageBitmap(play_sound_disabled);
				holder.icon.setOnClickListener(null);

			}
			return convertView;
		}

		static class ViewHolder {
			TextView text;
			ImageView icon;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

}
