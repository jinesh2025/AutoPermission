package in.techcure.autopermission;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import in.techcure.autopermission.errorPermCheck.PermissionHelper;
import in.techcure.autopermission.util.BaseActivity;

//Extend BaseActivity
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_crash).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				throw new RuntimeException("Auto gen error");
			}
		});

		findViewById(R.id.btn_perm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				(new PermissionHelper(MainActivity.this)).checkPermission();
			}
		});

		findViewById(R.id.btn_perm_auto).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				testContactPerms();
			}
		});
	}

	public void testContactPerms(){
		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {

			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		}
		phones.close();

		mToast("Reading Contact Done !!");
	}

}
