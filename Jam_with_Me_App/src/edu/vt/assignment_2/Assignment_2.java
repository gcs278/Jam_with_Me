package edu.vt.assignment_2;

import android.app.Application;
import android.content.Context;

// This class is used for any global variables
public class Assignment_2 extends Application{
	// Port Global Variable
	// Set to 8888 by default
	private static int port = 8888;
	private static String url = "grantspence.com";
	private static Context s_instance;
	
	public Assignment_2() {
        s_instance = this;
    }

	
	// Port Getter
	public static int getPort() {
		return port;
	}
	
	// Port Setter
	public static void setPort(int value) {
		port = value;
	}
	
	// Url Getter
	public static String getURL() {
		return url;
	}
	
	// URL setter
	public static void setURL(String value) {
		url = value;
	}
	
	// The following is code for getting a resource string
	public static Context getContext(){
        return s_instance;
    }
	public static String getResourceString(int resId){
        return getContext().getString(resId);       
    }
	
	// Unify error message
	public static String getErrorMessage() {
		return "Error: Could not connect. Please check the URL and port settings. Message: ";
	}
}
