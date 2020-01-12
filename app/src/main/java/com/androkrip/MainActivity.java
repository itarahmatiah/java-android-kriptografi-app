package com.androkrip;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androkrip.dao.ActivityMessage;
import com.androkrip.utils.ComponentProvider;
import com.androkrip.utils.DBHelper;
import com.androkrip.utils.Helpers;

/**
 * Application "Main Menu" activity class
 *
 */
public class MainActivity extends CryptActivity {

	private LinearLayout helpButton;
	private LinearLayout fileEncButton;
	private LinearLayout aboutButton;
	private LinearLayout exitButton;
	
	private LinearLayout containerAL;
	private LinearLayout containerBL;
	
	private static boolean readyForDestroy = false;
	
	
	public MainActivity()
	{		
		super();
	}
	
	/** Enable Main Activity for destroy */
	public static void setReadyForDestroy()
	{
		readyForDestroy = true;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		initApp();
		renderLayout();
    }
    
    /** Application Initialization */
    private void initApp()
    { 	    	
    	String importExportPath = settingDataHolder.getItem("SC_Common", "SI_ImportExportPath");
    	if(importExportPath.equals("???")) 
    	{
    		List<File> pathList = Helpers.getExtDirectories(this);
    		if(pathList != null && pathList.size() > 1 && pathList.get(1) != null)
    		{
    			settingDataHolder.addOrReplaceItem("SC_Common", "SI_ImportExportPath",
    				pathList.get(1).getAbsolutePath() + File.separator + getResources().getString(R.string.importExportDir));
    			settingDataHolder.save();
    		}
    	}		
    }   	
    	
    /** Prepare Main Menu Layout */
    private void renderLayout()
    {       	
        this.setContentView(R.layout.la_main);
        setTitle(getResources().getString(R.string.app_name_full));
        
        this.containerAL = (LinearLayout)this.findViewById(R.id.M_containerA);
        this.containerBL = (LinearLayout)this.findViewById(R.id.M_containerB);
        
        TextView text;
        ImageView image;

		this.helpButton = (LinearLayout)getLayoutInflater().inflate(R.layout.lc_square_button_icon, null);
		text = (TextView)helpButton.findViewById(R.id.text);
		text.setText(getResources().getString(R.string.main_helpButton));
		image = (ImageView)helpButton.findViewById(R.id.image);
		image.setImageResource(R.drawable.main_help);
		helpButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent myIntent = new Intent(v.getContext(), helpActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

        this.fileEncButton = (LinearLayout)getLayoutInflater().inflate(R.layout.lc_square_button_icon, null);
        text = (TextView)fileEncButton.findViewById(R.id.text);
        text.setText(getResources().getString(R.string.main_fileEncButton));
        image = (ImageView)fileEncButton.findViewById(R.id.image);
        image.setImageResource(R.drawable.main_file);
        fileEncButton.setOnClickListener(new OnClickListener() 
	    {
		    @Override
		    public void onClick(View v) 
		    {
                Intent myIntent = new Intent(v.getContext(), FileEncActivity.class);
                startActivityForResult(myIntent, 0);
		    }
	    });
        
        this.aboutButton = (LinearLayout)getLayoutInflater().inflate(R.layout.lc_square_button_icon, null);
        text = (TextView)aboutButton.findViewById(R.id.text);
        text.setText(getResources().getString(R.string.main_about));
        image = (ImageView)aboutButton.findViewById(R.id.image);
        image.setImageResource(R.drawable.main_about);
        aboutButton.setOnClickListener(new OnClickListener()
	    {
		    @Override
		    public void onClick(View v) 
		    {
                Intent myIntent = new Intent(v.getContext(), AboutActivity.class);
                startActivityForResult(myIntent, 0);
		    }
	    });

		this.exitButton = (LinearLayout)getLayoutInflater().inflate(R.layout.lc_square_button_icon, null);
		text = (TextView)exitButton.findViewById(R.id.text);
		text.setText(getResources().getString(R.string.main_exitButton));
		image = (ImageView)exitButton.findViewById(R.id.image);
		image.setImageResource(R.drawable.main_exit);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ComponentProvider.getExitDialog(v).show();
			}
		});
        setLayoutOrientation();

    }
    
    void processMessage()
    {
        ActivityMessage am = getMessage();
        if (am == null) return;
        
        int messageCode = am.getMessageCode();
    }
    
    /** Solve differences between Portrait and Landscape orientation */ 
	private void setLayoutOrientation()
    {    	
    	int orientation = this.getResources().getConfiguration().orientation;
    	
        parametrizeSquareView(helpButton);
        parametrizeSquareView(fileEncButton);
        parametrizeSquareView(aboutButton);
		parametrizeSquareView(exitButton);
        
        containerAL.removeAllViews();
        containerBL.removeAllViews();
    	
    	if(orientation == Configuration.ORIENTATION_PORTRAIT)
    	{   	
    		containerBL.setVisibility(View.VISIBLE);
    		containerAL.addView(helpButton);
            containerAL.addView(fileEncButton);
            containerBL.addView(aboutButton);
			containerBL.addView(exitButton);
    	}
    	else if(orientation == Configuration.ORIENTATION_LANDSCAPE)
    	{
    		containerBL.setVisibility(View.GONE);
    		containerAL.addView(helpButton);
            containerAL.addView(fileEncButton);
            containerBL.addView(aboutButton);
			containerBL.addView(exitButton);
    	}
    }
	
	/** Render Layout Helper */
	private void parametrizeSquareView(ViewGroup view)
	{
		float scaler = -1; int borderSize = -1;
		int orientation = this.getResources().getConfiguration().orientation;
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float width = pxToDp(dm.widthPixels);
		float height = pxToDp(dm.heightPixels);
		if(orientation == Configuration.ORIENTATION_PORTRAIT){
			scaler = 2;
			borderSize = dpToPx(10);
		} else {
			scaler = 4;
			borderSize = dpToPx(8);
		}
		int size = dpToPx((width - (10 * scaler)) / scaler);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size, 1.0f);
		params.setMargins(borderSize, borderSize, borderSize, borderSize);
		view.setLayoutParams(params);
		view.setFocusable(true);	
	}
    
	/** Part of the custom flow control */
    @Override
    protected void onStart()
    {
        switch (getRunningCode()) 
        {        

        	case RUNNING_FILEENCACTIVITY:
        	{
                Intent myIntent = new Intent(this, FileEncActivity.class);
                startActivityForResult(myIntent, 0);
        		break;
        	}
        	case RUNNING_SETTINGSACTIVITY:
        	{
                Intent myIntent = new Intent(this, helpActivity.class);
                startActivityForResult(myIntent, 0);
        		break;
        	}
        	case RUNNING_OTHERUTILS:
        	{
                Intent myIntent = new Intent(this, AboutActivity.class);
                startActivityForResult(myIntent, 0);
        		break;
        	}
        	default: 
            	break;
        }
    	super.onStart();
    }
    
    @Override
    public void onConfigurationChanged(Configuration c)
    {
    	setLayoutOrientation();
    	//drawApplicationReport();
    	super.onConfigurationChanged(c);
    }
     
    @Override
    public void onBackPressed()
    {
    	ComponentProvider.getExitDialog(this).show();
    }
    
    @Override
    public void onWindowFocusChanged(boolean b)
    {
    	//drawApplicationReport();
    	super.onWindowFocusChanged(b);
    }
    
    @Override
    public void onResume() {
        super.onResume();

        if(StaticApp.licenseLevel < 2) setTitle(getResources().getString(R.string.app_name_full_free));
        else setTitle(getResources().getString(R.string.app_name_full_pro));
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	if (readyForDestroy)
    	{
    		// Wipeout application
    		try {
				DBHelper.killDB();
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
}