package com.akitektuo.smartlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.activity.light.MainActivity;
import com.akitektuo.smartlist.activity.material.ListActivity;
import com.akitektuo.smartlist.util.Preference;

import static com.akitektuo.smartlist.util.Constant.KEY_CREATED;
import static com.akitektuo.smartlist.util.Constant.KEY_DESIGN;
import static com.akitektuo.smartlist.util.Constant.preference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new Preference(this);
        if (!preference.getPreferenceBoolean(KEY_CREATED)) {
            preference.setDefault();
        }
        if (preference.getPreferenceInt(KEY_DESIGN) == 0) {
            startActivity(new Intent(this, ListActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
