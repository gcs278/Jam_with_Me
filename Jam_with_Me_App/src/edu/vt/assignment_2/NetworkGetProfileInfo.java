package edu.vt.assignment_2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// This networking class retrieve profile data for a user
class NetworkGetProfileInfo extends AsyncTask<String, Void, String> {
	Context activity;
	int port;
	String URL;
	// JSON profile data string
	String profileData;

	public NetworkGetProfileInfo(Context context) {
		activity = context;
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			String query = "email=" + MainActivity.email + "&type=profile";
			URI address = new URI("http", null, URL, port, "/profile", query,
					null);
			// Execute
			HttpResponse response = httpclient.execute(new HttpGet(address));

			// Check status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get profile JSON string
				profileData = out.toString();
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
		return "OK";
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// Check if results OK
		if (result != "OK") {
			// Notify user that fail
			new AlertDialog.Builder(activity).setMessage(
					Assignment_2.getErrorMessage() + result).show();
		} else {
			// Parse JSON string and update profile
			try {
				JSONParser j = new JSONParser();
				JSONObject json = (JSONObject) j.parse(profileData);
				
						
				// Update profile
				MainActivity.nameView.setText((String) json.get("name"));
				MainActivity.instrumentView.setText((String) json
						.get("instrument"));
				MainActivity.longitudeView.setText((String) json
						.get("longitude"));
				MainActivity.latitudeView
						.setText((String) json.get("latitude"));

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}
}
