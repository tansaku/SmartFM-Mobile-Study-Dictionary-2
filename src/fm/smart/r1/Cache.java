package fm.smart.r1;

import java.util.HashMap;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import fm.smart.Result;

public class Cache extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	private BaseAdapter adapter;

	public Cache(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			this.put(null, "Unable to download");
			return "Unable to download";
		}
		Object entry = super.get(key);
		if (entry == null) {
			if (((String) key).startsWith("http://")) {
				new ImageDownloadTask(this).execute((String) key);
			} else {
				new SoundUrlsTask(this).execute((String) key);
			}
			entry = "Downloading";
		}
		return entry;
	}

	private class ImageDownloadTask extends AsyncTask<String, Integer, Object> {
		Cache cache = null;

		public ImageDownloadTask(Cache cache) {
			this.cache = cache;
		}

		protected Object doInBackground(String... key) {
			Bitmap bitmap = null;
			try {
				bitmap = MediaUtility.getRemoteImage(key[0], null);
				if (bitmap == null) {
					Log.d("SMARTFM", "image download trouble ...");
					cache.put(key[0], "Unable to download");
				} else {
					cache.put(key[0], bitmap);
				}
			} catch (Exception e) {
				Log.d("SMARTFM", "image download trouble ...");
				cache.put(key[0], "Unable to download");
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Object result) {
			adapter.notifyDataSetChanged();
		}
	}

	private class SoundUrlsTask extends AsyncTask<String, Integer, Object> {
		Cache cache = null;

		public SoundUrlsTask(Cache cache) {
			this.cache = cache;
		}

		protected Object doInBackground(String... key) {
			Result result = null;
			String entry = null;
			try {
				result = Main.lookup.itemSounds(Main.transport, key[0]);

				JSONObject sound = new JSONObject(result.http_response);
				if (sound.getJSONArray("sounds").length() > 0) {
					entry = sound.getJSONArray("sounds").getJSONObject(0)
							.getString("url");
				} else {
					entry = "Unable to download";
				}
				cache.put(key[0], entry);
			} catch (Exception e) {
				Log.d("SMARTFM", "Problem downloading sound for: " + key[0]);
				entry = "Unable to download";
				cache.put(key[0], entry);
				e.printStackTrace();
			}
			return entry;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Object result) {
			adapter.notifyDataSetChanged();
		}

	}
}
