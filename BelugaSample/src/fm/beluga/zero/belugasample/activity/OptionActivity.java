package fm.beluga.zero.belugasample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;

public class OptionActivity extends Activity {
	private Beluga beluga = Beluga.Instance();
	private SharedPreferences settings;
	private Spinner font_type_sp;

	public static final String[] typefaceNames = { "指定なし", "DEFAULT", "DEFAULT_BOLD", "SANS_SERIF", "SERIF", "MONOSPACE" };
	public static final Typeface[] typefaces = { null, Typeface.DEFAULT, Typeface.DEFAULT_BOLD, Typeface.SANS_SERIF,
			Typeface.SERIF, Typeface.MONOSPACE };

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		settings = getSharedPreferences(MainActivity.APP_STRAGE, 0);

		Uri uri = getIntent().getData();
		if (uri != null) {
			final String user_id = uri.getQueryParameter("user_id");
			final String user_token = uri.getQueryParameter("user_token");
			beluga.setUserToken(user_id, user_token);
			if (beluga.isConnected()) {
				// Save Setting
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("user_id", user_id);
				editor.putString("user_token", user_token);
				editor.commit();

				Toast.makeText(this, "認証しました", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "認証に失敗しました。", Toast.LENGTH_LONG).show();
			}
		}

		// 認証ボタン
		Button auth_button = (Button) findViewById(R.id.auth_button);
		auth_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doIntent(Intent.ACTION_VIEW, Beluga.auth_url);
			}
		});
		TextView text = (TextView) findViewById(R.id.auth_label);
		if (beluga.isConnected()) {
			text.setText("認証済み");
		} else {
			text.setText("未認証");
		}

		// フォント変更
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.new_simple_spinner_dropdown_item);
		for (int i = 0; i < typefaceNames.length; i++) {
			adapter.add(typefaceNames[i]);
		}

		font_type_sp = (Spinner) findViewById(R.id.spinner_fonts);
		font_type_sp.setAdapter(adapter);
		font_type_sp.setSelection(settings.getInt("font_type_setting", 0));

		// 保存して戻るボタン
		Button back_button = (Button) findViewById(R.id.option_back_button);
		back_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Editor edit = settings.edit();
				// 使用フォント
				edit.putInt("font_type_setting", font_type_sp.getSelectedItemPosition());
				
				edit.commit();
				goMainAction();
			}
		});
	}

	public static class Config {
		public static Typeface fontType(Context context) {
			SharedPreferences settings = context.getSharedPreferences(MainActivity.APP_STRAGE, 0);
			return typefaces[settings.getInt("font_type_setting", 0)];
		}
	}

	private void goMainAction() {
		Intent intent = new Intent(OptionActivity.this, MainTabsActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	private void doIntent(String action, String uriString) {
		try {
			Intent intent = new Intent(action, Uri.parse(uriString));
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "失敗\n" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
