package com.bzu.yhd.pocketcampus.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.base.BaseApplication;
import com.bzu.yhd.pocketcampus.main.theme.DayModeFragment;
import com.bzu.yhd.pocketcampus.main.theme.NightModeFragment;

import org.polaric.colorful.Colorful;

import java.util.List;

/**
 * 主题选择
 * </p>
 *
 * @CreateBy Yhd On 2017/3/16 20:01
 */
public class ThemeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_theme);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (Colorful.getThemeDelegate().isNight()) {
                ft.add(R.id.container, NightModeFragment.newInstance(), NightModeFragment.TAG);
            } else {
                ft.add(R.id.container, DayModeFragment.newInstance(), DayModeFragment.TAG);
            }
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchModeFragment(int cx, int cy, boolean appBarExpanded, String which) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        switch (which) {
            case DayModeFragment.TAG:
                fragment = DayModeFragment.newInstance(cx, cy, appBarExpanded);
                break;
            case NightModeFragment.TAG:
                fragment = NightModeFragment.newInstance(cx, cy, appBarExpanded);
                break;
            default:
                throw new IllegalStateException();
        }
        ft.add(R.id.container, fragment, which).commit();
    }

    public void removeAllFragmentExcept(String tag) {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment frag;
        for (int i = 0; i < frags.size(); i++) {
            frag = frags.get(i);
            if (frag == null) {
                continue;
            }
            if (tag == null || !tag.equals(frag.getTag())) {
                ft.remove(frag);
            }
        }
        ft.commit();
    }
}
