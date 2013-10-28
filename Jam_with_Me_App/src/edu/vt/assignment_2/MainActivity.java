package edu.vt.assignment_2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.example.assignment_2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {
	// Profile views
	static ImageView profilePic;
	static TextView nameView, instrumentView, emailView, latitudeView,
			longitudeView;
	// Buttons
	Button btnPlay;
	Button btnRecord;
	// Progress Bar variables
	ProgressBar progressBar;
	CountDownTimer recordTime;
	static CountDownTimer playTime;
	int RECORD_LIMIT = 5000;
	static int i = 0;
	// Email, other activities access
	public static String email;
	// Recording sound variables
	MediaRecorder mRecorder = null;
	String mFileName = null;
	MediaPlayer mPlayer = null;
	boolean playPrepared = true;
	boolean recordingPrepared = true;
	// Profile list variables
	static ArrayList<String> listItems = new ArrayList<String>();
	static ListView profileListView;
	static ArrayAdapter<String> adapter;
	// Admin or friend
	static String viewType = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		// Get Intent
		email = getIntent().getExtras().getString("email");
		viewType = getIntent().getExtras().getString("viewType");

		// Set the views, buttons and progress bar
		nameView = (TextView) findViewById(R.id.nameView);
		profilePic = (ImageView) findViewById(R.id.profilePic);
		instrumentView = (TextView) findViewById(R.id.instrumentView);
		emailView = (TextView) findViewById(R.id.emailView);
		latitudeView = (TextView) findViewById(R.id.latitudeView);
		longitudeView = (TextView) findViewById(R.id.longitudeView);
		btnPlay = (Button) findViewById(R.id.btnPlay2);
		btnRecord = (Button) findViewById(R.id.btnRecord);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setMax(50);
		progressBar.setProgress(i);
		emailView.setText(email);

		if (!viewType.equals(new String("admin"))) {
			((Button) findViewById(R.id.btnPicture)).setEnabled(false);
		}

		// Create a record timer for progress bar
		recordTime = new CountDownTimer(RECORD_LIMIT, 100) {
			@Override
			public void onTick(long millisUntilFinished) {
				i++;
				progressBar.setProgress(i);
			}

			@Override
			public void onFinish() {
				i = 0;
				progressBar.setProgress(i);
				btnRecord.performClick();
			}
		};

		// Audio filename
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/audiorecordtest.3gp";

		// Set listner for play button
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPlay(playPrepared);
				if (playPrepared) {
					btnPlay.setText("Stop playing");
				} else {
					btnPlay.setText("Start playing");
				}
				playPrepared = !playPrepared;
			}
		});

		// Set listner for record button
		btnRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onRecord(recordingPrepared);
				if (recordingPrepared) {
					btnRecord.setText("Stop recording");
				} else {
					btnRecord.setText("Start recording");
				}
				recordingPrepared = !recordingPrepared;
			}
		});

		// Set up the listview for profiles
		profileListView = (ListView) findViewById(R.id.profilesListView);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		profileListView.setAdapter(adapter);
		profileListView.setOnItemClickListener(this);
		// Get the users and add
		NetworkGetUsers task = new NetworkGetUsers(this);
		task.execute("");
	}

	// Back key
	@Override
	public void onBackPressed() {

		// If they are at own profile, logout
		if (viewType.equals(new String("admin"))) {
			System.out.println("Here");
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Intent Act2Intent = new Intent(MainActivity.this,
								LoginActivity.class);
						startActivity(Act2Intent);
						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Logout?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();

		}

		else {
			super.onBackPressed();
		}

	}

	// When user presses play button
	private void onPlay(boolean start) {
		if (start)
			startPlaying();
		else
			stopPlaying();
	}

	// When user presses record button
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	// Start recording
	private void startRecording() {
		// Check if microphone is available
		PackageManager pm = this.getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
			new AlertDialog.Builder(this).setMessage(
					"Error: No microphone available").show();
			recordingPrepared = true;
			return;
		}

		// Create a media recorder with speficied format
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		// Attempt to prepare it
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			new AlertDialog.Builder(this)
					.setMessage(
							"Error: Cannot prepare recorder. Might be external storage error or microphone access")
					.show();
			recordingPrepared = true;
			return;
		}

		// Start
		progressBar.setMax(RECORD_LIMIT / 100);
		recordTime.start();
		mRecorder.start();
	}

	// Stop recrdoing
	private void stopRecording() {
		recordTime.cancel();
		progressBar.setProgress(0);
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		// Upload sound to database
		NetworkPostSound task = new NetworkPostSound(this);
		task.execute(mFileName);
	}

	// Start playing sound
	private void startPlaying() {
		// First check if file exists
		File file = new File(mFileName);
		if (!file.exists()) {
			new AlertDialog.Builder(this)
					.setMessage(
							"File cannot be located: Try recording something first or external storage could be disabled")
					.show();
			playPrepared = true;
			return;
		}

		// Create a new media player
		mPlayer = new MediaPlayer();
		// Listener that changes play button back
		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				btnPlay.setText("Start playing");
				playPrepared = true;
			}
		});

		try {
			// Set the source, prepare
			mPlayer.setDataSource(mFileName);
			i = 0;
			mPlayer.prepare();

			// Set up the progress bar
			progressBar.setMax(mPlayer.getDuration() / 100);
			playTime = new CountDownTimer(mPlayer.getDuration(), 100) {
				@Override
				public void onTick(long millisUntilFinished) {
					i++;
					progressBar.setProgress(i);
				}

				@Override
				public void onFinish() {
					i = 0;
					progressBar.setProgress(i);
				}
			};

			// Start progress bar and playing sound
			playTime.start();
			mPlayer.start();
		} catch (IOException e) {
			new AlertDialog.Builder(this)
					.setMessage("Error: " + e.getMessage()).show();
		}

	}

	// Stop playing sound
	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		// Release the recorder or player
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	@Override
	protected void onResume() {
		// Get picture
		NetworkGetPicture pictureTask = new NetworkGetPicture(this);
		pictureTask.execute("");

		// Get profile data
		NetworkGetProfileInfo profileTask = new NetworkGetProfileInfo(this);
		profileTask.execute("");
		super.onResume();
	}

	public void takePicture(View view) {
		Intent pic = new Intent(this, CameraActivity.class);
		startActivity(pic);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		Log.i("HelloListView", "You clicked Item: " + id + " at position:"
				+ position);
		// Then you start a new Activity via Intent
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);

		intent.putExtra("email", ((TextView) v).getText());
		intent.putExtra("viewType", "friend");
		startActivityForResult(intent, RESULT_OK);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		return super.onOptionsItemSelected(item);
	}

}
