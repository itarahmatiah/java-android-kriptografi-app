package com.androkrip.components;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.androkrip.R;
import com.androkrip.dao.SettingDataHolder;
import com.androkrip.utils.ComponentProvider;

/** Screen Lock Dialog Abstract - Implement "implementOnClicks()"
 * 
 * @author Unicus (unicus<atmark>paranoiaworks.com) for Paranoia Works
 * @version 1.0.2
 */ 
public abstract class ScreenLockDialog extends Dialog {
	
	Activity context;
	
	Button unlockButton;
	Button leaveButton;
	EditText passwordET;
	TextView alertTV;
	CheckBox showPasswordCB;
	
	boolean leave = false;
	boolean active = true;
	boolean unicodeAllowed = false;
	
	String decKeyHash = null;
	int decAlgCode = -1;

	public ScreenLockDialog(View v, String decKeyHash, int decAlgCode) 
	{
		this((Activity)v.getContext(), decKeyHash, decAlgCode);
	}	
	
	public ScreenLockDialog(Activity context, String decKeyHash, int decAlgCode) 
	{
		super(context);
		this.context = context;
		this.decKeyHash = decKeyHash;
		this.decAlgCode = decAlgCode;
		this.init();
	}
	
	abstract void implementOnClicks(); // unlockButton.setOnClickListener, leaveButton.setOnClickListener, ...
	
	private void init()
	{		
		this.setContentView(R.layout.lc_screenlock_dialog);
		this.setTitle(context.getResources().getString(R.string.common_locked_text));
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
		
		unicodeAllowed = SettingDataHolder.getInstance().getItemAsBoolean("SC_Common", "SI_AllowUnicodePasswords");
		
		unlockButton = (Button)this.findViewById(R.id.SLD_unlockButton);
		leaveButton = (Button)this.findViewById(R.id.SLD_leaveButton);
		passwordET = (EditText)this.findViewById(R.id.SLD_passwordEditText);
		showPasswordCB = (CheckBox)this.findViewById(R.id.SLD_showPasswordCheckBox);
		alertTV = (TextView)this.findViewById(R.id.SLD_alertTV);
		
		passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		passwordET.setTransformationMethod(new PasswordTransformationMethod());
		
    	if(!unicodeAllowed) passwordET.setFilters(new InputFilter[] { filter });
    	
    	showPasswordCB.setOnCheckedChangeListener(new OnCheckedChangeListener()
    	{
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    	    {
    	        if (isChecked)
    	        {
    	        	passwordET.setTransformationMethod(null);
    	        	if(passwordET.length() > 0) passwordET.setSelection(passwordET.length());
    	        } else {
    	        	passwordET.setTransformationMethod(new PasswordTransformationMethod());
    	        	if(passwordET.length() > 0) passwordET.setSelection(passwordET.length());
    	        }
    	    }
    	});
    	
		passwordET.addTextChangedListener((new TextWatcher()
    	{
            public void  afterTextChanged (Editable s) {
            }
            public void  beforeTextChanged (CharSequence s, int start, int count, int after) {
            	alertTV.setVisibility(View.GONE);
            	alertTV.setText("");
            }
            public void onTextChanged (CharSequence s, int start, int before, int count)  {
            }
    	}));
    	
    	Window window = this.getWindow();
    	WindowManager.LayoutParams wlp = window.getAttributes();
    	wlp.gravity = Gravity.TOP;
    	window.setAttributes(wlp);
    	window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}
	
	public void leaveButtonEnabled(boolean enabled)
	{
		leaveButton.setEnabled(enabled);
	}
	
	public boolean getActiveFlag()
	{
		return active;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {           
            return true;
        } else return super.onKeyDown(keyCode, event);
    }
	
	// Only ASCII 32...126 allowed
    InputFilter filter = new InputFilter()
	{
	    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) 
	    { 
	    	if (source.length() < 1) return null;
	    	char last = source.charAt(source.length() - 1);
        	if(last > 126 || last < 32) 
        	{
				Dialog showMessageDialog = ComponentProvider.getShowMessageDialog(context, 
						context.getResources().getString(R.string.passwordDialog_title_incorrectCharacter), 
						context.getResources().getString(R.string.passwordDialog_incorrectCharacterReport), 
    					ComponentProvider.DRAWABLE_ICON_INFO_BLUE
    			);
				showMessageDialog.show();
        		
        		return source.subSequence(0, source.length() - 1);
        	}
        	return null;
	    }  
	};
}
