package fm.beluga.zero.belugasample.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;

public class OptionActivity extends Activity {
	Beluga beluga = Beluga.Instance();
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		Uri uri = getIntent().getData();
		if (uri != null) {
			beluga.setUserToken(uri.getQueryParameter("user_id"), uri.getQueryParameter("user_token"));
			if(beluga.isConnected()){
				Toast.makeText(this, "認証しました", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "認証に失敗しました。", Toast.LENGTH_LONG).show();
			}
		}

		Button auth_button = (Button) findViewById(R.id.auth_button);
		auth_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doIntent(Intent.ACTION_VIEW, Beluga.auth_url);
			}
		});

		TextView text = (TextView)findViewById(R.id.auth_label);
		if (beluga.isConnected()) {
			text.setText("認証済み");
		}else{
			text.setText("未認証");
		}
		
		Button back_button = (Button)findViewById(R.id.option_back_button);
		back_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				goMainAction();
			}
		});
	}
	
	private void goMainAction() {
		Intent intent = new Intent(OptionActivity.this, MainActivity.class);
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
