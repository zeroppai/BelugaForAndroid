package fm.beluga.zero.belugasample.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;
import fm.beluga.zero.belugasample.activity.OptionActivity.Config;

public class MainActivity extends Activity {
	private ListAdapter listAdapter;

	private Beluga beluga = Beluga.Instance();
	private List<Beluga.Timeline> timeline_list = new ArrayList<Beluga.Timeline>();
	private Timer timer;
	private Handler handler = new Handler();

	public static final String APP_STRAGE = "BelugaConfig";
	private static ProgressDialog objDialog;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// for over 3.0
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		// Check Token
		if (!beluga.isConnected()) {
			Toast.makeText(this, "トークンが設定されていません", Toast.LENGTH_LONG).show();
			goOptionAction();
		} else {
			beluga.getRoomList();
		}

		objDialog = new ProgressDialog(this);
		objDialog.setMessage("タイムラインを取得してます。...");
		objDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		objDialog.show();

		new Thread(new Runnable() {
			@Override public void run() {
				List<Beluga.Timeline> list = beluga.getHome();
				if (list != null) timeline_list.addAll(list);
				objDialog.dismiss();

				handler.post(new Runnable() {
					@Override public void run() {
						listAdapter.notifyDataSetChanged();
					}
				});

				// Timer
				timer = new Timer(true);
				timer.schedule(new TimerTask() {
					@Override public void run() {
						// TODO Auto-generated method stub
						if (beluga.isConnected()) {
							updateTimeline();
						}
					}
				}, 0, 30000);
			}
		}).start();

		listAdapter = new ListAdapter(this, timeline_list);
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
		// 別スレッドでUIにアクセスしたい場合はHandlerクラスを利用する
		handler.post(new Runnable() {
			@Override public void run() {
				// TODO Auto-generated method stub
				listAdapter.notifyDataSetChanged();
			}
		});
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

	public static class ListAdapter extends ArrayAdapter<Beluga.Timeline> {
		private LayoutInflater mInflater;
		private TextView mSname, mNmae, mText, mOption;
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
					e.printStackTrace();

					mIcon.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher));
					Log.e("homo", "Icon load error");
				}

				Typeface fontType = Config.fontType(getContext());

				mSname = (TextView) convertView.findViewById(R.id.sname_text);
				mSname.setText(item.user_sname);
				mSname.setTypeface(fontType);

				mNmae = (TextView) convertView.findViewById(R.id.name_text);
				mNmae.setText(item.user_name);
				mNmae.setTypeface(fontType);

				mText = (TextView) convertView.findViewById(R.id.main_text);
				mText.setText(item.text);
				mText.setTypeface(fontType);
				Linkify.addLinks(mText, Linkify.ALL);

				mOption = (TextView) convertView.findViewById(R.id.option_text);
				mOption.setText(item.date_string + " - " + item.room_name);
				mOption.setTypeface(fontType);
			}
			return convertView;
		}
	}
}
