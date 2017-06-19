package com.bzu.yhd.pocketcampus.bottomnav.home.circle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.model.Comment;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.util.ArrayList;

public class CommentAdapter extends BaseContentAdapter<Comment>{

	public CommentAdapter(Context context, ArrayList<Comment> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_item, null);
			viewHolder.userName = (TextView)convertView.findViewById(R.id.userName_comment);
			viewHolder.commentContent = (TextView)convertView.findViewById(R.id.content_comment);
			viewHolder.index = (TextView)convertView.findViewById(R.id.index_comment);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		final Comment comment = dataList.get(position);
		if(comment.getUser()!=null){
			viewHolder.userName.setText(comment.getUser().getNickName());
			LogUtils.i("CommentActivity","NAME:"+comment.getUser().getNickName());
		}else{
			viewHolder.userName.setText(comment.getUser().getUsername());
		}
		viewHolder.index.setText((position+1)+"楼");
		viewHolder.commentContent.setText(comment.getCommentContent());
		
		return convertView;
	}

	public static class ViewHolder{
		public TextView userName;
		public TextView commentContent;
		public TextView index;
	}
}
