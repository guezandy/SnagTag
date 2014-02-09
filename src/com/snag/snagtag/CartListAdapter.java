package com.snag.snagtag;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

 

public class CartListAdapter extends BaseAdapter {

	private ArrayList<Entity> listData;

	private LayoutInflater layoutInflater;

	public CartListAdapter(Context context, ArrayList<Entity> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
			holder = new ViewHolder();
			holder.brandView = (TextView) convertView.findViewById(R.id.brand);
			holder.priceView = (TextView) convertView.findViewById(R.id.price);
			holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
			//holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Entity closetItem = (Entity) listData.get(position);
		System.out.println("Entity brand: "+closetItem.get("Brand"));
		System.out.println("Entity price: "+closetItem.get("Price"));
		holder.brandView.setText("Brand: " +closetItem.get("Brand"));
		holder.priceView.setText("Price: " + closetItem.get("Price"));
		//holder.checkBox.setTag(position);

		if (holder.imageView != null) {
			new ImageDownloaderTask(holder.imageView).execute(closetItem.get("ImageUrl").toString());
		}

		return convertView;
	}

	static class ViewHolder {
		//CheckBox checkBox;
		TextView brandView;
		TextView priceView;
		ImageView imageView;
	}
}
