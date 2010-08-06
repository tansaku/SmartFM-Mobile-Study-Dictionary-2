package com.neurogrid.smartfm;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fm.smart.Item;
import fm.smart.Lookup;
import fm.smart.Node;
import fm.smart.Sentence;

public abstract class ItemDownload extends Thread {
	Lookup lookup = new Lookup();
	Activity context;
	ProgressDialog progress_dialog;

	public ItemDownload(Activity context, ProgressDialog progress_dialog) {
		this.context = context;
		this.progress_dialog = progress_dialog;
	}

	public abstract JSONObject downloadCall(Lookup lookup);

	public void run() {
		Item item = new Item();
		Node author_node = null;

		try {
			// TODO for cancel to work we'll need to keep checking for
			// interrupted state?
			/* if (!this.isInterrupted()) */

			item.item_node = downloadCall(lookup);
			item.cue_node = item.item_node.getJSONObject("cue");

			item.sentences_item = item.cue_node.getJSONObject("related")
					.getJSONArray("sentences");
			if (item.sentences_item != null) {
				Log.d("DEBUG", "sentences_item: "
						+ item.sentences_item.toString());
				JSONObject sentence_list = item.sentences_item.getJSONObject(0);
				Log.d("DEBUG", "sentence_list: " + sentence_list.toString());
			}

			item.cue_text = item.cue_node.getJSONObject("content").getString(
					"text");
			if (item.cue_node.getJSONObject("content").has("character")) {
				item.character = item.cue_node.getJSONObject("content")
						.getString("character");
				if (!item.character.equals("")) {
					item.character = "Åu" + item.character + "Åv";
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
			// not available any more by the look of it
			// author_node = item.item_node.getFirst("author");
			if (author_node != null) {
				item.author_name = author_node.getFirstContents("name");
				item.author_icon_url = author_node.getFirst("icon").atts.get(
						"href").toString();
			}
			item.type = item.response_node.getString("type");

			Bitmap author_icon_default = BitmapFactory.decodeResource(context
					.getResources(), R.drawable.no_user_image);
			// TODO removed temporarily,but not using this, so maybe chuck
			// out
			// item.author_image = Main.getRemoteImage(item.author_icon_url,
			// author_icon_default);
			if (item.cue_node.getJSONObject("content").has("sound")) {
				item.cue_sound_url = item.cue_node.getJSONObject("content")
						.getString("sound");
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

					sentence = Sentence.createSentence(item.sentences_item
							.getJSONObject(i).getString("id"));
					item.children[i + 1][0] = sentence.translation;

					Log.d("DEBUG", item.children[i + 1][0]);

					item.sentence_vector.addElement(sentence);

				}
			}
			ItemActivity.item = item;

		} catch (Exception e) {
			e.printStackTrace();
			// return;
		}

		progress_dialog.dismiss();
		if (item.item_node == null || item.cue_text == null) {
			// some sort of failure
			((Activity) context).runOnUiThread(new Thread() {
				public void run() {
					final AlertDialog dialog = new AlertDialog.Builder(context)
							.create();
					dialog.setTitle("Network Failure");
					dialog.setMessage("Please try again later");
					dialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					// TODO suggest to user to upload new sound?
					dialog.show();
				}
			});
		} else if (!this.isInterrupted()) {
			// context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
			// .parse("content://" + SmartFm.AUTHORITY + "/item/7")));

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, ItemActivity.class.getName());
			context.startActivity(intent);
		}
	}
}
