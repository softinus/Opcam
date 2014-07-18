package us.opcam.camera.activity;

import us.opcam.camera.R;
import us.opcam.camera.R.id;
import us.opcam.camera.R.layout;
import us.opcam.camera.R.string;
import us.opcam.camera.util.SPUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignActivity extends Activity
{
	private ProgressDialog LoadingDL;	// 프로그레스 다이어로그
	
	String mEmail="";
	String mPassword="";

	Button BTN_sign, BTN_facebook, BTN_kakao;
	EditText EDT_id, EDT_pw;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		
		LoadingDL = new ProgressDialog(this);
		
		EDT_id= (EditText) findViewById(R.id.txt_email);
		EDT_pw= (EditText) findViewById(R.id.txt_password);
		BTN_sign= (Button) findViewById(R.id.email_sign_in_button);
		BTN_facebook= (Button) findViewById(R.id.btn_login_passport_facebook);
		BTN_kakao= (Button) findViewById(R.id.btn_login_passport_kakao);
		
		BTN_sign.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptLogin();
			}
		});
		
		BTN_facebook.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				SPUtil.putString(getApplicationContext(), "my_id", "fantasysa@gmail.com");
				Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
				startActivity(intent);
				finish();
			}
		});
		
		BTN_kakao.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				SPUtil.putString(getApplicationContext(), "my_id", "fantasysa@gmail.com");
				Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
				startActivity(intent);
				finish();
			}
		});
		
		
	}
	
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin()
	{
		// Reset errors.
		EDT_id.setError(null);
		EDT_pw.setError(null);

		// Store values at the time of the login attempt.
		mEmail = EDT_id.getText().toString();
		mPassword = EDT_pw.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(mEmail) && !isPasswordValid(mPassword))
		{
			EDT_pw.setError(getString(R.string.error_invalid_password));
			focusView = EDT_pw;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail))
		{
			EDT_id.setError(getString(R.string.error_field_required));
			focusView = EDT_id;
			cancel = true;
		} else if (!isEmailValid(mEmail))
		{
			EDT_id.setError(getString(R.string.error_invalid_email));
			focusView = EDT_id;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else
		{			
			LoadingHandler.sendEmptyMessage(0);
			
			SignupRequest();
			
			
		}
	}


	private void SignupRequest()
	{
		try
		{
			ParseUser user = new ParseUser();
			user.setUsername(mEmail);
			user.setPassword(mPassword);
			user.setEmail(mEmail);
			 
			// other fields can be set just like with ParseObject
			//user.put("phone", "650-253-0000");
			 
			user.signUpInBackground(new SignUpCallback()
			{
			  public void done(ParseException e)
			  {
			    if (e == null)
			    {				
			    	LoadingHandler.sendEmptyMessage(1);	// sign up finish
			    } else {
			    	LoadingHandler.sendEmptyMessage(2);	// goto sign in ...
			    	
			    	ParseUser.logInInBackground(mEmail, mPassword, new LogInCallback()
			    	{
		    		  public void done(ParseUser user, ParseException e)
		    		  {
		    		    if (user != null)
		    		    {
		    		    	LoadingHandler.sendEmptyMessage(3);
		    		    } else {
		    		      // Signup failed. Look at the ParseException to see what happened.
		    		    	LoadingHandler.sendEmptyMessage(4);
		    		    }
		    		  }
		    		});
			    }
			  }
			});
			
			// Simulate network access.
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
		}
	}
	
	// 로딩 프로그레스 부분을 모두 핸들러로 관리한다.
	public Handler LoadingHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.what==-1)
			{
				
			}
			if(msg.what==0)
			{
				LoadingDL.setMessage("Sign up...");
		        LoadingDL.show();
			}
			if(msg.what==1)
			{
				LoadingDL.hide();
				ShowAlertDialog("Sign up", "Welcome abroad!", "Ok");
			}
			if(msg.what==2)
			{
				LoadingDL.hide();
				LoadingDL.setMessage("Sign in...");
		        LoadingDL.show();
			}
			if(msg.what==3)
			{
				LoadingDL.hide();
				ShowAlertDialog("Sign in", "Welcome!", "Ok");
			}
			if(msg.what==4)
			{
				LoadingDL.hide();
				ShowAlertDialog("Sign in", "Failed!", "Ok");
			}
		}
	};
	
	
	
	
	private boolean isEmailValid(String email)
	{
		// TODO: Replace this with your own logic
		return email.contains("@");
	}

	private boolean isPasswordValid(String password)
	{
		// TODO: Replace this with your own logic
		return password.length() > 4;
	}
	
	
	private void ShowAlertDialog(String strTitle, String strContent, String strButton)
	{
		new AlertDialog.Builder(this)
		.setTitle( strTitle )
		.setMessage( strContent )
		.setPositiveButton( strButton , null)
		.setCancelable(false)
		.create()
		.show();
	}
	
	
	


}
