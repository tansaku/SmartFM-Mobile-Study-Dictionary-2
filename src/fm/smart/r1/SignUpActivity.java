package fm.smart.r1;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.results.SignUpResult;

public class SignUpActivity extends Activity implements View.OnClickListener {
	protected static SignUpResult signup_result;
	WebView webview = null;
	static String return_to = Main.class.getName();
	String url = "";
	static String username = null;
	static String password = null;
	static String email = null;
	static String password_confirmation = null;
	public static HashMap<String, String> params;

	private static String superusername;
	private static String superpassword ;

	// TODO switch all comms to https when Cerego is ready.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();

		// Read from the /assets directory
		try {
			InputStream inputStream = assetManager.open("config.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			superusername = properties.getProperty("superusername");
			superpassword = properties.getProperty("superpassword");
		} catch (IOException e) {
			System.err.println("Failed to open config property file");
			e.printStackTrace();
		}
		
		
		setContentView(R.layout.signup);

		Button signup_button = (Button) findViewById(R.id.signup);
		signup_button.setOnClickListener(this);

		EditText usernameInput = (EditText) findViewById(R.id.username);
		EditText passwordInput = (EditText) findViewById(R.id.password);
		EditText emailInput = (EditText) findViewById(R.id.email);
		EditText passwordConfirmationInput = (EditText) findViewById(R.id.password_confirmation);
		usernameInput.setText(username);
		passwordInput.setText(password);
		passwordConfirmationInput.setText(password);
		emailInput.setText(email);
	}

	public void onClick(View v) {
		EditText usernameInput = (EditText) findViewById(R.id.username);
		EditText passwordInput = (EditText) findViewById(R.id.password);
		EditText emailInput = (EditText) findViewById(R.id.email);
		EditText passwordConfirmationInput = (EditText) findViewById(R.id.password_confirmation);

		final String username = usernameInput.getText().toString();
		SignUpActivity.username = username;
		final String password = passwordInput.getText().toString();
		SignUpActivity.password = password;
		final String email = emailInput.getText().toString();
		SignUpActivity.email = email;
		final String password_confirmation = passwordConfirmationInput.getText().toString();
		SignUpActivity.password_confirmation = password_confirmation;

		final ProgressDialog myOtherProgressDialog = new ProgressDialog(this);
		myOtherProgressDialog.setTitle("Please Wait ...");
		myOtherProgressDialog.setMessage("Signing up ...");
		myOtherProgressDialog.setIndeterminate(true);
		myOtherProgressDialog.setCancelable(true);

		final Thread login = new Thread() {
			public void run() {
				// TODO make this interruptable .../*if
				// (!this.isInterrupted())*/
				SignUpActivity.signup_result = new SignUpResult(
						Main.lookup.signup(Main.transport, username, password,
								password_confirmation, email, superusername,
								superpassword));
				if (SignUpActivity.signup_result.success()) {
					LoginActivity.login(SignUpActivity.this,
							SignUpActivity.username, SignUpActivity.password);
					// TODO set users default study goal ...

					// TODO set users default study goal ...
					// starting to look like the work to support this
					// should go on buckets ...

					LoginActivity.create_default_list = ItemListActivity
							.getDefaultStudyList(SignUpActivity.this);
				}
				myOtherProgressDialog.dismiss();

			}
		};
		myOtherProgressDialog.setButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						login.interrupt();
					}
				});
		OnCancelListener ocl = new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				login.interrupt();
			}
		};
		myOtherProgressDialog.setOnCancelListener(ocl);
		myOtherProgressDialog.show();
		login.start();

	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (SignUpActivity.signup_result != null) {
			synchronized (SignUpActivity.signup_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = SignUpActivity.signup_result.success();
				dialog.setTitle(SignUpActivity.signup_result.getTitle());
				dialog.setMessage(SignUpActivity.signup_result.getMessage());
				SignUpActivity.signup_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// if no success then user left on login screen with
						// current data ...
						if (success) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setClassName(SignUpActivity.this,
									SignUpActivity.return_to);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							if (SignUpActivity.params != null) {
								Iterator<String> iter = SignUpActivity.params
										.keySet().iterator();
								String key = null;
								while (iter.hasNext()) {
									key = iter.next();
									if (key.equals("list_id")
											&& TextUtils
													.isEmpty(SignUpActivity.params
															.get(key))) {
										AndroidUtils.putExtra(intent, key,
												Main.default_study_goal_id);
									} else {
										AndroidUtils.putExtra(intent, key,
												SignUpActivity.params.get(key));
									}
								}
							}
							SignUpActivity.this.startActivity(intent);
						}
					}
				});
				dialog.show();

			}
		}
	}

}
