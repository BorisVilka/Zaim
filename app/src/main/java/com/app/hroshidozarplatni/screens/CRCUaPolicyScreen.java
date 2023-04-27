package com.app.hroshidozarplatni.screens;

import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.app.hroshidozarplatni.R;

public class CRCUaPolicyScreen extends CRCUaBaseScreen {
    @Override
    int getLayout() {
        return R.layout.policy_screen;
    }

    @Override
    void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        TextView tvPolicy = findViewById(R.id.tvPolicy);

        String policy = getString(R.string.policy_string).replace("--app_name--", getString(R.string.app_name));
        tvPolicy.setText(Html.fromHtml(policy));
    }
}
