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
	private String uri4Share;
	private String parseURL;

	public String getParseURL() {
		return parseURL;
	}

	public void setParseURL(String parseURL) {
		this.parseURL = parseURL;
	}

	public String getUri4Share() {
		return uri4Share;
	}

	public void setUri4Share(String uri4Share) {
		this.uri4Share = uri4Share;
	}

	public ImageItem(String _parseURL)
	{
		this.parseURL = _parseURL;
	}
	public ImageItem(Bitmap image, String title, String uri, String uri2)
	{
		super();
		this.image = image;
		this.title = title;
		this.uri= uri;
		this.uri4Share= uri2;
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
