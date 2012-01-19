package com.quanturium.blackclock;


import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.OnAmbilWarnaListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.quanturium.blackclock.tools.Prefs;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
	    
	    Prefs.getPreferences(this).registerOnSharedPreferenceChangeListener(this);
	    
	    Preference clock_color = findPreference("clock_color");

	    clock_color.setOnPreferenceClickListener(new OnPreferenceClickListener()
	    {			
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(PreferencesActivity.this, Prefs.getPreferences(PreferencesActivity.this).getInt(Prefs.CLOCK_COLOR, 0), new OnAmbilWarnaListener()
				{
					
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color)
					{
						Prefs.getPreferences(PreferencesActivity.this).edit().putInt(Prefs.CLOCK_COLOR, color).commit();
					}
					
					@Override
					public void onCancel(AmbilWarnaDialog dialog)
					{
						
					}
				});

				dialog.show();
			
				return true;
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Prefs.getPreferences(this).edit().putBoolean(Prefs.RELOAD, true).commit();
	}

}
