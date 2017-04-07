package com.bzu.yhd.pocketcampus.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.yhd.pocketcampus.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SecondFragment extends Fragment {

    private ArrayList<Fragment> mFragmentArrayList;
    @BindView(R.id.fragment_vp)
    ViewPager mVpNewsList;

    @BindView(R.id.tab_layout)
    TabLayout tabs;

    private Unbinder mUnbinder;


    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    /**
     * 初始化各控件
     *
     * @param view
     */
    private void initView(View view) {


        mFragmentArrayList = new ArrayList<>();
        mFragmentArrayList.add(new FirstFragment());
        mFragmentArrayList.add(new ThirdFragment());

        mVpNewsList.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentArrayList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentArrayList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getStringArray(R.array.news_title)[position];
            }
        });
        tabs.setupWithViewPager(mVpNewsList);
        tabs.setTabMode(TabLayout.MODE_FIXED);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
