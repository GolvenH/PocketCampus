package com.bzu.yhd.pocketcampus.bottomnav.find;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bzu.yhd.pocketcampus.R;
import com.bzu.yhd.pocketcampus.base.BaseFragment;
import com.bzu.yhd.pocketcampus.bottomnav.find.radar.RadarAcitvity;


public class FindFragment extends BaseFragment {

    private String mParam1;
    private String mParam2;
    RelativeLayout re1,re2,re3;
    Intent intent;

    public FindFragment() {
    }

    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_find, container, false);

        re1= (RelativeLayout) view.findViewById(R.id.re_1);
        re2= (RelativeLayout) view.findViewById(R.id.re_2);
        re3= (RelativeLayout) view.findViewById(R.id.re_3);
/*
        re4= (RelativeLayout) view.findViewById(R.id.re_4);
*/


        re1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent();

                intent.setClass(getActivity(), RadarAcitvity.class);
                getActivity().startActivity(intent);            }
        });
        re2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent();
                intent.setClass(getActivity(), MyTopicActivity.class);
                getActivity().startActivity(intent);
            }
        });
        re3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent();
                intent.setClass(getActivity(), FavActivity.class);
                getActivity().startActivity(intent);
            }
        });
/*        re4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent();

                ShowToast("新生专区");
            }
        });*/

        return view;
    }

}
