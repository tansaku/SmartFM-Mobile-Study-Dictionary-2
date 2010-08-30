package com.neurogrid.smartfm;

import android.util.Log;

public class LanguagePair extends Object {
	String search_language_code;
	String result_language_code;

	public LanguagePair(String search_language_code,
			String result_language_code) {
		this.search_language_code = search_language_code;
		this.result_language_code = result_language_code;
	}

	public String toString() {
		return search_language_code + " , " + result_language_code;
	}

	public boolean equals(Object pair) {
		Log.d("LP-DEBUG", pair.getClass().getName());
		Log.d("LP-DEBUG", pair.toString());
		Log.d("LP-DEBUG", this.toString());
		if (pair.getClass().getName().equals(
				"fm.smart.r1.Main$LanguagePair")) {
			LanguagePair lp = (LanguagePair) pair;
			if (this.result_language_code.equals(lp.result_language_code)
					&& this.search_language_code
							.equals(lp.search_language_code)) {
				return true;
			}
		}
		return false;

	}
}
