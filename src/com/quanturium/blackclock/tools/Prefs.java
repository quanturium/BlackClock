package com.quanturium.blackclock.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs
{
	public final static String RELOAD = "reload";
	public final static String IS_DEF = "is_def";
	
	public final static String CLOCK_SIZE = "clock_size";
	public final static String CLOCK_OPACITY = "clock_opacity";
	public final static String CLOCK_COLOR = "clock_color";
	public final static String AIRPLANE_MODE = "airplane_mode";
	
	
    public static SharedPreferences getPreferences(Context context)
    {
    	return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
