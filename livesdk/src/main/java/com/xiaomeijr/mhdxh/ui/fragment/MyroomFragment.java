package com.xiaomeijr.mhdxh.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xiaomeijr.mhdxh.R;
import com.xiaomeijr.mhdxh.data.RoomInfo;
import com.xiaomeijr.mhdxh.ui.activity.MyRoomInfoActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;

/**
 * 我的聊天室
 */
public class MyroomFragment extends Fragment {


    private static MyroomFragment fragment;
    private ListView mListView;

    public MyroomFragment() {
    }

    public static MyroomFragment newInstance() {
        if (fragment == null)
            fragment = new MyroomFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_myroom, container, false);
        initView(v);
        initData();
        return v;
    }
    private void initView(View v) {
        mListView = (ListView) v.findViewById(R.id.listview);
    }

    private void initData() {
        ArrayList list = new ArrayList();
        list.add(new RoomInfo());
        list.add(new RoomInfo());
        list.add(new RoomInfo());
        list.add(new RoomInfo());
        list.add(new RoomInfo());
        list.add(new RoomInfo());
        mListView.setAdapter(new CommonAdapter<RoomInfo>(getActivity(),R.layout.item_chatroom,list) {
            @Override
            protected void convert(ViewHolder viewHolder, RoomInfo item, int position) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(),MyRoomInfoActivity.class));
            }
        });
    }
}
