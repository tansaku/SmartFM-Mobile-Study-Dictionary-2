package fm.smart.r1;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Log;
import fm.smart.r1.results.SaveFileResult;

public class MediaUtility {
	
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
		        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
		        InputStream instream = bufHttpEntity.getContent();
		        bm = BitmapFactory.decodeStream(instream);
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

	
	
	protected static MediaPlayer mediaPlayer;
	public static final String SOUND_DIRECTORY = "sounds";
	private static SaveFileResult save_file_result = null;
	
	public static void playSound(final String sound_url,
			final MediaPlayer mediaPlayer, final Context context) {

		File dir = context.getDir(SOUND_DIRECTORY, Activity.MODE_WORLD_READABLE);
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

					save_file_result = saveFile(sound_url, cache, context);

					// TODO would be nice if failure could give report to the
					// user
					// ...
					if (save_file_result.success()) {
						employMediaPlayer(cache);
						myOtherProgressDialog.dismiss();
					} else {
						myOtherProgressDialog.dismiss();
						((Activity) context).runOnUiThread(new Thread() {
							public void run() {
								final AlertDialog dialog = new AlertDialog.Builder(
										context).create();
								dialog.setTitle(save_file_result
										.getTitle());
								dialog.setMessage(save_file_result
										.getMessage());
								save_file_result = null;
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
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mediaPlayer = new MediaPlayer();
		FileInputStream is = null;
		try {
			is = new FileInputStream(cache);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mediaPlayer.setDataSource(is.getFD());
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
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static SaveFileResult saveFile(String url, File file, Context context) {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		FileOutputStream fos = null;
		InputStream is = null;
		FileDescriptor fd = null;
		try {

			URI uri = new URI(url);
			Log.d("DEBUG", uri.toString());
			HttpGet get = new HttpGet(uri);

			// GET /assets/legacy/halpern/ja_female/16/J0150989.mp3 HTTP/1.1
			// Host: assets1.smart.fm
			// User-Agent: Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-US;
			// rv:1.8.1.20) Gecko/20081217 Firefox/2.0.0.20
			// Accept:
			// text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
			// Accept-Language: en-us,en;q=0.5
			// Accept-Encoding: gzip,deflate
			// Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
			// Keep-Alive: 300
			// Connection: keep-alive

			get.setHeader("Host", uri.getHost());

			Header[] array = get.getAllHeaders();
			for (Header h : array) {
				Log.d("DEBUG", h.toString());
			}
			client = AndroidHttpClient.newInstance("Main");
			HttpResponse response1 = client.execute(get);
			status_code = response1.getStatusLine().getStatusCode();
			Log.d("DEBUG", response1.getStatusLine().toString());
			array = response1.getAllHeaders();
			for (Header h : array) {
				Log.d("DEBUG", h.toString());
			}
			long length = response1.getEntity().getContentLength();

			// byte[] response_bytes = new byte[(int) length];
			http_response = Long.toString(length);
			// avoid writing file if not a successful response
			if (status_code == 200) {
				HttpEntity entity = response1.getEntity(); // .getContent().read(response_bytes)
				fos = new FileOutputStream(file);
				fd = fos.getFD();
				is = entity.getContent();
				int chomp = is.read();
				while (chomp != -1) {
					fos.write(chomp);
					chomp = is.read();
				}
				fos.flush();
				fos.close();
			} else {
				file.delete();
			}

		} catch (IOException e) {
			// Reset to Default image on any error.
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (client != null) {
				client.close();
			}
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return new SaveFileResult(status_code, http_response, fd);
	}
}