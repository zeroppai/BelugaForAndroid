package fm.beluga.zero.belugasample.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;

public class MainActivity extends Activity {
	private ListAdapter listAdapter;

	private Beluga beluga = Beluga.Instance();
	private List<Beluga.Timeline> timeline_list = new ArrayList<Beluga.Timeline>();
	private Timer timer;
	private Handler handler = new Handler();

	public static final String APP_STRAGE = "BelugaConfig";

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

		// Load Config
		SharedPreferences settings = getSharedPreferences(APP_STRAGE, 0);
		beluga.setUserToken(settings.getString("user_id", ""), settings.getString("user_token", ""));

		// Check Token
		if (!beluga.isConnected()) {
			Toast.makeText(this, "トークンが設定されていません", Toast.LENGTH_LONG).show();
			goOptionAction();
		} else {
			Toast.makeText(this, "タイムラインを取得してます", Toast.LENGTH_LONG).show();
			beluga.getRoomList();
		}

		new Thread(new Runnable() {
			@Override public void run() {
				List<Beluga.Timeline> list = beluga.getHome();
				if (list != null) timeline_list.addAll(list);
			}
		}).start();

		// Timer
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override public void run() {
				// TODO Auto-generated method stub
				if(beluga.isConnected()){
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
		List<Beluga.Timeline> list = beluga.getHomeFromLast();
		if (list != null) {
			Collections.reverse(list);
			for (Beluga.Timeline tl : list) {
				timeline_list.add(0, tl);
			}
		}
	}
	
	private void goRoomlistAction() {
		Intent intent = new Intent(MainActivity.this, RoomlistActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}
	
	private void goOptionAction() {
		Intent intent = new Intent(MainActivity.this, OptionActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	private void goUpdateAction() {
		Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1: // 書き込み
			goUpdateAction();
			break;
		case R.id.item_oppai: // おっぱい
			beluga.postText(getString(R.string.oppai));
			Toast.makeText(getApplicationContext(), R.string.oppai, Toast.LENGTH_SHORT).show();
			break;
		case R.id.item2: // 更新
			updateTimeline();
			listAdapter.notifyDataSetChanged();
			break;
		case R.id.item3: // ルーム一覧
			goRoomlistAction();
			break;
		case R.id.item4: // 設定
			goOptionAction();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ListAdapter extends ArrayAdapter<Beluga.Timeline> {
		private LayoutInflater mInflater;
		private TextView mNmae, mText, mOption;
		private ImageView mIcon;

		public ListAdapter(Context context, List<Beluga.Timeline> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.timeline_row, null);
			}

			final Beluga.Timeline item = this.getItem(position);
			if (item != null) {
				mIcon = (ImageView) convertView.findViewById(R.id.icon_image);
				try {
					mIcon.setImageBitmap(item.icon_x75);
				} catch (Exception e) {
					Log.e("homo", "Icon load error");
				}

				mNmae = (TextView) convertView.findViewById(R.id.name_text);
				mNmae.setText(item.name);

				mText = (TextView) convertView.findViewById(R.id.main_text);
				mText.setText(item.text);

				mOption = (TextView) convertView.findViewById(R.id.option_text);
				mOption.setText(item.date_string + " - " + item.room_name);
			}
			return convertView;
		}
	}
}
