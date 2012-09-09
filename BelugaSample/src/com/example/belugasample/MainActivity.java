package com.example.belugasample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.belugasample.R.id;

public class MainActivity extends Activity {
	private ListAdapter listAdapter;
	
	private Beluga beluga = new Beluga();
	private List<Beluga.Timeline> timeline_list = new ArrayList<Beluga.Timeline>();
	private Timer timer;
	private Handler handler = new Handler();

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timeline_list.addAll(beluga.getHome());

		// Timer
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override public void run() {
				// TODO Auto-generated method stub
				updateTimeline();
				
				//別スレッドでUIにアクセスしたい場合はHandlerクラスを利用する
				handler.post(new Runnable() {
					@Override public void run() {
						// TODO Auto-generated method stub
						listAdapter.notifyDataSetChanged();
					}
				});
			}
		}, 10000 ,30000);

		listAdapter = new ListAdapter(getApplicationContext(), timeline_list);

		ListView listView = (ListView) findViewById(id.listView1);
		listView.setAdapter(listAdapter);
	}

	private void updateTimeline() {
		List<Beluga.Timeline> list = beluga.getHomeFromLast();
		Collections.reverse(list);
		for (Beluga.Timeline tl : list) {
			timeline_list.add(0,tl);
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item1: //書き込み
				break;
			case R.id.item2: //更新
				updateTimeline();
				listAdapter.notifyDataSetChanged();
				break;
			case R.id.item3: //ルーム一覧
				break;
			case R.id.item4: //設定
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
				convertView = mInflater.inflate(R.layout.row, null);
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