package edu.vt.assignment_2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.EditText;

// This task is to login a user
class NetworkPostLogin extends AsyncTask<String, Void, String> {
	Context activity;
	ProgressDialog pd;
	String email, URL;
	int port;
	// Long and lat for user location
	double longitude, latitude;

	public NetworkPostLogin(Context context) {
		activity = context;
		email = LoginActivity.emailInput.getText().toString();
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();

		// Get location to send to database
		LocationManager service = (LocationManager) activity
				.getSystemService(LoginActivity.LOCATION_SERVICE);

		// Make sure provider is enabled
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			// Show GPS allow popup
			Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			activity.startActivity(intent);
		}

		// Get logitude and latitude
		LocationManager lm = (LocationManager) activity
				.getSystemService(activity.LOCATION_SERVICE);
		Location location = lm
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
		}
	}

	// Let user know its updating
	@Override
	protected void onPreExecute() {
		// Display a loading widget to keep user happy
		pd = new ProgressDialog(activity);
		pd.setTitle("Logging in...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String verified = "FAIL";
		// Socket s = new Socket();
		try {
			// Set up HTTP POST
			HttpClient httpclient = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/login", null, null);
			HttpPost post = new HttpPost(address);

			// Get data ready
			JSONObject json = new JSONObject();
			json.put("username", email);
			json.put("password", "test");
			json.put("latitude", latitude);
			json.put("longitude", longitude);
			post.setEntity(new StringEntity(json.toString()));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(post);

			// Check status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get response
				verified = out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}

		} catch (IOException e) {
			return e.getMessage();
		} catch (URISyntaxException e) {
			return e.getMessage();
		}
		return verified;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
		// Check if authenticated or not
		if (result.contains("FAIL")) {
			new AlertDialog.Builder(activity).setMessage(
					"Sorry, you are not a registered user").show();
		} else if (result.contains("OK")) {
			// Start user's profile
			Intent user = new Intent(activity, MainActivity.class);
			user.putExtra("email", email);
			user.putExtra("viewType", "admin");
			activity.startActivity(user);
		} else {
			
			new AlertDialog.Builder(activity).setMessage(
					Assignment_2.getErrorMessage() + result).show();
		}
	}
}
