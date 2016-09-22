package in.techcure.autopermission.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.techcure.autopermission.R;
import in.techcure.autopermission.errorPermCheck.ExceptionHandler;

/**
 * Created by Jinesh Soni on 15-08-2015.
 */
public class BaseActivity extends AppCompatActivity {

	Activity activity;
	Toolbar toolbar = null;
	ProgressDialog pDialog;
	Context mContext;
	NetworkConnectivity networkConnectivity;

	/**
	 * Hides the soft keyboard
	 */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (activity.getCurrentFocus() != null)
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static int pxToDp(int px) {
		return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}

	public void mToast(String text) {
		Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
	}

	public static View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	public static View getViewByPosition(int pos, GridView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	public void expand(final View v) {
		v.measure(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views with a height of 0.
		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? LinearLayoutCompat.LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public void setAnimation(View viewToAnimate, int anime) {
		viewToAnimate.startAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(), anime));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	@Override
	protected void onPause() {
		super.onPause();
		hideSoftKeyboard(BaseActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideSoftKeyboard(BaseActivity.this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Catching exceptn (including permission) & send to handler for bette handeling
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		activity = BaseActivity.this;
		mContext = activity.getApplicationContext();
		networkConnectivity = new NetworkConnectivity(this);

		//lise wise can use in all act -if extends base act
		//libDB = new LibDB(this);
		//libServer = new LibServer(this);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Processing Data . .");
		pDialog.setCancelable(false);


		connectivity_checker();
	}

	public void showDialogBox() {
		LinearLayout ll = new LinearLayout(BaseActivity.this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(BaseActivity.this);
		ll.setPadding(10, 10, 10, 10);
		tv.setText("No internet Connection Detected.Please connect to Internet in order to access Application");
		ll.addView(tv);
		tv.setPadding(10, 10, 10, 10);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
		alertDialogBuilder.setView(ll);
		alertDialogBuilder.setTitle("No Internet Connection");
		alertDialogBuilder.setPositiveButton("TURN ON", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				activity.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				activity.finish();
			}
		});
		alertDialogBuilder.show();
	}

	public Boolean connectivity_checker() {
		if (networkConnectivity.isConnected()) {
			return true;
		} else {
			showDialogBox();
			return false;
		}
	}

	public Toolbar getToolbar() {
		return toolbar;
	}


	/**
	 * Shows the soft keyboard
	 */
	public void showSoftKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		view.requestFocus();
		inputMethodManager.showSoftInput(view, 0);
	}

	public void showProgressDialog() {
		pDialog.show();
		hideSoftKeyboard(BaseActivity.this);
	}


	public void showProgressDialog(String message) {
		pDialog.setMessage(message);
		pDialog.show();
		hideSoftKeyboard(BaseActivity.this);

	}

	public void dismissProgressDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
		hideSoftKeyboard(BaseActivity.this);
	}

	public Toolbar setToolbarWithoutArrow(String title) {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		toolbar.setTitleTextColor(getResources().getColor(R.color.white));
		return toolbar;
	}

	public Toolbar setToolbar(String title) {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		toolbar.setTitleTextColor(getResources().getColor(R.color.white));
		toolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_24dp);
		toolbar.setSubtitleTextColor(Color.WHITE);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseActivity.this.onBackPressed();
			}
		});
		return toolbar;
	}


	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}



}
