package us.opcam.camera.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import us.opcam.camera.R;
import us.opcam.camera.view.DiscoverGalleryFragment;
import us.opcam.camera.view.LocalGalleryFragment;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//public class GalleryTabsActivity extends FragmentActivity implements ActionBar.TabListener
public class GalleryTabsActivity extends SherlockFragmentActivity implements ActionBar.TabListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	





	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_tabs);
		
		
		Log.d("GalleryTabsActivity", "==============onCreate1");
		
		setTitle("Gallery");	// 갤러리

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		Log.d("GalleryTabsActivity", "=====================onCreate2");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode != 0)
			return;
		
		// 로컬 갤러리 아이템 클릭했을 떄 넘어가는 것 처리하는 부분인데, 현재 fragment 구조로 변경되면서 임시 보류.
		
//		arrImages= getData();	// 찍은 사진 데이터들을 얻어와서
//		
//		// 데이터가 아무것도 없으면
//		if(arrImages == null || arrImages.isEmpty())
//		{
//			toast("No pictures on opcam folder");
//			finish();
//			return;
//		}
//		
//		
//		Collections.reverse(arrImages);	// 최근 찍은 것이 위로 가게
//		
//		gridView = (GridView) findViewById(R.id.grid_view);
//		customGridAdapter = new GridViewAdapter(this, R.layout.row_grid, arrImages);
//		gridView.setAdapter(customGridAdapter);
		
	}
	
	

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {

	    switch(item.getItemId())
	    {
	         
	    case R.id.actionbar_camera:
	    	this.GotoCameraActivity();	// 카메라 버튼을 누르면 카메라 찍는 화면으로 넘어간다.
	        break;
	    }
		return super.onOptionsItemSelected(item);
	}

	public void GotoCameraActivity()
	{
		//Intent intent= new Intent(GalleryTabsActivity.this, CameraPreview2.class);
		//startActivity(intent);
		this.finish();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.gallery, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if(position == 0)
			{
				Fragment fragment = new LocalGalleryFragment();
				return fragment;	
			}
			else
			{
				Fragment fragment = new DiscoverGalleryFragment();
				return fragment;
			}
			
			
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			//Locale l = Locale.getDefault();
			switch (position)
			{
			case 0:
				return "Me";
			case 1:
				return "Discover";
			}
			return null;
		}
	}
	

	public void toast(String msg)
    {
        Toast.makeText (this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }

}
