package fm.smart.r1;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import fm.smart.Item;
import fm.smart.Result;
import fm.smart.Sentence;

public class ItemDownload extends Thread {
	Activity context;
	ProgressDialog progress_dialog;
	String item_id;

	public ItemDownload(Activity context, ProgressDialog progress_dialog,
			String item_id) {
		this.context = context;
		this.progress_dialog = progress_dialog;
		this.item_id = item_id;
	}

	public void run() {
		Item item = new Item();
		String message = "Please try again later";
		AndroidHttpClient client = null;

		try {
			// TODO for cancel to work we'll need to keep checking for
			// interrupted state?
			/* if (!this.isInterrupted()) */

			String api_call = Main.lookup.itemURL(item_id, Main.search_lang,
					Main.result_lang);
			Log.d("SMARTFM", api_call);

			Result result = Main.lookup.item(Main.transport, item_id,
					Main.search_lang, Main.result_lang);

			JSONObject json = null;

			json = new JSONObject(result.http_response);

			item.item_node = json;
			item.cue_node = item.item_node.getJSONObject("cue");

			item.sentences_item = item.cue_node.getJSONObject("related")
					.getJSONArray("sentences");
			if (item.sentences_item != null) {
				Log.d("DEBUG",
						"sentences_item: " + item.sentences_item.toString());
				if (item.sentences_item.length() > 0) {
					JSONObject sentence_list = item.sentences_item
							.getJSONObject(0);
					Log.d("DEBUG", "sentence_list: " + sentence_list.toString());
				}
			}

			item.cue_text = item.cue_node.getJSONObject("content").getString(
					"text");
			if (item.cue_node.getJSONObject("content").has("character")) {
				item.character = item.cue_node.getJSONObject("content")
						.getString("character");
				if (!item.character.equals("")) {
					item.character = "u" + item.character + "v";
					item.cue_text += item.character;
				}
			}
			if (item.sentences_item != null) {
				item.number_groups += item.sentences_item.length();
			}

			item.groups = new String[item.number_groups];
			item.children = new String[item.number_groups][];

			Log.d("DEBUG", "number_groups:" + item.number_groups);
			item.groups[0] = item.cue_text;
			item.children[0] = new String[1];
			item.response_node = item.item_node.getJSONObject("response");
			item.children[0][0] = item.response_node.getJSONObject("content")
					.getString("text");

			item.part_of_speech = item.cue_node.getJSONObject("related")
					.getString("part_of_speech").toString();

			item.type = item.response_node.getString("type");

			result = Main.lookup.itemSounds(Main.transport, item_id);
			JSONObject sound = new JSONObject(result.http_response);

			if (sound.getJSONArray("sounds").length() > 0) {
				item.cue_sound_url = sound.getJSONArray("sounds")
						.getJSONObject(0).getString("url");
			}

			if (item.response_node.getJSONObject("content").has("sound")) {
				item.response_sound_url = item.response_node.getJSONObject(
						"content").getString("sound");
			}
			if (item.sentences_item != null) {
				Log.d("DEBUG", "sentences: " + item.sentences_item.toString());
				for (int i = 0; i < item.sentences_item.length(); i++) {
					item.groups[i + 1] = item.sentences_item.getJSONObject(i)
							.getString("text");
					// Log.d("DEBUG", "v: " + v.toString());
					// Node n = v.firstElement();
					// Log.d("DEBUG", "n: " + n.toString());
					// item.groups[i + 1] = n.contents;
					Log.d("DEBUG", "groups[i + 1]: " + item.groups[i + 1]);
				}
			}
			// Will need a Sentence object ...
			Sentence sentence = null;
			if (item.sentences_item != null) {
				for (int i = 0; i < item.sentences_item.length(); i++) {
					item.children[i + 1] = new String[1];

					sentence = Sentence.createSentence(Main.transport,
							item.sentences_item.getJSONObject(i)
									.getString("id"), Main.lookup);

					if (sentence.translation == null) {
						// TODO would be nice to have some way to add
						// translation ...
						sentence.translation = "No translation available";
					}
					item.children[i + 1][0] = sentence.translation;
					Log.d("DEBUG", item.children[i + 1][0]);

					item.sentence_vector.addElement(sentence);

				}
			}
			ItemActivity.item = item;

		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		} finally {

			if (client != null) {
				client.close();
			}
		}
		final String dialog_message = message;
		progress_dialog.dismiss();
		if (item.item_node == null || item.cue_text == null) {
			// some sort of failure
			Main.showErrorDialog(dialog_message, context);
		} else if (!this.isInterrupted()) {
			// context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
			// .parse("content://" + SmartFm.AUTHORITY + "/item/7")));

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, ItemActivity.class.getName());
			context.startActivity(intent);
		}
	}
}
