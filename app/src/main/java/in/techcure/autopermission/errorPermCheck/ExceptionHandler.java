package in.techcure.autopermission.errorPermCheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Jinesh Soni on 22/09/2016.
 */

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";

	public ExceptionHandler(Activity context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

		Toast.makeText(myContext,errorReport.toString(),Toast.LENGTH_LONG).show();
		//Catching error and sending fro Error recovery / auto permission
		//Dont forget to declare activity in manifest
		Intent intent = new Intent(myContext, ErrorRecoveryActvity.class);
		intent.putExtra("error", errorReport.toString());
		myContext.startActivity(intent);


		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

}

