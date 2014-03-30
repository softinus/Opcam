package us.opcam.camera.util;

import android.graphics.Bitmap;

/**
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class ImageItem
{
	private Bitmap image;
	private String title;
	private String uri;

	public ImageItem(Bitmap image, String title, String uri)
	{
		super();
		this.image = image;
		this.title = title;
		this.uri= uri;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
