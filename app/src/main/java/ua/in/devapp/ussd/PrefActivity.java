package ua.in.devapp.ussd;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    String language1 = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        ListPreference listPreference = (ListPreference) findPreference(language1);
        if(listPreference.getValue()==null) {
            listPreference.setValueIndex(0);
        }

        listPreference.setSummary(listPreference.getValue().toString());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference etp = (ListPreference) pref;
            pref.setSummary(etp.getValue().toString());

            if (language1.equals(key)) {
                MyApp.ChangeLang(getResources(), this);
                MyApp.setReloadCon(true);
            }
        } else if (pref instanceof CheckBoxPreference) {
            //CheckBoxPreference etp = (CheckBoxPreference) pref;

            if (Operators.ks.name().equals(key)
                    || Operators.life.name().equals(key)
                    || Operators.mts.name().equals(key)) {
                MyApp.setReloadCon(true);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}

