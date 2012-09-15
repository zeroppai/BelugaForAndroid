package fm.beluga.zero.belugasample.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;
import fm.beluga.zero.belugasample.activity.MainActivity.ListAdapter;

public class RoomActivity extends Activity {
	private ListAdapter listAdapter;

	private Beluga beluga = Beluga.Instance();
	private List<Beluga.Timeline> timeline_list = new ArrayList<Beluga.Timeline>();
	private Timer timer;
	private Handler handler = new Handler();

	private String room_hash = null;
	private String last_id = "0";

	@Override protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new Thread(new Runnable() {
			@Override public void run() {
				if (room_hash != null) {
					List<Beluga.Timeline> list = beluga.getRoom(room_hash);
					if (list != null) timeline_list.addAll(list);
				}
			}
		}).start();
		
		String hash = getIntent().getStringExtra("room_hash");
		if(hash!=null) room_hash = hash;

		// Timer
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override public void run() {
				// TODO Auto-generated method stub
				if (beluga.isConnected()) {
					updateTimeline();

					// 別スレッドでUIにアクセスしたい場合はHandlerクラスを利用する
					handler.post(new Runnable() {
						@Override public void run() {
							// TODO Auto-generated method stub
							listAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		}, 10000, 30000);

		listAdapter = new ListAdapter(getApplicationContext(), timeline_list);

		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(listAdapter);
	}

	private void updateTimeline() {
		if (room_hash != null) {
			List<Beluga.Timeline> list = beluga.getRoom(room_hash, last_id);
			if (list != null && list.size() > 0) {
				last_id = String.valueOf(list.get(0).id);
				Collections.reverse(list);
				for (Beluga.Timeline tl : list) {
					timeline_list.add(0, tl);
				}
			}
		}
	}
}
