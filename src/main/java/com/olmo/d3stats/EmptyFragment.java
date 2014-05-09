package com.olmo.d3stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.olmo.d3stats.interfaces.Communication;


public class EmptyFragment extends Fragment implements Communication {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FrameLayout view = (FrameLayout) inflater.inflate(
                R.layout.fragment_empty, container, false);


        return view;
    }

    @Override
    public void setTitle(){

    }

    @Override
    public int getHeroId() {
        return -1;
    }
}
