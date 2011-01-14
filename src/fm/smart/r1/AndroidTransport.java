package fm.smart.r1;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import android.util.Log;
import fm.smart.Lookup;
import fm.smart.Result;
import fm.smart.Transport;
import fm.smart.Utils;

public class AndroidTransport extends Transport {

	public Result authenticatedResponse(String url_string, String method,
			String username, String password) throws UnknownHostException {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {
			URI uri = new URI(Lookup.SMARTFM_API_HTTP_ROOT + url_string);
			Log.d("DEBUG", uri.toString());
			HttpRequestBase request = null;

			if (method.equals("GET")) {
				request = new HttpGet(uri);
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(uri);
			} else {
				throw new IOException("Unrecognized HTTP method: " + method);
			}

			if (!Utils.isEmpty(username) && !Utils.isEmpty(password)) {
				String auth = username + ":" + password;
				Log.d("SMARTFM", auth);
				byte[] bytes = auth.getBytes();
				String encoded = new String(Base64.encodeBase64(bytes));
				request.setHeader("Authorization", "Basic " + encoded);
				Log.d("SMARTFM", encoded);
			}
			request.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			request.setHeader("Host", "api.smart.fm");

			client = AndroidHttpClient.newInstance("Main");
			// TODO this sometimes throws UnknownHostException
			HttpResponse response1 = client.execute(request);
			status_code = response1.getStatusLine().getStatusCode();

			http_response = Lookup.convertStreamToString(response1.getEntity()
					.getContent());
			// HttpEntity entity = response1.getEntity();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (client != null) {
				client.close();
			}
		}

		return new Result(status_code, http_response);
	}

	public Result authenticatedResponseWithBody(String url_string,
			String method, String body, String username, String password){
		String http_response = "";
		int status_code = -1;
		AndroidHttpClient client = null;
		try {

			client = AndroidHttpClient.newInstance("Smart FM Android app v2");
			HttpPost post = new HttpPost(Lookup.SMARTFM_API_HTTP_ROOT
					+ url_string);

			String auth = username + ":" + password;
			byte[] bytes = auth.getBytes();
			// Logging this is a security hole? Since some Android apps can read these log files on the phone ...
			//Log.d("SMARTFM", auth);

			String encoded = new String(Base64.encodeBase64(bytes));
			post.setHeader("Authorization", "Basic " + encoded);
			//Log.d("SMARTFM", encoded);

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Host", "api.smart.fm");

			HttpEntity entity = new StringEntity(body, "UTF-8");
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			status_code = response.getStatusLine().getStatusCode();

			HttpEntity resEntity = response.getEntity();

			long length = response.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response.getEntity().getContent().read(response_bytes);

			http_response = new String(response_bytes);
			if (resEntity != null) {
				resEntity.consumeContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (client != null) {
				client.close();
			}
		}
		return new Result(status_code, http_response);
	}
}
