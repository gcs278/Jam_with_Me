package edu.vt.assignment_2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

// This network activty retrieves and updates the profile picture
class NetworkGetPicture extends AsyncTask<String, Void, String> {
	Context activity;
	int port;
	String URL;

	public NetworkGetPicture(Context context) {
		activity = context;
		// Get the URL and port
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String imageBase64 = "";
		try {
			// Set up HTTP GET
			HttpClient httpclient = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/uploadpic",
					"email=" + MainActivity.email, null);
			
			// Excecute
			HttpResponse response = httpclient.execute(new HttpGet(address));
			
			// Check status
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// Get the image
				imageBase64 = out.toString();
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
		return imageBase64;
	}

	// Process data, display
	@Override
	protected void onPostExecute(String result) {
		// check if it didn't fail
		if (result != "connectFail") {
			try {
				// Decode and set image to profile pic
				byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);
				MainActivity.profilePic.setImageBitmap(decodedByte);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

	}
}
