package fm.smart.r1;

import android.content.Intent;

public class AndroidUtils {

	public static void putExtra(Intent intent, String key, String value) {
		if (value != null && value.length() > 0) {
			intent.putExtra(key, value);
		}
	}

}
