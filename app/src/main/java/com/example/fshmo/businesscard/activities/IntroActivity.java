package com.example.fshmo.businesscard.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.R;

public class IntroActivity extends AppCompatActivity {

    private static final String LTAG = IntroActivity.class.getName();

    public static final String SHARED_PREF_NAME = "START_SHARED_PREF";
    public static final String KEY_STARTED = "BOOLEAN_KEY_STARTER";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences preference = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Log.e(LTAG, String.valueOf(preference.getBoolean(KEY_STARTED, true)));
        if (preference.getBoolean(KEY_STARTED, true)) {
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean(KEY_STARTED, false);
            editor.apply();
            imageView = findViewById(R.id.intro_image_view);
            Glide.with(this)
                    .load(R.drawable.intro_screen)
                    .into(imageView);
            Log.i(LTAG, "Showing Intro Activity");
        } else {
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean(KEY_STARTED, true);
            editor.apply();
            Log.i(LTAG, "Showing Feed Activity");
            NewsFeedActivity.start(this);
        }
    }
}
