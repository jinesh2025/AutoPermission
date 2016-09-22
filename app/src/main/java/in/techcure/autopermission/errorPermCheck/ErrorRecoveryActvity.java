package in.techcure.autopermission.errorPermCheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import in.techcure.autopermission.MainActivity;
import in.techcure.autopermission.R;

/**
 * Created by Jinesh Soni on 22/09/2016.
 */

public class ErrorRecoveryActvity extends AppCompatActivity {

	PermissionHelper permissionHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO NOTE : can make own error recovery activity look like permission asking activity
		setContentView(R.layout.activity_error_recovery_actvity);

		permissionHelper = new PermissionHelper(this);

		TextView errorView = (TextView) findViewById(R.id.tv_error);
		final String error = getIntent().getStringExtra("error");
		errorView.setText(error);

		ArrayList<String> perms = new ArrayList<>();

		String[] splited = error.split("\\s+");

		for (int i = 0; i < splited.length; i++) {
			if (splited[i].contains("android.permission")) {
				perms.add(splited[i]);
				Log.v("sd-log - raw", splited[i]);
			}
		}

		//Eliminate Redundancy
		HashSet hs = new HashSet();
		hs.addAll(perms);
		perms.clear();
		perms.addAll(hs);

		for (int i = 0; i < perms.size(); i++) {
			Log.v("sd-log - refined", perms.get(i));
		}

		permissionHelper.checkPermission(perms);

		findViewById(R.id.btn_send_error).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				//In-case error details needed on succeeding Activity
				intent.putExtra("error", error);
				startActivity(intent);
				finish();
			}
		});
	}
}

