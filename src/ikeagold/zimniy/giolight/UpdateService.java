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
		String sint = prefs.getString("updateInterval", "0");
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

	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand();
		return START_REDELIVER_INTENT;
	}

	private void handleCommand() {

		new Thread() {
			public void run() {

				// If we're in one of our list activities, we don't want
				// to run an update because the database will be out of
				// sync with the display.
				if (((FDroidApp) getApplication()).inActivity != 0)
					return;

				// See if it's time to actually do anything yet...
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				long lastUpdate = prefs.getLong("lastUpdateCheck", 0);
				String sint = prefs.getString("updateInterval", "0");
				int interval = Integer.parseInt(sint);
				if (interval == 0)
					return;
				if (lastUpdate + (interval * 60 * 60) > System
						.currentTimeMillis())
					return;

				// Do the update...
				DB db = null;
				try {
					db = new DB(getBaseContext());
					boolean notify = prefs.getBoolean("updateNotify", false);

					// Get the number of updates available before we
					// start, so we can notify if there are new ones.
					// (But avoid doing it if the user doesn't want
					// notifications, since it may be time consuming)
					int prevUpdates = 0;
					if (notify)
						prevUpdates = db.getNumUpdates();

					boolean success = RepoXMLHandler.doUpdates(
							getBaseContext(), db);

					if (success && notify) {
						int newUpdates = db.getNumUpdates();
						Log.d("FDroid", "Updates before:" + prevUpdates
								+ ", after: " + newUpdates);
						if (newUpdates > prevUpdates) {
							NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							Notification notification = new Notification(
									R.drawable.icon,
									"FDroid Updates Available",
									System.currentTimeMillis());
							Context context = getApplicationContext();
							CharSequence contentTitle = "FDroid";
							CharSequence contentText = "Updates are available.";
							Intent notificationIntent = new Intent(
									UpdateService.this, FDroid.class);
							PendingIntent contentIntent = PendingIntent
									.getActivity(UpdateService.this, 0,
											notificationIntent, 0);
							notification.setLatestEventInfo(context,
									contentTitle, contentText, contentIntent);
							notification.flags |= Notification.FLAG_AUTO_CANCEL;
							n.notify(1, notification);
						}
					}

				} catch (Exception e) {
					Log.e("FDroid",
							"Exception during handleCommand():\n"
									+ Log.getStackTraceString(e));
				} finally {
					if (db != null)
						db.close();
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
