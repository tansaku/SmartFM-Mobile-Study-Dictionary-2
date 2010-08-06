package com.neurogrid.smartfm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import fm.smart.Lookup;
import fm.smart.Utils;

public class Main extends Activity {
	/** Called when the activity is first created. */
	Lookup lookup = new Lookup();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			// so it seems the sentences being pulled in will not be filtered by translation language,
			// and the only solution appears to be storing them in database and re-running the query
			// on the database itself, as otherwise all the example counts will be off ...
			
			// actually this might allow us to compose the data, i.e. do the same thing that Kim was 
			// doing, i.e. to return only responses with multiple examples, merge those with the same meaning
			// but not clear if I can do that fast enough for the user
			// could cache it, but then there isn't a fantastically simple way to expire the cache
			
			JSONObject json = lookup.searchItemsJson("orange", 1, "2");
			
			ItemListActivity.items = json.getJSONArray("items");
			ItemListActivity.number_results = json.getInt("totalResults");
			ItemListActivity.start_index = json.getInt("startIndex");
			ItemListActivity.items_per_page = json.getInt("itemsPerPage");
			ItemListActivity.query_string = "orange";
			ItemListActivity.cue_language = Utils.INV_LANGUAGE_MAP.get(ItemListActivity.items
					.getJSONObject(0).getJSONObject("cue").getJSONObject(
					"related").getString("language"));
			ItemListActivity.response_language = Utils.INV_LANGUAGE_MAP.get(ItemListActivity.items
					.getJSONObject(0).getJSONObject("response").getJSONObject(
					"related").getString("language"));

			String cue = ItemListActivity.items.getJSONObject(0).getJSONObject("cue")
					.getJSONObject("content").getString("text");
			Log.d("SMART-FM",cue);
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this,ItemListActivity.class.getName());
			startActivity(intent);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Bitmap getRemoteImage(String url, Bitmap default_bitmap) {
		if (url == null)
			return default_bitmap;
		Bitmap bm = null;
		AndroidHttpClient client = null;
		Log.d("DEBUG", url);
		try {
			// Log.d("DEBUG", file);
			// URLConnection conn = new URL(file).openConnection();
			// conn.connect();
			// InputStream is = conn.getInputStream();
			// BufferedInputStream bis = new BufferedInputStream(is);
			// bm = BitmapFactory.decodeStream(bis);
			// bis.close();
			// is.close();
			if (!url.equals("")) {
				URI uri = new URI(url);
				HttpGet get = new HttpGet(uri);
				client = AndroidHttpClient.newInstance("Main");
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				bm = BitmapFactory.decodeStream(entity.getContent());
			}
		} catch (IOException e) {
			/* Reset to Default image on any error. */
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bm == null) {
				bm = default_bitmap;

			}
			if (client != null) {
				client.close();
			}
		}

		//
		return bm;
	}

	/*
	
	protected static MediaPlayer mediaPlayer;
	
	public static void playSound(final String sound_url,
			final MediaPlayer mediaPlayer, final Context context) {

		File dir = context.getDir(SOUND_DIRECTORY, MODE_WORLD_READABLE);
		final File cache = new File(dir, "Sound"
				+ Integer.toString(sound_url.hashCode()) + ".mp3");

		if (cache.exists()) {
			employMediaPlayer(cache);
		} else {

			final ProgressDialog myOtherProgressDialog = new ProgressDialog(
					context);
			myOtherProgressDialog.setTitle("Please Wait ...");
			myOtherProgressDialog.setMessage("Downloading sound file ...");
			myOtherProgressDialog.setIndeterminate(true);
			myOtherProgressDialog.setCancelable(true);

			final Thread download = new Thread() {
				public void run() {

					Main.save_file_result = saveFile(sound_url, cache, context);

					// TODO would be nice if failure could give report to the
					// user
					// ...
					if (Main.save_file_result.success()) {
						employMediaPlayer(cache);
						myOtherProgressDialog.dismiss();
					} else {
						myOtherProgressDialog.dismiss();
						((Activity) context).runOnUiThread(new Thread() {
							public void run() {
								final AlertDialog dialog = new AlertDialog.Builder(
										context).create();
								dialog.setTitle(Main.save_file_result
										.getTitle());
								dialog.setMessage(Main.save_file_result
										.getMessage());
								Main.save_file_result = null;
								dialog.setButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {

											}
										});
								// TODO suggest to user to upload new sound?
								dialog.show();
							}
						});
					}

				}

			};
			myOtherProgressDialog.setButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							download.interrupt();
						}
					});
			OnCancelListener ocl = new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					download.interrupt();
				}
			};
			myOtherProgressDialog.setOnCancelListener(ocl);
			myOtherProgressDialog.show();
			download.start();
		}

	}

	private static void employMediaPlayer(final File cache) {
		if (Main.mediaPlayer != null) {
			Main.mediaPlayer.release();
			Main.mediaPlayer = null;
		}
		Main.mediaPlayer = new MediaPlayer();
		FileInputStream is = null;
		try {
			is = new FileInputStream(cache);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Main.mediaPlayer.setDataSource(is.getFD());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Main.mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Main.mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	*/

}