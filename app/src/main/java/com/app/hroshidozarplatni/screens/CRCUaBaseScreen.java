package com.app.hroshidozarplatni.screens;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public abstract class CRCUaBaseScreen extends FragmentActivity {

    abstract int getLayout();

    abstract void initView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initView();
    }
}
