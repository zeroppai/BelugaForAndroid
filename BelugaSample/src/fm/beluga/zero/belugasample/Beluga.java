package fm.beluga.zero.belugasample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Beluga {
	public static final String app_id = "41";
	public static final String app_secret = "NDNlYTNkYzc1OGYzMzQ5NzkyYzE2ZjYwNzBmZjdjNjUxZjRhYjQyOAXX";
	public static final String auth_url = "http://beluga.fm/authorize?app_id=41";

	String user_id = "";
	String user_token = "";

	static Beluga beluga_instanec = new Beluga();
	private String last_id = "0";

	private Map<String, Room> room_list = new HashMap<String, Room>();

	/**
	 * タイムライン
	 */
	public static class Timeline {
		public int id;
		public String name, text, date_string, room_name;
		public Bitmap icon_x50, icon_x75, icon_x100;
	};

	public static class Room {
		public int id, last_update_time;
		public String name, url, hash;
	};

	public static Beluga Instance() {
		return beluga_instanec;
	}

	public void setUserToken(String id, String token) {
		user_id = id;
		user_token = token;
	}

	public boolean isConnected() {
		try {
			String url = "http://api.beluga.fm/1/statuses/home?user_id=" + user_id + "&user_token=" + user_token + "&app_id="
					+ app_id + "&app_secret=" + app_secret + "&since_id=0";
			JSONArray jsons = new JSONArray(getData(url));
			return (jsons.length() > 0);
		} catch (Exception e) {
			return false;
		}
	}

	public List<Room> getRoomList() {
		return new ArrayList<Beluga.Room>(room_list.values());
	}

	/**
	 * タイムラインを取得する
	 * 
	 * @param since_id
	 *            指定したID以降を取得する
	 * @return List<Timeline> タイムラインのリスト
	 */
	public List<Timeline> getHome() {
		return getHome("0");
	}

	public List<Timeline> getHomeFromLast() {
		return getHome(last_id);
	}

	public List<Timeline> getHome(String since_id) {
		String url = "http://api.beluga.fm/1/statuses/home?user_id=" + user_id + "&user_token=" + user_token + "&app_id="
				+ app_id + "&app_secret=" + app_secret + "&since_id=" + since_id;
		ArrayList<Timeline> list = new ArrayList<Timeline>();

		try {
			JSONArray jsons = new JSONArray(getData(url));
			for (int i = 0; i < jsons.length(); i++) {
				JSONObject jsonObj = jsons.getJSONObject(i);
				Timeline tl = new Timeline();
				tl.id = jsonObj.getInt("id");
				tl.name = jsonObj.getJSONObject("user").getString("name");
				tl.text = jsonObj.getString("text");
				tl.date_string = jsonObj.getString("date_string");

				// アイコン処理
				try {
					String url_x50 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x50");
					String url_x75 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x75");
					String url_x100 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x100");
					tl.icon_x50 = BitmapFactory.decodeStream((new URL(url_x50).openStream()));
					tl.icon_x75 = BitmapFactory.decodeStream((new URL(url_x75).openStream()));
					tl.icon_x100 = BitmapFactory.decodeStream((new URL(url_x100).openStream()));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// ルーム処理
				JSONObject obj = jsonObj.getJSONObject("room");
				tl.room_name = obj.getString("name");

				if (!room_list.containsKey(obj.getString("hash"))) {
					Room room = new Room();
					room.id = obj.getInt("id");
					room.name = obj.getString("name");
					room.hash = obj.getString("hash");
					room.url = obj.getString("url");
					room.last_update_time = obj.getInt("last_update_time");
					room_list.put(room.hash, room);
				}
				list.add(tl);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if (list.size() > 0) {
			this.last_id = String.valueOf(list.get(0).id);
		}

		return list;
	}

	/**
	 * 指定URLからgetした文字列を取得する
	 * 
	 * @param sUrl
	 * @return
	 */
	private String getData(String sUrl) {
		HttpClient objHttp = new DefaultHttpClient();
		HttpParams params = objHttp.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000); // 接続のタイムアウト
		HttpConnectionParams.setSoTimeout(params, 1000); // データ取得のタイムアウト
		String sReturn = "";
		try {
			HttpGet objGet = new HttpGet(sUrl);
			HttpResponse objResponse = objHttp.execute(objGet);
			if (objResponse.getStatusLine().getStatusCode() < 400) {
				InputStream objStream = objResponse.getEntity().getContent();
				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;
				while ((sLine = objBuf.readLine()) != null) {
					objJson.append(sLine);
				}
				sReturn = objJson.toString();
				objStream.close();
			}
		} catch (IOException e) {
			return null;
		}
		return sReturn;
	}

	/**
	 * 指定URLからpostした文字列を取得する
	 * 
	 * @param sUrl
	 *            送信先URL
	 * @param sJson
	 *            文字列に変換したJSONデータ
	 * @return
	 */
	private String postJsonData(String sUrl, String sJson) {
		HttpClient objHttp = new DefaultHttpClient();
		String sReturn = "";
		try {
			HttpPost objPost = new HttpPost(sUrl);
			List<NameValuePair> objValuePairs = new ArrayList<NameValuePair>(2);
			objValuePairs.add(new BasicNameValuePair("json", sJson));
			objPost.setEntity(new UrlEncodedFormEntity(objValuePairs, "UTF-8"));

			HttpResponse objResponse = objHttp.execute(objPost);
			if (objResponse.getStatusLine().getStatusCode() < 400) {
				InputStream objStream = objResponse.getEntity().getContent();
				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;
				while ((sLine = objBuf.readLine()) != null) {
					objJson.append(sLine);
				}
				sReturn = objJson.toString();
				objStream.close();
			}
		} catch (IOException e) {
			return null;
		}
		return sReturn;
	}
}
