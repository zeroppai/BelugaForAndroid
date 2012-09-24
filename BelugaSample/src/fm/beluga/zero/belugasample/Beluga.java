package fm.beluga.zero.belugasample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import android.util.Log;

public class Beluga {
	public static final String app_id = "41";
	public static final String app_secret = "NDNlYTNkYzc1OGYzMzQ5NzkyYzE2ZjYwNzBmZjdjNjUxZjRhYjQyOAXX";
	public static final String auth_url = "http://beluga.fm/authorize?app_id=41";

	String user_id = "";
	String user_token = "";

	static Beluga beluga_instanec = new Beluga();
	private String last_id = "0";

	private Map<Integer, Room> room_list = null;

	/**
	 * タイムライン class
	 */
	public static class Timeline {
		public int id, room_id;
		public String user_name, user_sname, text, date_string, room_name;
		public Bitmap icon_x50, icon_x75, icon_x100;
	}

	public static class Room {
		public int id, category_id, first_status_id, created_at, permission, created_by, last_status_id, last_update_time;
		public String name, short_url, url, hash, description;
		public boolean secret, show_followers;
	}

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
			Log.d("homo",url);
			JSONArray jsons = new JSONArray(getData(url));
			return (jsons.length() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ルームの検索、なければnull
	 * 
	 * @param id
	 * @return
	 */
	public Room searchRoom(int id) {
		return room_list.get(id);
	}

	/**
	 * ルームリストの取得
	 * 
	 * @return
	 */
	public List<Room> getRoomList() {
		if (room_list == null) {
			room_list = getFollowing();
		}
		return new ArrayList<Room>(room_list.values());
	}

	private Map<Integer, Room> getFollowing() {
		String url = "http://api.beluga.fm/1/account/following?user_id=" + user_id + "&user_token=" + user_token + "&app_id="
				+ app_id + "&app_secret=" + app_secret;
		Log.d("homo",url);
		Map<Integer, Room> list = new TreeMap<Integer, Room>();
		try {
			JSONArray jsons = new JSONArray(getData(url));
			for (int i = 0; i < jsons.length(); i++) {
				JSONObject obj = jsons.getJSONObject(i);
				Room room = new Room();
				room.id = obj.getInt("id");
				room.name = obj.getString("name");
				room.hash = obj.getString("hash");
				room.description = obj.getString("description");
				room.url = obj.getString("url");
				room.last_update_time = obj.getInt("last_update_time");
				list.put(room.id, room);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 指定したルームに投稿する。
	 * 
	 * @param text
	 * @param room_hash
	 * @return boolean
	 */
	public boolean postText(String text) {
		return postText("11eJDfcF96UIc", text);
	}

	public boolean postText(String room_hash, String text) {
		String encode_text = null;
		String encode_room = null;
		try {
			encode_text = URLEncoder.encode(text, "UTF-8");
			encode_room = URLEncoder.encode(room_hash, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String url = "http://api.beluga.fm/1/statuses/update?user_id=" + user_id + "&user_token=" + user_token + "&app_id="
				+ app_id + "&app_secret=" + app_secret + "&room_hash=" + encode_room + "&text=" + encode_text;
		Log.d("homo", "url:"+url+"\npost:"+getData(url));
		return true;
	}

	/**
	 * タイムラインを取得する。 since_idで指定したID以降を取得する。
	 * 
	 * @param since_id
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
		List<Timeline> list = stringToJson(getData(url));
		if (list != null && list.size() > 0) this.last_id = String.valueOf(list.get(0).id);
		return list;
	}

	/**
	 * 指定したタイムラインを取得する。 since_idで指定したID以降を取得する。
	 * 
	 * @param since_id
	 * @return List<Timeline> タイムラインのリスト
	 */
	public List<Timeline> getRoom(String room_hash) {
		return getRoom(room_hash, "");
	}

	public List<Timeline> getRoom(String room_hash, String since_id) {
		String encode_room = null;
		try {
			encode_room = URLEncoder.encode(room_hash, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String url = "http://api.beluga.fm/1/statuses/room?user_id=" + user_id + "&user_token=" + user_token + "&app_id="
				+ app_id + "&app_secret=" + app_secret + "&room_hash=" + encode_room;
		Log.d("homo",url);
		if (since_id != "") url += "&since_id=" + since_id;
		return stringToJson(getData(url));
	}

	/**
	 * StringからList<Timeline>に変換する
	 */
	private List<Timeline> stringToJson(String text) {
		ArrayList<Timeline> list = new ArrayList<Timeline>();
		try {
			JSONArray jsons = new JSONArray(text);
			for (int i = 0; i < jsons.length(); i++) {
				JSONObject jsonObj = jsons.getJSONObject(i);
				Timeline tl = new Timeline();
				tl.id = jsonObj.getInt("id");
				tl.user_name = jsonObj.getJSONObject("user").getString("name");
				tl.user_sname = jsonObj.getJSONObject("user").getString("screen_name");
				tl.text = jsonObj.getString("text");
				tl.date_string = jsonObj.getString("date_string");

				tl.room_id = jsonObj.getJSONObject("room").getInt("id");
				tl.room_name = jsonObj.getJSONObject("room").getString("name");

				// アイコン処理
				String url_x50 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x50");
				String url_x75 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x75");
				String url_x100 = jsonObj.getJSONObject("user").getJSONObject("profile_image_sizes").getString("x100");
				tl.icon_x50 = getBitMapFromUrl(url_x50);
				tl.icon_x75 = getBitMapFromUrl(url_x75);
				tl.icon_x100 = getBitMapFromUrl(url_x100);

				list.add(tl);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	private Bitmap getBitMapFromUrl(String url){
		Bitmap result = null;
		try {
			result = BitmapFactory.decodeStream((new URL(url).openStream()));
		} catch (SocketTimeoutException e){
			result = BitmapFactory.decodeFile("/BelugaSample/res/drawable-hdpi/ic_launcher.png");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
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
		HttpConnectionParams.setSoTimeout(params, 3000); // データ取得のタイムアウト
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
			e.printStackTrace();
			return sReturn;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return sReturn;
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
