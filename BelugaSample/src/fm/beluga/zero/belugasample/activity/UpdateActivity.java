package fm.beluga.zero.belugasample.activity;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fm.beluga.zero.ImgurAPI.ImgurAPI;
import fm.beluga.zero.ImgurAPI.ImgurAPI.ImgUrl;
import fm.beluga.zero.belugasample.Beluga;
import fm.beluga.zero.belugasample.R;

public class UpdateActivity extends Activity {
	final int REQUEST_GALLERY = 0; 
	Beluga beluga = Beluga.Instance();
	EditText editor;
	Spinner sp;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		// Spinner
		List<Beluga.Room> list = beluga.getRoomList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item);

		adapter.setDropDownViewResource(R.layout.new_simple_spinner_dropdown_item);
		for (int i = 0; i < list.size(); i++) {
			Beluga.Room room = list.get(i);
			adapter.add(room.name);
		}

		sp = (Spinner) findViewById(R.id.spinner1);
		sp.setAdapter(adapter);
		sp.setSelection(0);

		// Editor
		editor = (EditText) findViewById(R.id.edit_text_view);

		// Send Button
		Button button = (Button) findViewById(R.id.edit_update_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText edit = (EditText) findViewById(R.id.edit_text_view);
				SpannableStringBuilder sb = (SpannableStringBuilder) edit.getText();
				if (sb.toString() != "") {
					Beluga.Room room = beluga.getRoomList().get(sp.getSelectedItemPosition());
					beluga.postText(room.hash, sb.toString());
					edit.setText("");
					Toast.makeText(getApplicationContext(), "投稿しました", Toast.LENGTH_LONG).show();
				}
			}
		});

		// Upload Image
		Button upload_image_button = (Button) findViewById(R.id.upload_image_button);
		upload_image_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// ギャラリー呼び出し
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, REQUEST_GALLERY);
			}
		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				InputStream in = getContentResolver().openInputStream(data.getData());
				Bitmap img = BitmapFactory.decodeStream(in);
				in.close();

				// 選択した画像のりよう
				ImgUrl imgurl = ImgurAPI.uploadImage(img);
				editor.setText(editor.getText() + " " + imgurl.url);
			} catch (Exception e) {

			}
		}
	}
}
