package fm.beluga.zero.belugasample.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;

public class UpdateActivity extends Activity {
	Beluga beluga = Beluga.Instance();
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		Spinner sp = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_update);
		
		List<Beluga.Room> list = beluga.getRoomList();
		if(list!=null)
		for(int i=0;i<list.size();i++){
			Log.d("homo","test "+i);
		}
//		for (Beluga.Room room : list) {
//			if(room!=null)
//				Log.d("homo",room.name);
//			adapter.add("oppai");
//		}
		sp.setAdapter(adapter);
		sp.setSelection(0);
	}
}
