package com.bzu.yhd.pocketcampus.view.activity;

import android.os.Bundle;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.view.base.BaseActivity;
import com.bzu.yhd.pocketcampus.view.fragment.SettingsFragment;

/**
 * 设置
 * <p/>
 * Created by yhd on 2017-03-13.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeToolbar();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("设置");

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance())
                .commit();
    }

}
