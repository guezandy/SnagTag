package com.snag.snagtag;
/*
 * Copyright (c) 2013 Kinvey Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserManagementCallback;
import com.kinvey.java.User;
//import com.stripe.android.*;
/**
 * Activity for registering new accounts.  This prompts a user for a user name (email address),
 * First and Last name, and a password. After validation, it creates a new KinveyUser and redirects 
 * the user back to the login page for Authentication.
 */
public class RegisterNewAccountActivity extends Activity {

	public static final String TAG = MainActivity.class.getSimpleName();
	
	protected Client kinveyClient;
	protected EditText mEditFirstName;
	protected EditText mEditLastName;
	protected EditText mEditEmailAddress;
	protected EditText mEditPassword;
	protected EditText mEditPasswordConfirm;
	protected EditText mcardnum;
	protected EditText mcvc;
	protected EditText mexpmonth;
	protected EditText mexpyear;
	protected EditText mfname;
	protected EditText mlname;
	protected Button mRegisterAccount;
	public static final String appKey = "kid_PVAtuuzi2f";
	public static final String appSecret = "2cab4a07424945e981478fcfc02341af";
	protected EditText cardnumber;
	protected EditText cvc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register_new_account);
		
		mEditFirstName = (EditText) findViewById(R.id.fullName);
		mEditLastName = (EditText) findViewById(R.id.lname);
		mEditEmailAddress = (EditText) findViewById(R.id.unEmail);
		mEditPassword = (EditText) findViewById(R.id.pass);
		mEditPasswordConfirm = (EditText) findViewById(R.id.comPass);
		mcardnum = (EditText) findViewById(R.id.cardnum);
		mcvc = (EditText) findViewById(R.id.cvc);
		mexpmonth = (EditText) findViewById(R.id.expmonth);
		mexpyear = (EditText) findViewById(R.id.expyear);
		mRegisterAccount = (Button) findViewById(R.id.registerButton);
		kinveyClient = new Client.Builder(appKey, appSecret
			    , this.getApplicationContext()).build();
	}
	
	 public void registerAccount(View view) {
		if (validateFields()) {
			if (validatePasswordMatch()) {
				processSignup(view);
			} else {
				Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
			} 
		} else {
			Toast.makeText(this, "Fields not filled in", Toast.LENGTH_SHORT).show();
		}
	}
	
	// TODO:  Implement Text Listeners to handle this
	private boolean validateFields() {
		if (mEditFirstName.getText().length()>0 && mEditLastName.getText().length()>0 
				&& mEditEmailAddress.length()>0 && mEditPassword.getText().length()>0 
				&& mEditPasswordConfirm.getText().length()>0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validatePasswordMatch() {
		if (mEditPassword.getText().toString().equals(mEditPasswordConfirm.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}
	 public void submit(View view) {
		 kinveyClient.user().retrieve(new KinveyUserManagementCallback() {
		        @Override
		        public void onFailure(Throwable e) { 
		        	Toast.makeText(getApplicationContext(), "Cannot Retreieve User", Toast.LENGTH_SHORT).show();
		        }
		        @Override
		        public void onSuccess(Void unused) { 
		        	Toast.makeText(getApplicationContext(), "User Retreived", Toast.LENGTH_SHORT).show();
		        	
		        }
		    });
         kinveyClient.user().sendEmailVerification(new KinveyUserManagementCallback() {
            @Override
         public void onFailure(Throwable e) { 
	        	Toast.makeText(getApplicationContext(), "Cannot Send Email Verification", Toast.LENGTH_SHORT).show();
            }
         @Override
         public void onSuccess(Void unused) { 
	        	Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT).show();

         }
         });
     }
	
	/**
	 * Calls the createUserWithUsername method of the KinveyClient to create a new user.
	 * After sign-up is successful, the First and Last name are added to the user, and
	 * an Intent is instantiated to bring the user back to the login page to confirm
	 * authentication.  
	 * 
	 * Note that a user is not Authorized here to the Android Account Manager, but rather
	 * is added to AccountManager upon successful authentication.  
	 */
	public void processSignup(View view) {
		Toast.makeText(this, "Creating user...", Toast.LENGTH_SHORT).show();
	    kinveyClient.user().create(mEditEmailAddress.getText().toString().toLowerCase(), mEditPassword.getText().toString(), new KinveyUserCallback() {
            @Override
	    	public void onFailure(Throwable t) {
                CharSequence text = "Could not sign up -> " + t.getMessage();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-up error", t);
            }
			@Override
            public void onSuccess(User u) {
                u.put("email", u.get("username"));
                u.put("firstname", mEditFirstName.getText().toString());
                u.put("lastname", mEditLastName.getText().toString());
                u.put("cardnumber", mcardnum.getText().toString());
                u.put("cvv", mcvc.getText().toString());
                u.put("Expmonth", mexpmonth.getText().toString());
                u.put("expyear", mexpyear.getText().toString());
                CharSequence text = "Welcome, " + u.get("firstname") + ".  Your account has been registered.  Please login to confirm your credentials.";

                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                RegisterNewAccountActivity.this.startActivity(new Intent(RegisterNewAccountActivity.this, MainActivity.class));
                RegisterNewAccountActivity.this.finish();

            }
        });
	}
}
