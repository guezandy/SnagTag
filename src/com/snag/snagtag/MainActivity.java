package com.snag.snagtag;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;


public class MainActivity extends BaseSherlockeFragmentActivity  {

	public static final String TAG = MainActivity.class.getSimpleName();
	
	/**
	 * Configuration parameters for Android's AbstractAuthenticator
	 */
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_AUTHTOKENTYPE="authtokenType";
	public static final String PARAM_LOGIN_TYPE_KINVEY = "kinvey";
	private AccountManager mAccountManager;	
	private ProgressDialog mProgressDialog = null;
	public static final int MIN_USERNAME_LENGTH = 8;
	public static final int MIN_PASSWORD_LENGTH = 4;
	public static final String appKey = "kid_PVAtuuzi2f";
	public static final String appSecret = "2cab4a07424945e981478fcfc02341af";
	private Boolean mConfirmCredentials = false;
    public static final String AUTHTOKEN_TYPE = "com.kinvey.myapplogin";
    public static final String ACCOUNT_TYPE = "com.kinvey.myapplogin";
    public static final String LOGIN_TYPE_KEY = "loginType";

	protected Client kinveyClient;
	
	protected Button mButtonLogin;
	protected EditText mEditUserEmail;
	protected EditText mEditPassword;
	protected TextView mErrorMessage;
	protected String mUserEmail;
	protected String mPassword;
	
	protected Boolean mRequestNewAccount = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		kinveyClient = new Client.Builder(appKey, appSecret
			    , this.getApplicationContext()).build();
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAccountManager = AccountManager.get(this);
		final Intent intent = getIntent();
		mUserEmail = intent.getStringExtra(PARAM_USERNAME);

		mRequestNewAccount = (mUserEmail == null);
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS,false);

		setContentView(R.layout.activity_main);

		//mErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
		mEditUserEmail = (EditText) findViewById(R.id.usernamee);
		mEditPassword = (EditText) findViewById(R.id.passwordd);
    	//mButtonLogin = (Button) findViewById(R.id.loginButton);
    	
    	final Button bregister = (Button) findViewById(R.id.register);
        bregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerAccount();
            }
        });
    	
        final Button blogin = (Button) findViewById(R.id.loginButton);
        blogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
        final Button bskip = (Button) findViewById(R.id.skipButton);
        bskip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                skipToMenu();
            }
        });
        
//        final Button bfblogin = (Button) findViewById(R.id.btnLoginFacebook);
//        bfblogin.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                submitFacebook();
//            }
//        });


	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Authenticating...");
		dialog.setIndeterminate(true);
		
		mProgressDialog = dialog;
		return dialog;		
	}
	
	/**
	 * Method to handle Login button clicks - gets Username and Password and calls User Login method.  
	 */
	public void login() {
		System.out.println("Made it inside!");
		if(validateFields()) {
			if (mRequestNewAccount) {
				mUserEmail = mEditUserEmail.getText().toString();
			}
			mPassword = mEditPassword.getText().toString();
			if (TextUtils.isEmpty(mUserEmail) || TextUtils.isEmpty(mPassword)) {
				mErrorMessage.setText("Please enter a valid username and password.");
			} else {
				//showProgress();
				System.out.println("about to fit userLogin");
				userLogin();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "Please Insert Username and Password", Toast.LENGTH_SHORT).show();
		}
	}
	private boolean validateFields() {
		if (mEditUserEmail.length()>0 && mEditPassword.getText().length()>0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	public void launchTwitterLogin(View view) {
		MainActivity.this.startActivity(new Intent(MainActivity.this, TwitterLoginActivity.class));
        MainActivity.this.finish();
	}
	*/
	//public void launchFacebookLogin(View view) {
    //    Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
    //   startActivity(intent);
    //   System.out.println("WE out of the main;");
    //    //MainActivity.this.finish();
	//}
	
	
	public void skipToMenu() {
		MainActivity.this.startActivity(new Intent(MainActivity.this, MainMenu.class));
        MainActivity.this.finish();
	}
	/*
	public void launchGoogleLogin(View v){
		LoginActivity.this.startActivity(new Intent(LoginActivity.this, GoogleLoginActivity.class));
        LoginActivity.this.finish();
	}
	*/
	public void registerAccount() {

		Intent intent = new Intent(this, RegisterNewAccountActivity.class);
        startActivity(intent);
	}
	
	/**
	 * Called as a result of a Kinvey Authentication if credentials needed to be confirmed 
	 * (needed for Android Account Manager in case credentials change/expire.)
	 */
	private void finishConfirmCredentials(boolean result) {
		final Account account = new Account(mUserEmail, ACCOUNT_TYPE);
		mAccountManager.setPassword(account, mPassword);
		mAccountManager.setUserData(account, LOGIN_TYPE_KEY, PARAM_LOGIN_TYPE_KINVEY);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/**
	 * Finishes the login process by creating/updating the account with the Android
	 * AccountManager.  
	 */
	private void finishLogin(String authToken) {
		final Account account = new Account(mUserEmail, ACCOUNT_TYPE);
		if (mRequestNewAccount) {
			Bundle userData = new Bundle();
			userData.putString(LOGIN_TYPE_KEY, PARAM_LOGIN_TYPE_KINVEY);
			mAccountManager.addAccountExplicitly(account, mPassword, userData);
		} else {
			mAccountManager.setPassword(account, mPassword);
			mAccountManager.setUserData(account, LOGIN_TYPE_KEY, PARAM_LOGIN_TYPE_KINVEY);
		}
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUserEmail);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
//		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK,intent);
		finish();
	}
	
	/**
	 * Called following a successful KinveyLogin to process the result and persist to AccountManager
	 */
	public void onAuthenticationResult(String authToken) {
		boolean success = ((authToken != null) && (authToken.length()>0));
		hideProgress();
		
		if(success) {
			if (!mConfirmCredentials) {
				finishLogin(authToken);
			} else {
				finishConfirmCredentials(success);
			}
		} else {
			if (mRequestNewAccount) {
				mErrorMessage.setText("Please enter a valid username or password");
			} else {
				mErrorMessage.setText("Please enter a valid password");
			}
			
		}
	}
	
	
	// TODO:  Fix ShowDialog
	@SuppressWarnings("deprecation")
	public void showProgress() {
		showDialog(0);
	}
	
	private void hideProgress() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	

	 //  Method to log the twitter Kinvey user, passing a KinveyCallback.  
	 
	public void userLogin() {
		System.out.println("Before Client is called!");
		kinveyClient.user().login(mEditUserEmail.getText().toString().toLowerCase() , mEditPassword.getText().toString(), new KinveyUserCallback() {
            public void onFailure(Throwable t) {
                CharSequence text = "Wrong username or password";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                //onAuthenticationResult(null);
            }

            public void onSuccess(User u) {
                CharSequence text = "Welcome "+ u.get("username");
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                //onAuthenticationResult(u.getId());
                System.out.println("Made it to onsuccess before launch new intent!");
                MainActivity.this.startActivity(new Intent(MainActivity.this, MainMenu.class));
                MainActivity.this.finish();
            }

        });
	}
//	   public void submitFacebook(){
//	        // The FB SDK has a bit of a delay in response
//	        final ProgressDialog progressDialog = ProgressDialog.show(
//	                MainActivity.this, "Connecting to Facebook",
//	                "Logging in with Facebook - just a moment");
//	        System.out.println("We made it inside the submitfacebook function");
//	        Session.openActiveSession(this, true, new Session.StatusCallback() {
//	            @Override
//	            public void call(Session session, SessionState state, Exception exception) {
//	                if (exception == null) {
//	                    if (state.equals(RESULT_CANCELED)) {
//	                        Toast.makeText(MainActivity.this, "FB login cancelled",
//	                               Toast.LENGTH_LONG).show();
//	                     } else if (state.isOpened()) {
//	                        if (progressDialog != null && progressDialog.isShowing()) {
//	                               progressDialog.dismiss();
//	                         }
//	                           Toast.makeText(MainActivity.this, 
//	                                   "Logged in with Facebook.", Toast.LENGTH_LONG).show();
//	                           loginFacebookKinveyUser(progressDialog, 
//	                                   session.getAccessToken());
//	                      }
//	                } else {
//	                     error(progressDialog, exception.getMessage());
//	                   }
//	            }
//
//				private void error(ProgressDialog progressDialog, String message) {
//					// TODO Auto-generated method stub
//					
//				}
//	           });
//	    }
//	    private void loginFacebookKinveyUser(final ProgressDialog progressDialog, 
//	            String accessToken) {
//	        kinveyClient.user().loginFacebook(accessToken, new KinveyUserCallback() {
//	            @Override
//	            public void onFailure(Throwable e) {
//	                error(progressDialog, "Kinvey: " + e.getMessage());
//	                Log.e(TAG, "failed Kinvey facebook login", e);
//	            }
//	            private void error(ProgressDialog progressDialog, String string) {
//					Toast.makeText(getApplicationContext(), "IN KinveyloginFailure", Toast.LENGTH_SHORT).show();
//					
//				}
//				@Override
//	            public void onSuccess(User u) {
//	                Log.d(TAG, "successfully logged in with facebook");
//					Toast.makeText(getApplicationContext(), "IN KinveyloginSuccess", Toast.LENGTH_SHORT).show();
//
//	            }
//	        });
//	    }
//	
	
	
}