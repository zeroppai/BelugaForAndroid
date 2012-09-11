package fm.beluga.zero.ImgurAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

public class ImgurAPI {
	static public final String APIKey = "e70d68501da42b1a2b2a427b5d61d170";

	static public class ImgUrl {
		public String url,name,date_time;
	}

	static public ImgUrl uploadImage(Bitmap image) {
		ImgUrl item = new ImgUrl();
		try {
			JSONObject jsonObj = new JSONObject(postImage(image));
			item.name = jsonObj.getJSONObject("upload").getJSONObject("image").getString("name");
			item.date_time = jsonObj.getJSONObject("upload").getJSONObject("image").getString("datetime");
			item.url = jsonObj.getJSONObject("upload").getJSONObject("links").getString("original");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	static private String postImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("key", APIKey));
		params.add(new BasicNameValuePair("image", base64Image));
		params.add(new BasicNameValuePair("type", "base64"));

		return postJsonData("http://api.imgur.com/2/upload.json", params);
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
	static private String postJsonData(String sUrl, ArrayList<NameValuePair> params) {
		String sReturn = "";
		try {
			HttpPost httpPost = new HttpPost(sUrl);
			// パラメータを設定
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			// ステータスコードを取得
			if (httpResponse.getStatusLine().getStatusCode() < 400) {
				// レスポンスを取得
				HttpEntity entity = httpResponse.getEntity();
				sReturn = EntityUtils.toString(entity);
				// リソースを解放
				entity.consumeContent();
				// クライアントを終了させる
				client.getConnectionManager().shutdown();
				// 後はステータスコードやレスポンスを煮るなり焼くなり
			} else {
				Log.d("homo", "error:over 400 code");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sReturn;
	}
}
