package edu.vt.assignment_2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.assignment_2.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

// This activity is for registering a user
public class RegisterActivity extends Activity {
	// Fields for input
	static EditText emailInput, nameInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Set text edits
		emailInput = (EditText) findViewById(R.id.regEmailInput);
		nameInput = (EditText) findViewById(R.id.regNameInput);

		// Hid keyboard
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	// When user hits submit button
	public void submit(View view) {
		
		if(nameInput.getText().toString().isEmpty()) {
			new AlertDialog.Builder(this).setMessage(
					"Error: Please enter a name").show();
			return;
		} else if (emailInput.getText().toString().isEmpty()) {
			new AlertDialog.Builder(this).setMessage(
					"Error: Please enter an email address").show();
			return;
		}
		
		// Get the radiogroup and selected button
		RadioGroup instrumentGroup = (RadioGroup) findViewById(R.id.radioInstrument);
		int checkedId = instrumentGroup.getCheckedRadioButtonId();
		if (checkedId == -1) {
			new AlertDialog.Builder(this).setMessage(
					"Error: Please select an instrument").show();
			return;
		}
		RadioButton selectedButton = (RadioButton) findViewById(checkedId);

		// Attempt at SHA256ing the password, not in use
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		EditText password = (EditText) findViewById(R.id.passwordRegView);

		byte[] hash = digest.digest(password.getText().toString().getBytes());
		String passwordBase64 = null;
		try {
			passwordBase64 = new String(hash, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Register the user with the database
		NetworkPostRegister task = new NetworkPostRegister(this, selectedButton
				.getText().toString(), passwordBase64);
		task.execute("");
	}
}
