package in.techcure.autopermission.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectivity {

	Context _context;
	int i;

	public NetworkConnectivity(Context context) {
		_context = context;
	}

	;

	public Boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo[] = connectivityManager.getAllNetworkInfo();
			for (i = 0; i < networkInfo.length; i++) {
				if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {

					return true;
				}
			}


		}
		return false;
	}


}