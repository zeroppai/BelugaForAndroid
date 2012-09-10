package fm.beluga.zero.belugasample.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fm.beluga.zero.belugasample.Beluga;
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
		
		ListView listView = (ListView)this.findViewById(R.id.room_list_view);
		listView.setAdapter(listAdapter);
	}

	public class RoomListAdapter extends ArrayAdapter<Beluga.Room> {
		private LayoutInflater mInflater;
		private TextView mName, mDesc, mUrl;

		public RoomListAdapter(Context context, List<Beluga.Room> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.roomlist_row, null);
			}

			final Beluga.Room item = this.getItem(position);
			if (item != null) {
				mName = (TextView)convertView.findViewById(R.id.roomlist_row_name);
				mDesc = (TextView)convertView.findViewById(R.id.roomlist_row_description);
				mUrl = (TextView)convertView.findViewById(R.id.roomlist_row_url);
				
				mName.setText(item.name);
				mDesc.setText(item.description);
				mUrl.setText(item.url);
			}
			
			return convertView;
		}
	}
}
