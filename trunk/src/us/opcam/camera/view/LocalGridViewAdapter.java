package us.opcam.camera.view;

import java.util.ArrayList;

import us.opcam.camera.R;
import us.opcam.camera.util.ImageItem;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author javatechig {@link http://javatechig.com}
 * 
 */
public class LocalGridViewAdapter extends ArrayAdapter<ImageItem>
{
	private Context context;
	private int layoutResourceId;
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

	public LocalGridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		ViewHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.TXT_name = (TextView) row.findViewById(R.id.text_name);
			holder.TXT_date = (TextView) row.findViewById(R.id.text_date);
			holder.IMG_pic = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		ImageItem item = data.get(position);	// ITEM 가져와서
	
		String fileName= item.getTitle();
		String[] tokens= fileName.split("_");
		
		if(tokens.length == 3)	// 형식에 맞는 파일 이름이면 토큰이 3개여야 한다.
		{
			String year= tokens[2].substring(0, 4);
			String month= tokens[2].substring(4, 6);
			String day= tokens[2].substring(6, 8);
			
//			int extPos= tokens[2].lastIndexOf(".");
//			String numberNotIncludeExt= tokens[2].substring(0, extPos);
			
			holder.TXT_name.setText(tokens[0]+"_"+tokens[1]);
			holder.TXT_date.setText(year+"."+month+"."+day);
		}
		else
		{
			holder.TXT_name.setText(item.getTitle());
			holder.TXT_date.setText("...");
		}
		
		
		holder.IMG_pic.setImageBitmap(item.getImage());
		return row;
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public ImageItem getItem(int position)
	{
		return data.get(position);
		//return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	static class ViewHolder
	{
		//TextView 
		TextView TXT_name;
		TextView TXT_date;
		ImageView IMG_pic;
	}
}