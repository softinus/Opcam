package us.opcam.camera.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.facebook.SessionDefaultAudience;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;

public class OpcamApp extends Application
{
	private static final String APP_ID = "193443224183217";
	private static final String APP_NAMESPACE = "omnipresent_camera";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		{	// facebook init.
			// set log to true
			Logger.DEBUG_WITH_STACKTRACE = true;
	
			// initialize facebook configuration
			Permission[] permissions = new Permission[] { 
					Permission.BASIC_INFO, 
					Permission.USER_CHECKINS,
					Permission.USER_PHOTOS, 
					Permission.FRIENDS_EVENTS, 
					Permission.PUBLISH_STREAM };
	
			SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(APP_ID)
				.setNamespace(APP_NAMESPACE)
				.setPermissions(permissions)
				.setDefaultAudience(SessionDefaultAudience.FRIENDS)
				.setAskForAllPermissionsAtOnce(false)
				.build();
	
			SimpleFacebook.setConfiguration(configuration);
		}
		
		{	// parse init
			Parse.initialize(this, "5qeL512r6WzBuHnxGhMKLNNj0tUCBAI26FPN4TRl", "V2mAVufHvtdaTwlas4LANYSJoQbdFrcAomBRcOL7");
			ParseFacebookUtils.initialize("193443224183217");
		}
		
		{
			Map<String, String> dimensions = new HashMap<String, String>();
			// What type of news is this?
			dimensions.put("category", "politics");
			// Is it a weekday or the weekend?
			dimensions.put("dayType", "weekday");
			// Send the dimensions to Parse along with the 'read' event
			 
			ParseAnalytics.trackEvent("read", dimensions);
		}
		
		{
			// Create global configuration and initialize ImageLoader with this configuration	        
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();
			
			ImageLoader.getInstance().init(config);
		}
	}

}
