package fm.beluga.zero.belugasample.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.Beluga.Room;
import fm.beluga.zero.belugasample.R;

public class RoomlistActivity extends Activity {
	private RoomListAdapter listAdapter;
	private Beluga beluga = Beluga.Instance();
	private List<Beluga.Room> room_list = new ArrayList<Beluga.Room>();;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomlist);

		room_list = beluga.getRoomList();
		listAdapter = new RoomListAdapter(getApplicationContext(), room_list);

		ListView listView = (ListView) this.findViewById(R.id.room_list_view);
		listView.setAdapter(listAdapter);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.roomlist_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1: // タブの再読み込み
			Intent intent = new Intent(RoomlistActivity.this, MainTabsActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			break;
		case R.id.item2://キャンセル
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class RoomListAdapter extends ArrayAdapter<Beluga.Room> {
		private LayoutInflater mInflater;
		private TextView mName, mDesc, mUrl;
		private CheckBox check_box;
		private SharedPreferences settings;

		public RoomListAdapter(Context context, List<Beluga.Room> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.roomlist_row, null);
				convertView.setClickable(true);
				convertView.setLongClickable(true);
			}

			final Beluga.Room item = this.getItem(position);
			if (item != null) {
				settings = getSharedPreferences(MainActivity.APP_STRAGE, 0);

				mName = (TextView) convertView.findViewById(R.id.roomlist_row_name);
				mDesc = (TextView) convertView.findViewById(R.id.roomlist_row_description);
				mUrl = (TextView) convertView.findViewById(R.id.roomlist_row_url);

				mName.setText(item.name);
				mDesc.setText(item.description);
				mUrl.setText(item.url);

				check_box = (CheckBox) convertView.findViewById(R.id.checkBox1);
				check_box.setChecked(settings.getBoolean("room_" + item.id, false));

				convertView.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						// TODO Auto-generated method stub
						Room item = room_list.get(position);
						boolean set_value = !settings.getBoolean("room_" + item.id, false);

						// Save Config
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean("room_" + item.id, set_value);
						editor.commit();

						if (set_value) {
							Toast.makeText(getApplicationContext(), item.name + "をタブに追加しました", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), item.name + "をタブから削除しました", Toast.LENGTH_SHORT).show();
						}
						check_box.setChecked(set_value);
						listAdapter.notifyDataSetChanged();
					}
				});
			}

			return convertView;
		}
	}
}
