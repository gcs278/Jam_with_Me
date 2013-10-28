package edu.vt.assignment_2;

import com.example.assignment_2.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

// This activity is for logging in a user
public class LoginActivity extends Activity {
	static EditText emailInput, passwordInput;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_login);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Get EditTexts
		emailInput = (EditText) findViewById(R.id.emailInput);
		passwordInput = (EditText) findViewById(R.id.passwordInput);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// DO nothing
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// Attempt to log the user in
	public void login(View view) {
		NetworkPostLogin loginTask = new NetworkPostLogin(this);
		loginTask.execute("");

	}

	// If user needs to register, start another activity
	public void register(View view) {
		Intent register = new Intent(this, RegisterActivity.class);
		startActivity(register);
	}
}
