package com.example.namsan.scanvoca.tab;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.study.StudyActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudyFragment extends Fragment {


    public StudyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        Button goStudy = (Button) view.findViewById(R.id.btn_go_study);
        goStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StudyActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
