package edu.vt.assignment_2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

// Network task to register a user
class NetworkPostRegister extends AsyncTask<String, Void, String> {
	// Context to display diaglog
	Context activity;
	ProgressDialog pd; // Loading Dialog
	String email, name, passwordSHA256, instrument; // Register data
	String URL;
	int port;

	public NetworkPostRegister(Context context, String instrument,
			String password) {
		activity = context;
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();

		// Get register Data
		email = RegisterActivity.emailInput.getText().toString();
		name = RegisterActivity.nameInput.getText().toString();
		this.instrument = instrument;
		passwordSHA256 = password;
	}

	// Let user know its updating
	@Override
	protected void onPreExecute() {
		// Display a loading widget to keep user happy
		pd = new ProgressDialog(activity);
		pd.setTitle("Registering...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String responseString = "";
		try {
			// Create HTTP Object
			HttpClient httpclient = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/register", null,
					null);

			HttpPost post = new HttpPost(address);

			// Create JSON object for post data
			JSONObject json = new JSONObject();
			json.put("username", email);
			json.put("password", passwordSHA256);
			json.put("name", name);
			json.put("instrument", instrument);
			System.out.println(json.toString());

			// Add JSON to post and execute
			post.setEntity(new StringEntity(json.toString()));
			HttpResponse response = httpclient.execute(post);

			// Check Status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				// Get the response from the server
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get Response
				responseString = out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				return statusLine.getReasonPhrase();
			}
		} catch (ClientProtocolException e) {
			return responseString = e.getMessage();
		} catch (IOException e) {
			return responseString = e.getMessage();
		} catch (JSONException e) {
			return responseString = e.getMessage();
		} catch (URISyntaxException e) {
			return responseString = e.getMessage();
		}
		return responseString;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		pd.dismiss();
		// Check if failed
		if (result.contains("FAIL")) {
			new AlertDialog.Builder(activity).setMessage(
					"Whoops, that name is taken already").show();
		} else if (result.contains("SUCCESS")) {

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Start user's profile
					Intent user = new Intent(activity, LoginActivity.class);
					activity.startActivity(user);
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("You have successfully registered! You will need to log back in. Leave Password blank.")
					.setPositiveButton("Got it!", dialogClickListener).show();
		} else {
			// Display error
			new AlertDialog.Builder(activity).setMessage(
					Assignment_2.getErrorMessage() + result).show();
		}

	}
}
