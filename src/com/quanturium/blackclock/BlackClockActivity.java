package com.quanturium.blackclock;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanturium.blackclock.tools.Prefs;

public class BlackClockActivity extends Activity implements OnClickListener, OnSystemUiVisibilityChangeListener
{
    /** Called when the activity is first created. */
	
	View mainView;
	TextView clockView;
	ImageView preferencesView;
	
	String previousTime = "";
	
	Handler clockHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);        
               
        if(Prefs.getPreferences(this).getBoolean(Prefs.IS_DEF, false) == false)
        {   
        	// Default preferences
        	Prefs.getPreferences(this).edit().putInt(Prefs.CLOCK_COLOR, 0xffffffff).commit();
        	Prefs.getPreferences(this).edit().putInt(Prefs.CLOCK_SIZE, 75).commit();
        	Prefs.getPreferences(this).edit().putInt(Prefs.CLOCK_OPACITY, 40).commit();
        	Prefs.getPreferences(this).edit().putBoolean(Prefs.AIRPLANE_MODE, false).commit();
        	
        	Prefs.getPreferences(this).edit().putBoolean(Prefs.IS_DEF, true).commit();
        }
        
        setContentView(R.layout.main);
        
        mainView = (View) findViewById(R.id.mainView);
        clockView = (TextView) findViewById(R.id.clock);
        preferencesView = (ImageView) findViewById(R.id.preferenceIcon);
        
        clockView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/digital-7-mono.ttf"));
        
        setClock();
        
        mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);         
        
        mainView.setOnSystemUiVisibilityChangeListener(this);
        mainView.setOnClickListener(this);
        
        preferencesView.setOnClickListener(this);

        this.clockHandler = new Handler()
        {        	
        	@Override
        	public void handleMessage(Message msg)
        	{
        		String currentTime = getTime();
        		
        		if(!currentTime.equals(previousTime))
        		{        			
        			clockView.setText(getTime());
        			previousTime = currentTime;
        		}
        		
        	}        	
        };        
        
        new Thread(new Runnable()
        {			
			@Override
			public void run()
			{
				Message m;                
				
				while(!Thread.currentThread().isInterrupted())
				{		
					m = new Message();
	                BlackClockActivity.this.clockHandler.sendMessage(m);
	                
	                try {
	                        Thread.sleep(1000);
	                } catch (InterruptedException e) {
	                        Thread.currentThread().interrupt();
	                }
				}
			}
		}).start();                  
    }  
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	if(Prefs.getPreferences(this).getBoolean(Prefs.RELOAD, false))
    	{
    		setClock();
    		Prefs.getPreferences(this).edit().putBoolean(Prefs.RELOAD, false).commit();
    	}
    	
    	if(Prefs.getPreferences(this).getBoolean(Prefs.AIRPLANE_MODE, false))
    	{
    		Toast.makeText(this, "Airplane mode : on", Toast.LENGTH_SHORT).show();
    		Settings.System.putInt(this.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 1);
    		
    		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    		intent.putExtra("state", 1);
    		sendBroadcast(intent);
    	}
    		
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	if(Prefs.getPreferences(this).getBoolean(Prefs.AIRPLANE_MODE, false))
    	{
    		Settings.System.putInt(this.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0);
    		Toast.makeText(this, "Airplane mode : off", Toast.LENGTH_SHORT).show();
    		
    		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    		intent.putExtra("state", 0);
    		sendBroadcast(intent);
    	}    		
    }
    
    private void setClock()
    {
    	clockView.setTextColor(Prefs.getPreferences(this).getInt(Prefs.CLOCK_COLOR, 0));
    	clockView.setAlpha((float) Prefs.getPreferences(this).getInt(Prefs.CLOCK_OPACITY, 0) / 100);
    	
    	DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	
    	clockView.setTextSize(metrics.heightPixels * (1 / metrics.density) * Prefs.getPreferences(this).getInt(Prefs.CLOCK_SIZE, 0) / 150);    	
	}

	private String getTime()
    {
    	Calendar c = Calendar.getInstance(); 
    	int hours = c.get(Calendar.HOUR_OF_DAY);
    	int minutes = c.get(Calendar.MINUTE);
    	
		return String.format("%02d", hours) + ":" + String.format("%02d", minutes);    	
    }

	@Override
	public void onClick(View v)
	{
		if(v == mainView)
			mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		if(v == preferencesView)
			startActivity(new Intent(this, PreferencesActivity.class));
	}

	@Override
	public void onSystemUiVisibilityChange(int visibility)
	{
		if(visibility == View.SYSTEM_UI_FLAG_VISIBLE)
		{
			preferencesView.setVisibility(View.VISIBLE);
		}
		else
		{
			preferencesView.setVisibility(View.GONE);
		}
	}
}