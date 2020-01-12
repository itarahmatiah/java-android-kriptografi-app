package com.androkrip;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lenovo
 */
public class helpActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
    }

    public void backtoHome(View view) {
        finish();
    }
}
