package com.androkrip.components;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.androkrip.CryptActivity;
import com.androkrip.R;
import com.androkrip.dao.ActivityMessage;
import com.androkrip.utils.Encryptor;

/** Screen Lock Dialog for Password Vault
* 
* @author Unicus (unicus<atmark>paranoiaworks.com) for Paranoia Works
* @version 1.0.2
*/ 
public class PWVScreenLockDialog extends ScreenLockDialog {
	
	public PWVScreenLockDialog(View v, String decKeyHash, int decAlgCode) 
	{
		super(v, decKeyHash, decAlgCode);
	}	
	
	public PWVScreenLockDialog(Activity context, String decKeyHash, int decAlgCode) 
	{
		super(context, decKeyHash, decAlgCode);
		implementOnClicks();
	}
		
	void implementOnClicks()
	{		
		unlockButton.setOnClickListener(new View.OnClickListener()
	    {
		    @Override
		    public void onClick(View v) 
		    {
		    	String currentPassword = passwordET.getText().toString().trim();
		    	String testKeyHash = null;
		    	try {
					testKeyHash = (new Encryptor(currentPassword, decAlgCode, unicodeAllowed)).getEncKeyHash();
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	
				if(decKeyHash.equals(testKeyHash))
				{
			    	active = false;
			    	cancel();
				}
				else
				{
	        		passwordET.setText("");
	        		alertTV.setVisibility(View.VISIBLE);
		    		alertTV.setText(context.getResources().getString(R.string.passwordDialog_invalidPassword));
				}
		    }
	    });
		
		leaveButton.setOnClickListener(new View.OnClickListener()
	    {
		    @Override
		    public void onClick(View v) 
		    {
		    	leave = true;
		    	active = false;
		    	cancel();
		    }
	    });
		
    	this.setOnCancelListener(new OnCancelListener() {
    		@Override
    		public void onCancel (DialogInterface dialogInterface) {
		    	if(leave)
		    	{
	    			CryptActivity ca = (CryptActivity)context;
			    	ca.setMessage(new ActivityMessage(CryptActivity.COMMON_MESSAGE_CONFIRM_EXIT, null));
		    	}
    		}
    	});	
	}
}
