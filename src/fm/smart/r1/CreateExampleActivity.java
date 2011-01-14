/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.smart.r1;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.Result;
import fm.smart.Utils;
import fm.smart.r1.results.AddItemResult;
import fm.smart.r1.results.AddSentenceResult;
import fm.smart.r1.results.CreateExampleResult;

public class CreateExampleActivity extends Activity implements
		View.OnClickListener {
	public static ProgressDialog myProgressDialog;
	private static CreateExampleResult create_example_result = null;
	private String item_id = null;
	private String goal_id = null;
	private String example = null;
	private String cue = null;
	private String translation = null;
	public static String example_language = null;
	public static String translation_language = null;
	protected static AddSentenceResult add_sentence_goal_result;
	protected static AddItemResult add_item_goal_result;
	private String example_transliteration = null;
	private String translation_transliteration = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.create_example);
		final Intent queryIntent = getIntent();
		Bundle extras = queryIntent.getExtras();
		item_id = (String) extras.get("item_id");
		goal_id = (String) extras.get("goal_id");
		if (goal_id == null || goal_id.equals("")) {
			goal_id = Main.default_study_goal_id;
		}

		cue = (String) extras.get("cue");
		example = (String) extras.get("example");
		translation = (String) extras.get("translation");
		example_language = (String) extras.get("example_language");
		translation_language = (String) extras.get("translation_language");
		example_transliteration = (String) extras
				.get("example_transliteration");
		translation_transliteration = (String) extras
				.get("translation_transliteration");

		TextView example_text = (TextView) findViewById(R.id.create_example_sentence);
		if (!TextUtils.isEmpty(example)) {
			example_text.setText(example);
		}
		example_text.setHint(example_language + " sentence with " + cue);
		TextView translation_text = (TextView) findViewById(R.id.create_example_translation);
		if (!TextUtils.isEmpty(translation)) {
			translation_text.setText(translation);
		}
		translation_text.setHint(translation_language
				+ " translation of example sentence");

		Button button = (Button) findViewById(R.id.create_example_submit);
		button.setOnClickListener(this);

		TextView translation_text_legend = (TextView) findViewById(R.id.create_example_translation_legend);

		TextView sentence_transliteration_textView = (TextView) findViewById(R.id.create_example_sentence_transliteration);
		EditText sentence_transliteration_input_textView = (EditText) findViewById(R.id.sentence_transliteration);
		if (!Utils.isIdeographicLanguage(Main.search_lang)) {
			sentence_transliteration_textView.setVisibility(View.GONE);
			sentence_transliteration_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(example_transliteration)) {
			sentence_transliteration_input_textView
					.setText(example_transliteration);
		}

		TextView translation_transliteration_textView = (TextView) findViewById(R.id.create_example_translation_transliteration);
		EditText translation_transliteration_input_textView = (EditText) findViewById(R.id.translation_transliteration);
		if (!Utils.isIdeographicLanguage(Main.result_lang)) {
			translation_transliteration_textView.setVisibility(View.GONE);
			translation_transliteration_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(translation_transliteration)) {
			translation_transliteration_input_textView
					.setText(translation_transliteration);
		}

	}

	// so there is question of checking for existing items (auto-completion?)
	// and uploading sounds and images ...
	public void onClick(View v) {
		EditText exampleInput = (EditText) findViewById(R.id.create_example_sentence);
		EditText translationInput = (EditText) findViewById(R.id.create_example_translation);
		EditText exampleTransliterationInput = (EditText) findViewById(R.id.sentence_transliteration);
		EditText translationTransliterationInput = (EditText) findViewById(R.id.translation_transliteration);
		final String example = exampleInput.getText().toString();
		final String translation = translationInput.getText().toString();
		if (TextUtils.isEmpty(example) || TextUtils.isEmpty(translation)) {
			Toast t = Toast.makeText(this,
					"Example and translation are required fields", 150);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		} else {
			final String example_language_code = Utils.LANGUAGE_MAP
					.get(example_language);
			final String translation_language_code = Utils.LANGUAGE_MAP
					.get(translation_language);
			final String example_transliteration = exampleTransliterationInput
					.getText().toString();
			final String translation_transliteration = translationTransliterationInput
					.getText().toString();

			if (LoginActivity.isNotLoggedIn(this)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(this, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // avoid
				// navigation
				// back to this?
				LoginActivity.return_to = CreateExampleActivity.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("goal_id", goal_id);
				LoginActivity.params.put("item_id", item_id);
				LoginActivity.params.put("example", example);
				LoginActivity.params.put("translation", translation);
				LoginActivity.params.put("example_language", example_language);
				LoginActivity.params.put("translation_language",
						translation_language);
				LoginActivity.params.put("example_transliteration",
						example_transliteration);
				LoginActivity.params.put("translation_transliteration",
						translation_transliteration);
				startActivity(intent);
			} else {

				final ProgressDialog myOtherProgressDialog = new ProgressDialog(
						this);
				myOtherProgressDialog.setTitle("Please Wait ...");
				myOtherProgressDialog.setMessage("Creating Example ...");
				myOtherProgressDialog.setIndeterminate(true);
				myOtherProgressDialog.setCancelable(true);

				final Thread create_example = new Thread() {
					public void run() {
						// TODO make this interruptable .../*if
						// (!this.isInterrupted())*/
						try {
							// TODO failures here could derail all ...
							CreateExampleActivity.add_item_goal_result = new AddItemResult(
									Main.lookup.addItemToGoal(Main.transport,
											goal_id, item_id, null));

							Result result = null;
							try {
								result = Main.lookup.createExample(
										Main.transport, translation,
										translation_language_code, translation,
										null, null, null);
								JSONObject json = new JSONObject(
										result.http_response);
								String text = json.getString("text");
								String translation_id = json.getString("id");
								result = Main.lookup.createExample(
										Main.transport, example,
										example_language_code,
										example_transliteration,
										translation_id, item_id, goal_id);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							CreateExampleActivity.create_example_result = new CreateExampleResult(
									result);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						myOtherProgressDialog.dismiss();

					}
				};
				myOtherProgressDialog.setButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								create_example.interrupt();
							}
						});
				OnCancelListener ocl = new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
						create_example.interrupt();
					}
				};
				myOtherProgressDialog.setOnCancelListener(ocl);
				myOtherProgressDialog.show();
				create_example.start();
			}
		}
	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (CreateExampleActivity.create_example_result != null) {
			synchronized (CreateExampleActivity.create_example_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = CreateExampleActivity.create_example_result
						.success();
				dialog.setTitle(CreateExampleActivity.create_example_result
						.getTitle());
				dialog.setMessage(CreateExampleActivity.create_example_result
						.getMessage());
				CreateExampleActivity.create_example_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO avoid moving to item view if previous thread was
						// interrupted? create_example.isInterrupted() but need
						// user to be aware if we
						// have created example already - progress dialog is set
						// cancelable, so back button will work? maybe should
						// avoid encouraging cancel
						// on POST operations ... not sure what to do if no
						// response from server - guess we will time out
						// eventually ...
						if (success) {
							// want to go back to individual item screen now ...
							ItemListActivity.loadItem(
									CreateExampleActivity.this, item_id);
						}
					}
				});
				dialog.show();

			}
		}
	}

}
