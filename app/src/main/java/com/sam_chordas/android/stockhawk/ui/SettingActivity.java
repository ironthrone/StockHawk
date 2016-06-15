package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.sam_chordas.android.stockhawk.R;

public class SettingActivity extends PreferenceActivity{
    private AppCompatDelegate mDelegate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        addToolbar();

        ListPreference preference = (ListPreference) findPreference(getString(R.string.pref_period_key));
        preference.setSummary(getPreferenceManager().getSharedPreferences()
                .getString(getString(R.string.pref_period_key),getString(R.string.pref_period_default)));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });
    }

    private void addToolbar() {
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar,root,false);
        root.addView(toolbar,0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    private AppCompatDelegate getDelegate(){
        if(mDelegate == null){
            mDelegate = AppCompatDelegate.create(this,null);
        }
        return mDelegate;
    }
}
