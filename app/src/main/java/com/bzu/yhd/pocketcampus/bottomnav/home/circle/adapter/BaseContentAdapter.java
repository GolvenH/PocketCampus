package com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * @author kingofglory
 *         email: kingofglory@yeah.net
 *         blog:  http:www.google.com
 * @param <T>
 * @date 2014-2-24
 * TODO
 */

public abstract class BaseContentAdapter<T> extends BaseAdapter{

	protected Context mContext;
	protected ArrayList<T> dataList ;
	protected LayoutInflater mInflater;
	
	
	
	public ArrayList<T> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<T> dataList) {
		this.dataList = dataList;
	}

	public BaseContentAdapter(Context context,ArrayList<T> list){
		mContext = context;
		dataList = list;
		mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		return getConvertView(position,convertView,parent);
	}
	
	public abstract View getConvertView(int position, View convertView, ViewGroup parent);

}
