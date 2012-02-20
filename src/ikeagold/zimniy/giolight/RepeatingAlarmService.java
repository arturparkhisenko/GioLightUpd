package ikeagold.zimniy.giolight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RepeatingAlarmService extends BroadcastReceiver {

	public String glvn;
	public String glvc;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "It's Service Time!", Toast.LENGTH_LONG).show();
		
		String cleanstr3 = "";
		try {
			Process ifc = Runtime.getRuntime().exec("getprop ro.light.version");
			BufferedReader bis = new BufferedReader(new InputStreamReader(
					ifc.getInputStream()));
			cleanstr3 = bis.readLine();
			ifc.destroy();
		} catch (java.io.IOException e) {
		}
		glvc = cleanstr3;
		glvn = DownloadText("http://gio-light.googlecode.com/hg/version.testing.txt");

		if (glvc.trim().equalsIgnoreCase(glvn.trim())) {
		} else {
			if (Integer.parseInt(glvc.trim()) < Integer.parseInt(glvn.trim())) {
				Toast.makeText(context, "Новая версия есть", Toast.LENGTH_LONG).show();
			} else {
			}
		}
		
		
		Log.v(this.getClass().getName(),
				"Timed alarm onReceive() started at time: "
						+ new java.sql.Timestamp(System.currentTimeMillis())
								.toString());
	}
	

	private InputStream OpenHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			throw new IOException("Error connecting");
		}
		return in;
	}

	private String DownloadText(String URL) {
		int BUFFER_SIZE = 2000;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		InputStreamReader isr = new InputStreamReader(in);
		int charRead;
		String str = "";
		char[] inputBuffer = new char[BUFFER_SIZE];
		try {
			while ((charRead = isr.read(inputBuffer)) > 0) {
				// convert the chars to a String
				String readString = String
						.copyValueOf(inputBuffer, 0, charRead);
				str += readString;
				inputBuffer = new char[BUFFER_SIZE];
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return str;
	}
		
}