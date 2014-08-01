package us.opcam.camera.activity;

import us.opcam.camera.R;
import us.opcam.camera.util.SPUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidquery.util.Constants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignActivity extends Activity implements OnClickListener
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
		
		BTN_sign.setOnClickListener(this);
		BTN_facebook.setOnClickListener(this);
		BTN_kakao.setOnClickListener(this);
		
	}
	

	@Override
	public void onClick(View v)
	{
		
		if(v.getId() == R.id.btn_login_passport_facebook)
		{
			LoadingHandler.sendEmptyMessage(0);
			
			ParseFacebookUtils.logIn(this, new LogInCallback()
			{				  
				  @Override
				  public void done(ParseUser user, ParseException err)
				  {
				    if (user == null)
				    {
				    	ShowAlertDialog("User cancelled", "Uh oh. The user cancelled the Facebook login.\n\n"+err.toString(), "Ok");
				    }
				    else if (user.isNew())
				    {
				    	ShowAlertDialog("Successful", "User signed up and logged in through Facebook!", "Ok");

				    	if(user.getEmail() == null)
				    	{
				    		LoadingHandler.sendEmptyMessage(-1);
				    		ShowInputDialog(user);
				    	}
				    }
				    else
				    {
				    	if(user.getEmail() == null)
				    	{
				    		LoadingHandler.sendEmptyMessage(-1);
				    		ShowInputDialog(user);
				    	}
//						Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
//						startActivity(intent);
//						finish();
				    }
				  }
			});
		}
		else if(v.getId() == R.id.btn_login_passport_kakao)
		{

			SPUtil.putString(getApplicationContext(), "my_id", "fantasysa@gmail.com");
			Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
			startActivity(intent);
			finish();
		}
		else if(v.getId() == R.id.btn_email_sign_in_button)
		{
			attemptLogin();
		}
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
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
				LoadingDL.hide();
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
			if(msg.what==5)
			{
				LoadingDL.setMessage("Updating information...");
		        LoadingDL.show();
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
	
	// check nickname lenght
	private boolean isNickNameVaild(String nick)
	{
		if(nick.length() > 4)
		{
			return false;
		}
		return true;
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


	private void ShowInputDialog(final ParseUser user)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Please input your email and nickname.");
		alert.setMessage("In order to reset your password, please enter the email you used to register for Opcam.");

		// Set an EditText view to get user input
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		// letter length filter
		InputFilter[] filter30 = new InputFilter[1];
		filter30[0] = new InputFilter.LengthFilter(30);
		 
		InputFilter[] filter10 = new InputFilter[1];
		filter10[0] = new InputFilter.LengthFilter(10);
		 
		final EditText emailBox = new EditText(this);
		emailBox.setSingleLine();
		emailBox.setHint("E-mail");
		emailBox.setFilters(filter30);
		layout.addView(emailBox);

		final EditText nicknameBox = new EditText(this);
		nicknameBox.setSingleLine();
		nicknameBox.setFilters(filter10);
		nicknameBox.setHint("Nickname (4~10 words)");
		layout.addView(nicknameBox);

		alert.setView(layout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String email = emailBox.getText().toString();
				String nick= nicknameBox.getText().toString();
				if( isEmailValid(email) && isNickNameVaild(nick) )
				{
					LoadingHandler.sendEmptyMessage(5);	// updating information...
					try
					{
						user.setEmail(email);
						user.put("nick", nick);
						user.save();
					} catch (ParseException e) {
						LoadingHandler.sendEmptyMessage(-1);
						ShowAlertDialog("Error ouccurred", e.toString(), "Ok");
						return;
					}
					LoadingHandler.sendEmptyMessage(-1);
					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_EMAIL, email);	// email 을 shared preference 에 넣어준다.
					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_NICK, nick);	// nickname 을 shared preference 에 넣어준다.
					Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
					startActivity(intent);
					finish();
				}
				else
				{
					ShowAlertDialog("Error", "email or nick is invaild.", "Ok");
				}
			}
		});


		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		
		alert.show(); 
	}

	
	
	


}
