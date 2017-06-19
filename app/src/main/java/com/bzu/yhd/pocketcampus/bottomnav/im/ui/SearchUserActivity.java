package com.bzu.yhd.pocketcampus.bottomnav.im.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.model.User;
import com.bzu.yhd.pocketcampus.bottomnav.im.adapter.OnRecyclerViewListener;
import com.bzu.yhd.pocketcampus.bottomnav.im.adapter.SearchUserAdapter;
import com.bzu.yhd.pocketcampus.bottomnav.im.model.BaseModel;
import com.bzu.yhd.pocketcampus.bottomnav.im.model.UserModel;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/*搜索好友
        *
        *

@author :smile
 * @project:SearchUserActivity
 * @date :2016-01-25-18:23
 */
public class SearchUserActivity extends BaseActivity {

    @BindView(R.id.et_find_name)
    EditText et_find_name;
    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    @BindView(R.id.btn_search)
    Button btn_search;
    @BindView(R.id.rc_view)
    RecyclerView rc_view;
    LinearLayoutManager layoutManager;
    SearchUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeToolbar();
        toolbar.setTitle("添加好友");

        adapter = new SearchUserAdapter();
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        rc_view.setAdapter(adapter);
        sw_refresh.setEnabled(true);
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                User user = adapter.getItem(position);
                bundle.putSerializable("u", user);
                Intent i=new Intent();
                i.setClass(SearchUserActivity.this,UserInfoActivity.class);
                startActivity(i);
            }
            @Override
            public boolean onItemLongClick(int position) {
                return true;
            }
        });
    }

    @OnClick(R.id.btn_search)
    public void onSearchClick(View view) {
        sw_refresh.setRefreshing(true);
        query();
    }

    public void query() {
        String name = et_find_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("请填写用户名");
            sw_refresh.setRefreshing(false);
            return;
        }
        UserModel.getInstance().queryUsers(name, BaseModel.DEFAULT_LIMIT,
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            sw_refresh.setRefreshing(false);
                            adapter.setDatas(null);
                            adapter.notifyDataSetChanged();
                            showToast(e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }
                }
        );
    }

}
