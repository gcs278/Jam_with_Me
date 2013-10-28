package edu.vt.assignment_2;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

// This task is for uploading a picture to the database
class NetworkPostPicture extends AsyncTask<String, Void, String> {
	Context activity;
	// Progress display
	ProgressDialog pd;
	int port;
	String URL;

	public NetworkPostPicture(Context context) {
		activity = context;
		port = Assignment_2.getPort();
		URL = Assignment_2.getURL();
	}

	// Let user know its updating
	@Override
	protected void onPreExecute() {
		// Display a loading widget to keep user happy
		pd = new ProgressDialog(activity);
		pd.setTitle("Uploading Picture...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
	}

	// Retrieve data
	@Override
	protected String doInBackground(String... params) {
		String verified = "OK";
		try {
			// Create the HTTP Post
			HttpClient client = new DefaultHttpClient();
			URI address = new URI("http", null, URL, port, "/uploadpic",
					"email=" + MainActivity.email, null);
			HttpPost request = new HttpPost(address);

			// Retrieve bytes from image files we just took
			File file = new File(params[0]);
			byte[] bytes = IOUtils.toByteArray(file.toURI());

			// Encode them into a BASE64 string
			byte[] encoded = Base64.encodeBase64(bytes);

			request.setEntity(new ByteArrayEntity(encoded));

			ResponseHandler<String> responsehandler = new BasicResponseHandler();
			client.execute(request, responsehandler);

		} catch (ClientProtocolException e) {
			return e.getMessage();
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
		// Close progress dialog
		pd.dismiss();
		if (result.contains("OK")) {
			new AlertDialog.Builder(activity).setMessage(
					"Picture successfully uploaded to database").show();
		} else {
			new AlertDialog.Builder(activity).setMessage(
					Assignment_2.getErrorMessage() + result).show();
		}

	}
}
