package com.bzu.yhd.pocketcampus.view.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseActivity;
import com.bzu.yhd.pocketcampus.bottomnav.home.service.OpenModel;
import com.bzu.yhd.pocketcampus.view.adapter.LicenseListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 开源许可
 * <p/>
 * Created by Yhd on 2017-02-14.
 */
public class OpenLicenseActivity extends BaseActivity {

    @BindView(R.id.license_recycler_view)
    RecyclerView mRecyclerView;
    private Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_license);
        mUnbinder = ButterKnife.bind(this);
        initializeToolbar();

        String[] libs = getResources().getStringArray(R.array.libraries);
        String[] licenses = getResources().getStringArray(R.array.licenses);
        List<OpenModel> list = new ArrayList<>();
        OpenModel openModel;
        for (int i = 0; i < libs.length; i++) {
            openModel = new OpenModel();
            openModel.setName(libs[i]);
            openModel.setLicense(licenses[i]);
            list.add(openModel);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new LicenseListAdapter(list));
    }
}
