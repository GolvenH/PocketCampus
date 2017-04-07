package com.bzu.yhd.pocketcampus.view.bottom.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.view.base.BaseFragment;
import com.bzu.yhd.pocketcampus.view.fragment.FirstFragment;
import com.bzu.yhd.pocketcampus.view.fragment.ThirdFragment;

import org.polaric.colorful.Colorful;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 新闻列表界面
 *
 * @CreateBy yhd
 * @CreateOn 2017/4/6 15:56
 */
public class HomeListFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private ArrayList<Fragment> mFragmentArrayList;
    @BindView(R.id.vp_news_list)
    ViewPager mVpNewsList;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;

    @BindView(R.id.pagerstabs)
    PagerSlidingTabStrip tabs;

    private Unbinder mUnbinder;
    private static final String ARG_COLUMN_COUNT = "column-count";

    @SuppressWarnings("unused")
    public static HomeListFragment newInstance(int columnCount) {
        HomeListFragment fragment = new HomeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mFragmentArrayList = new ArrayList<>();
        mFragmentArrayList.add(new FirstFragment());
        mFragmentArrayList.add(new ThirdFragment());

        tabs.setTextSize(28);
        if (Colorful.getThemeDelegate().isNight()) {
            //字体颜色
            tabs.setTextColorResource(R.color.alpha_white_color_60);
            //下划横线颜色
            tabs.setIndicatorColorResource(R.color.alpha_white_color_60);
            //中间分割线颜色
            tabs.setDividerColorResource(R.color.alpha_white_color_40);
            tabs.setIndicatorHeight(5);
        } else {
            relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),
                    Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));

            //字体颜色
            tabs.setTextColorResource(R.color.alpha_white_color_80);
            //下划横线颜色
            tabs.setIndicatorColorResource(R.color.alpha_white_color_90);
            //中间分割线颜色
            tabs.setDividerColorResource(R.color.alpha_white_color_60);
            tabs.setUnderlineColor(ContextCompat.getColor(getActivity(),
                    Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
            tabs.setUnderlineHeight(2);
            tabs.setIndicatorHeight(6);

        }

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
        mVpNewsList.addOnPageChangeListener(this);
        tabs.setViewPager(mVpNewsList);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mVpNewsList.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
