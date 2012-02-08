package ikeagold.zimniy.giolight;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GioLightUpdActivity extends Activity {
	//public vars
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    public String fn;
    public String fnt;
    public String glv;

    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView tv10 = (TextView) findViewById(R.id.textView10);
		String cleanstr3 = android.os.Build.DISPLAY.substring(6, 8);
		glv = "Билд - " + cleanstr3 + "\n";
		tv10.setText(glv);
	}
	
	//download buttons ZONE
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Загружаю в /sd-card/Light/...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog; 
            default:
                return null;
        }
    }

	private class DownloadFile extends AsyncTask<String, String, String>{
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

	    @Override
	    protected String doInBackground(String... url) {
	        int count;
	        try {
	            URL url1 = new URL(url[0]);
	            URLConnection conexion = url1.openConnection();
	            conexion.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int lenghtOfFile = conexion.getContentLength();
	            // download the file
	            InputStream input = new BufferedInputStream(url1.openStream());
	            OutputStream output = new FileOutputStream("/sdcard/Light/1.zip");

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress
	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	        } catch (Exception e) {}
	        return null;
	    }
	    protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
       }

       @Override
       protected void onPostExecute(String unused) {
           dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
       }

	}
   
	public boolean button5_Click(View v) {
		if (isInternetOn()) {
			String fntd = DownloadText("http://gio-light.googlecode.com/hg/version.testing.txt");
			String cleanstr1 = fntd.substring(0, 2);
			fnt = "GioLight" + cleanstr1 + ".zip";

			String furlt = DownloadText("http://gio-light.googlecode.com/hg/url.testing.txt");
			DownloadFile downloadFile = new DownloadFile();
			downloadFile.execute(furlt);

			String RootDir = Environment.getExternalStorageDirectory()
					+ File.separator + "Light";
			File from = new File(RootDir, ("1.zip"));
			File to = new File(RootDir, fnt);
			from.renameTo(to);

			return true;
		} else {
			Toast.makeText(this, "Интернета нет :(", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public boolean button6_Click(View v) {
		if (isInternetOn()) {
			String fnd = DownloadText("http://gio-light.googlecode.com/hg/version.txt");
			String cleanstr2 = fnd.substring(0, 2);
			fn = "GioLight" + cleanstr2 + ".zip";

			String furl = DownloadText("http://gio-light.googlecode.com/hg/url.txt");
			DownloadFile downloadFile = new DownloadFile();
			downloadFile.execute(furl);

			String RootDir = Environment.getExternalStorageDirectory()
					+ File.separator + "Light";
			File from = new File(RootDir, ("1.zip"));
			File to = new File(RootDir, fn);
			from.renameTo(to);

			return true;
		} else {
			Toast.makeText(this, "Интернета нет :(", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
   
	//download buttons ZONE

	public void button1_Click(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent i = new Intent(this, Alert.class);
			startActivity(i);
			break;
		}
	}

	public void button2_Click(View v) {
		switch (v.getId()) {
		case R.id.button2:
			Intent i = new Intent(this, Install.class);
			startActivity(i);
			break;
		}
	}

	public void button3_Click(View v) {
		switch (v.getId()) {
		case R.id.button3:
			Intent i = new Intent(this, Info.class);
			startActivity(i);
			break;
		}
	}

	// Update
	public boolean button4_Click(View v) {
		if (isInternetOn()) {
			
			String str1 = DownloadText("http://gio-light.googlecode.com/hg/version.testing.txt");
			TextView tv2 = (TextView) findViewById(R.id.textView2);
			tv2.setText("Билд - " + str1);

			String str3 = DownloadText("http://gio-light.googlecode.com/hg/version.txt");
			TextView tv4 = (TextView) findViewById(R.id.textView4);
			tv4.setText("Билд - " + str3);

			Toast.makeText(this, "Готово!", Toast.LENGTH_SHORT).show();

			return true;
		} else {
			Toast.makeText(this, "Интернета нет :(", Toast.LENGTH_SHORT).show();
			return false;
		}

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
				// ---convert the chars to a String---
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

	// check internet
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ARE WE CONNECTED TO THE NET
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			// MESSAGE TO SCREEN FOR TESTING (IF REQ)
			//Toast.makeText(this, "Готово! :)", Toast.LENGTH_SHORT).show();
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			// System.out.println(“Not Connected”);
			return false;
		}
		return false;
	}

	// MENU
	Menu myMenu = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.myMenu = menu;
		MenuItem item1 = menu.add(0, 1, 0, R.string.exit);
		item1.setIcon(R.drawable.exit);
		MenuItem item2 = menu.add(0, 2, 0, R.string.vote);
		item2.setIcon(R.drawable.vote);
		MenuItem item3 = menu.add(0, 3, 0, R.string.about);
		item3.setIcon(R.drawable.about);
		return true;
	}

	private void AboutDialog() {
		AlertDialog aboutDialog = new AlertDialog.Builder(this).create();
		aboutDialog.setTitle(R.string.about);
		aboutDialog.setMessage(getResources().getText(R.string.alasecond));
		aboutDialog.setButton(getResources().getText(R.string.close),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		aboutDialog.setIcon(R.drawable.about);
		aboutDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			finish();
			break;
		case 2:
			Intent marketIntent2 = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://market.android.com/details?id="
							+ getPackageName()));
			startActivity(marketIntent2);
			break;
		case 3:
			AboutDialog();
			return true;
		}
		return false;
	}
}