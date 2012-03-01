package ikeagold.zimniy.giolight;

import ikeagold.zimniy.giolight.GioLightUpdActivity;
import ikeagold.zimniy.giolight.R;
import ikeagold.zimniy.giolight.Preferences;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.content.res.Resources;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;


public class UpdateService extends Service {
	
	

	public static void schedule(Context context) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		boolean Sync;
		Sync = prefs.getBoolean("synckey", false);
		if (Sync == false) {
			// Nothing
		} else {
			String sint = prefs.getString("updateInterval", "24");
			int interval = Integer.parseInt(sint);
			Intent intent = new Intent(context, UpdateService.class);
			PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(pending);
			if (interval > 0) {
				alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime() + 5000,
						AlarmManager.INTERVAL_HOUR, pending);
			}
		}

	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand();
		return START_REDELIVER_INTENT;
	}

	// MAIN DEV SECTION
	private void handleCommand() {

		new Thread() {
			public void run() {
				
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				
				boolean Sync;
				Sync = prefs.getBoolean("synckey", false);
				if (Sync == false)
					return;
				
				//

				long lastUpdate = prefs.getLong("lastUpdateCheck", 0);
				String sint = prefs.getString("updateInterval", "24");
				int interval = Integer.parseInt(sint);
				if (interval == 0)
					return;
				if (lastUpdate + (interval * 60 * 60) > System
						.currentTimeMillis())
					return;

				// Do the update...
				
				try {
					
					
					// AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
					
				} catch (Exception e) {
					Log.e("WTF",
							"Exception during handleCommand():\n"
									+ Log.getStackTraceString(e));
				} finally {
					stopSelf();
				}
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
