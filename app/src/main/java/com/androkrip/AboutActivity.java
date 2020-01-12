package com.androkrip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lenovo
 */
public class AboutActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    public void backHome(View view) {
            finish();
    }
}
