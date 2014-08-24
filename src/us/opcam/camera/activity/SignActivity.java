package us.opcam.camera.activity;

import us.opcam.camera.R;
import us.opcam.camera.util.SPUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kakao.APIErrorResult;
import com.kakao.KakaoTalkHttpResponseHandler;
import com.kakao.KakaoTalkProfile;
import com.kakao.KakaoTalkService;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class SignActivity extends Activity implements OnClickListener
{
	Context mContext= null;
	private ProgressDialog LoadingDL;	// ���α׷��� ���̾�α�
	
	String mEmail="";
	String mPassword="";

	Button BTN_sign, BTN_facebook, BTN_kakao, BTN_resettingPW;
	EditText EDT_id, EDT_pw;
	
	private LoginButton loginButton;
	private final SessionCallback mySessionCallback = new MySessionStatusCallback();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		
		mContext= this.getApplicationContext();
		
		LoadingDL = new ProgressDialog(this);
		
		EDT_id= (EditText) findViewById(R.id.txt_email);
		EDT_pw= (EditText) findViewById(R.id.txt_password);
		BTN_sign= (Button) findViewById(R.id.btn_email_sign_in_button);
		BTN_facebook= (Button) findViewById(R.id.btn_login_passport_facebook);
		BTN_kakao= (Button) findViewById(R.id.btn_login_passport_kakao);
		BTN_resettingPW= (Button) findViewById(R.id.resetting_pw);
        
		BTN_sign.setOnClickListener(this);
		BTN_facebook.setOnClickListener(this);
		BTN_kakao.setOnClickListener(this);
		BTN_resettingPW.setOnClickListener(this);

		// �α��� ��ư�� �α��� ����� ���� �ݹ��� �����Ѵ�.
        loginButton = (LoginButton) findViewById(R.id.img_login_passport_kakao);
        loginButton.setLoginSessionCallback(mySessionCallback);
		
	}
	
	@Override
    protected void onResume()
	{
        super.onResume();
        // ������ �ʱ�ȭ �Ѵ�
        if(Session.initializeSession(this, mySessionCallback))
        {
            // 1. ������ ���� ���̸�, ���α׷����ٸ� ���̰ų� ��ư�� ����� ���� �׼��� ���Ѵ�
            loginButton.setVisibility(View.GONE);
        } else if (Session.getCurrentSession().isOpened())
        {
            // 2. ������ ���µȵ� �����̸�, ���� activity�� �̵��Ѵ�.
            onSessionOpened();
        }
            // 3. else �α��� â�� ���δ�.
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
				    		ShowInputDialog(user,"","");
				    	}
				    	else
				    		GotoNext();
				    }
				    else
				    {
				    	if(user.getEmail() == null)
				    	{
				    		LoadingHandler.sendEmptyMessage(-1);
				    		ShowInputDialog(user,"","");
				    	}
				    	else
				    		GotoNext();
//						Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
//						startActivity(intent);
//						finish();
				    }
				  }
			});
		}
		else if(v.getId() == R.id.resetting_pw)
		{
			this.ShowResettingEmailDialog();
		}
		else if(v.getId() == R.id.btn_login_passport_kakao)
		{
//
//			SPUtil.putString(getApplicationContext(), "my_id", "fantasysa@gmail.com");
//			Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
//			startActivity(intent);
//			finish();
		}
		else if(v.getId() == R.id.btn_email_sign_in_button)
		{
			attemptEmailLogin();
		}
		
	}
	

	
	protected void onSessionOpened()
	{
		KakaoTalkService.requestProfile(new MyTalkHttpResponseHandler<KakaoTalkProfile>() {
	        @Override
	        protected void onHttpSuccess(final KakaoTalkProfile talkProfile)
	        {
	        	final String nickName = talkProfile.getNickName();
	        	  
	      		SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_NICK, nickName);	      		

	    		SignupViaKakao(nickName);	
	        }
	    });
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
	public void attemptEmailLogin()
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
	
	// īī���� ���� �α���
	private void SignupViaKakao(final String nick)
	{
		final ParseUser user = new ParseUser();
		user.setUsername(nick);
		user.setPassword("null");
		
		LoadingHandler.sendEmptyMessage(6);
		user.signUpInBackground(new SignUpCallback()
		{
		  public void done(ParseException e)
		  {
		    if (e == null)	// ���� ����
		    {				
		    	LoadingHandler.sendEmptyMessage(-1);	// sign up finish
		    	ShowInputDialog(user,"",nick);
		    }
		    else	// ����ĳ�ÿ� ����� ������, ������ īī���� ���ԵǾ� �ִ� ���. (���缳ġ�� ��⺯�� ��)
		    {
		    	ParseUser.logInInBackground(nick, "null", new LogInCallback()	// īī�� �α����� (�г���/null)
		    	{
	    		  public void done(ParseUser user, ParseException e)
	    		  {
	    			  LoadingHandler.sendEmptyMessage(-1);	// sign up finish
	  		    	  ShowInputDialog(user,"",nick);		// �����ϸ�
	    		  }
		    	});

		    	
		    }
		  }
		});
		

	}


	// ���ξ� ��û.
	private void SignupRequest()
	{
		try
		{
			final ParseUser user = new ParseUser();
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
			    	LoadingHandler.sendEmptyMessage(-1);	// sign up finish
			    	ShowInputDialog(user,mEmail,"");
			    } else {
			    	LoadingHandler.sendEmptyMessage(2);	// goto sign in ...
			    	
			    	ParseUser.logInInBackground(mEmail, mPassword, new LogInCallback()
			    	{
		    		  public void done(ParseUser user, ParseException e)
		    		  {
		    		    if (user != null)	// �α��� �����ϸ�
		    		    {
		    		    	LoadingHandler.sendEmptyMessage(-1);
		    		    	if(user.getEmail()==null)
		    		    	{
		    		    		ShowInputDialog(user,"",user.get("nick").toString());
		    		    	}
		    		    	else if(user.getEmail().equals(""))	// �̸����� ������
		    		    	{	
		    		    		ShowInputDialog(user,"",user.get("nick").toString()); 
		    		    	}
		    		    	else if(user.get("nick")==null)
		    		    	{
		    		    		ShowInputDialog(user,mEmail, ""); 
		    		    	}
		    		    	else if(user.get("nick").toString().equals(""))
		    		    	{
		    		    		ShowInputDialog(user,mEmail, "");
		    		    	}
		    		    	else	// �α��� �ߴµ� �̸��ϰ� �г��� �� �� ������...
		    		    	{

		    					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_EMAIL, user.getEmail());	// email �� shared preference �� �־��ش�.
		    					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_NICK, user.get("nick").toString());	// nickname �� shared preference �� �־��ش�.
		    		    		Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
		    					startActivity(intent);
		    					finish();
		    		    	}
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
	
	
	
	// �ε� ���α׷��� �κ��� ��� �ڵ鷯�� �����Ѵ�.
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
				ShowAlertDialog("Sign in", "Incorrect login or password.", "Ok");
			}
			if(msg.what==5)
			{
				LoadingDL.setMessage("Updating information...");
		        LoadingDL.show();
			}
			if(msg.what==6)
			{
				LoadingDL.setMessage("sign via kakao...");
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
		if(nick.length() <= 4)
		{
			return false;
		}
		return true;
	}
	
	/// īī���� ���ǰ� ���������ڵ鷯
	private abstract class MyTalkHttpResponseHandler<T> extends KakaoTalkHttpResponseHandler<T> {
        @Override
        protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
            //redirectLoginActivity();
        }

        @Override
        protected void onNotKakaoTalkUser(){
            Toast.makeText(getApplicationContext(), "not a KakaoTalk user", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onFailure(final APIErrorResult errorResult) {
            Toast.makeText(getApplicationContext(), "failed : " + errorResult, Toast.LENGTH_SHORT).show();
        }
    }
	
	private class MySessionStatusCallback implements SessionCallback
	{
        @Override
        public void onSessionOpened()
        {
            // ���α׷����ٸ� ���̰� �־��ٸ� �����ϰ� ���� ������ ���� �������� �̵�
        	SignActivity.this.onSessionOpened();
        }


		@Override
		public void onSessionClosed(KakaoException exception)
		{
            // ���α׷����ٸ� ���̰� �־��ٸ� �����ϰ� ���� ������ �������� �ٽ� �α��� ��ư ����.
            loginButton.setVisibility(View.VISIBLE);
			
		}

    }
	
	private void GotoNext()
	{
		Intent intent= new Intent(SignActivity.this, CameraPreview2.class);
		startActivity(intent);
		finish();
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

	
	private void ShowResettingEmailDialog()
	{		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Reset Your Opcam Password.");
		alert.setMessage("Submit your email address and we'll send you a link to reset your password.");
		final EditText emailBox = new EditText(this);
		emailBox.setHint("email");
		
		alert.setView(emailBox);

		alert.setPositiveButton("send", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String email= emailBox.getText().toString();
				ParseUser.requestPasswordResetInBackground(email,
	                    new RequestPasswordResetCallback()
				{
					public void done(ParseException e)
					{
						if (e == null)
						{
						// An email was successfully sent with reset instructions.
							ShowAlertDialog("info","email has been sent.","okay");
						} else {
						// Something went wrong. Look at the ParseException to see what's up.
							ShowAlertDialog("error", e.toString(), "okay");
						}
					}
				});
			}
			
		});
		
		alert.show();
		
	}
	

	private void ShowInputDialog(final ParseUser user, String email, String nick)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		if(!email.equals(""))	// �̸��� �̹� ��������
		{
			alert.setTitle("Please input your nickname.");
			alert.setMessage("Nickname is in order to used in your activity.");
		}
		else if(!nick.equals(""))	// �г۸� �̹� ��������
		{
			alert.setTitle("Please input your email.");
			alert.setMessage("In order to reset your password, please enter the email you used to register for Opcam.");
		}
		else
		{
			alert.setTitle("Please input your email and nickname.");
			alert.setMessage("In order to reset your password, please enter the email you used to register for Opcam.");
		}
		

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
		if(!email.equals(""))	// �⺻�� ������ ���� ����
		{
			emailBox.setText(email);
			emailBox.setEnabled(false);
		}		
		emailBox.setHint("E-mail");
		emailBox.setFilters(filter30);
		layout.addView(emailBox);

		final EditText nicknameBox = new EditText(this);
		nicknameBox.setSingleLine();
		if(!nick.equals(""))	// �⺻�� ������ ���� ����
		{
			nicknameBox.setText(nick);
			nicknameBox.setEnabled(false);
		}
		nicknameBox.setHint("Nickname (4~10 words)");
		nicknameBox.setFilters(filter10);
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
					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_EMAIL, email);	// email �� shared preference �� �־��ش�.
					SPUtil.putString(getApplicationContext(), us.opcam.camera.util.Constants.Extra.MY_NICK, nick);	// nickname �� shared preference �� �־��ش�.
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
