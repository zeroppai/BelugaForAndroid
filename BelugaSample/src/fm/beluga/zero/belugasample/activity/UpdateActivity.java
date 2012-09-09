package fm.beluga.zero.belugasample.activity;

import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;
import android.app.Activity;
import android.os.Bundle;

public class UpdateActivity extends Activity {
	Beluga beluga = Beluga.Instance();
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
	}
}
