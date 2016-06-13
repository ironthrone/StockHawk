package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.sam_chordas.android.stockhawk.R;

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
