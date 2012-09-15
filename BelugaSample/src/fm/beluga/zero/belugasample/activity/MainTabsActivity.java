package fm.beluga.zero.belugasample.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.Beluga.Room;
import fm.beluga.zero.belugasample.R;

@SuppressWarnings("deprecation") public class MainTabsActivity extends TabActivity {
	private Beluga beluga = Beluga.Instance();
	private Set<String> room_list = new HashSet<String>();
	private SharedPreferences settings;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tabs_host);

		final TabHost tabHost = getTabHost();
		TabHost.TabSpec tabSpec;
		Intent intent;

		// Load Config
		settings = getSharedPreferences(MainActivity.APP_STRAGE, 0);
		beluga.setUserToken(settings.getString("user_id", ""), settings.getString("user_token", ""));

		// Load RoomTab Config
		// Format: タブのIDと同じ
		// key : "room_" + rooom.id
		// value : boolean
		List<Room> all_rooms = beluga.getRoomList();
		settings = getSharedPreferences(MainActivity.APP_STRAGE, 0);
		for (Room room : all_rooms) {
			if (settings.getBoolean("room_" + room.id, false)) room_list.add(String.valueOf(room.id));
		}
		
		// 1つ目のタブを作成する
		intent = new Intent(this, MainActivity.class);
		// タブのインディケーターを作成する
		tabSpec = tabHost.newTabSpec("room_home");
		// タブのラベルを作成をセットする（テキストのみ）
		tabSpec.setIndicator("ホーム");
		// タブの内容となるActivityを表示するためのIntentをセットする
		tabSpec.setContent(intent);
		// 作成したタブを追加する
		tabHost.addTab(tabSpec);

		if (room_list != null) for (String room_id : room_list) {
			intent = new Intent(this, RoomActivity.class);
			Room room = beluga.searchRoom(Integer.parseInt(room_id));
			// データ
			intent.putExtra("room_hash", room.hash);

			tabSpec = tabHost.newTabSpec("room_" + room.id);
			tabSpec.setIndicator(room.name);
			tabSpec.setContent(intent);
			tabHost.addTab(tabSpec);
		}

		// タブの大きさを調整
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().width = 150;
		}

	}
}
