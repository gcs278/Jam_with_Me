package edu.vt.assignment_2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// This task gets a list of users availble from the database
class NetworkGetUsers extends AsyncTask<String, Void, String> {
	Context activity;
	int port;
	String URL;

	public NetworkGetUsers(Context context) {
		activity = context;
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String usersData = "";
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			String query = "type=users";
			URI address = new URI("http", null, URL, port, "/profile", query,
					null);
			// Execute
			HttpResponse response = httpclient.execute(new HttpGet(address));

			// Get Status line
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get users list
				usersData = out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (IOException e) {
			return "connectFail";
		} catch (URISyntaxException e) {
			return "connectFail";
		}
		return usersData;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// Make sure it worked
		if (result != "connectFail") {
			// Parse results and update GUI
			try {
				JSONParser j = new JSONParser();
				JSONObject json = (JSONObject) j.parse(result);
				JSONArray array = (JSONArray) json.get("users");

				// Update GUI
				MainActivity.listItems.clear(); // Delete old
				Collections.addAll(MainActivity.listItems,
						Arrays.copyOf(array.toArray(), array.toArray().length,
								String[].class)); // transfer new

				// Nofity to update
				MainActivity.adapter.notifyDataSetChanged();

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}