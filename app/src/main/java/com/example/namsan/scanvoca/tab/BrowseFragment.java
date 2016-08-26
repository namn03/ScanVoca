package com.example.namsan.scanvoca.tab;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.list.activity.FolderListActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment {


    public BrowseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        Button goFolderList = (Button) view.findViewById(R.id.btn_go_folderlist);
        goFolderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FolderListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
