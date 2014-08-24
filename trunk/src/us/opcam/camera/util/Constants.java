package us.opcam.camera.util;

public final class Constants
{
	public static boolean bRefreshed= false;	// Discover gallery에서 한 번 서버에서 받아오면 대기.
	public static int nDiscoverCurrentPage= 0;	// Discover gallery 현재 선택된 페이지 저장.
	
	public static class Extra
	{
		public static final String IMAGES = "us.opcam.IMAGES";
		public static final String IMAGES_CREATE_DATE = "us.opcam.IMAGES_CREATE_DATE";
		public static final String IMAGES_AUTHOR = "us.opcam.IMAGES_AUTHOR";
		public static final String IMAGE_POSITION = "us.opcam.IMAGE_POSITION";
		public static final String DEL_POS_SERVER = "us.opcam.FOR_DELETE_POS_IN_SERVER";
		public static final String REFRESH_DISCOVER = "us.opcam.FOR_REFRESH_DISCOVER_GALLERY";
		
		// user infomations
		public static final String MY_EMAIL="us.opcam.MY_EMAIL";
		public static final String MY_NICK="us.opcam.MY_NICK";
	}
	
	public static class REQ
	{
		public static final int CAMERA_TO_AVIARY= 1;
		public static final int CAMERA_TO_GALLERY= 2;
		public static final int DISCOVER_TO_PICTURE= 3;
	}
}
