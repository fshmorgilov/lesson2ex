package com.example.fshmo.businesscard.activities.intro;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.intro.fragments.IntroAboutFragment;
import com.example.fshmo.businesscard.activities.intro.fragments.IntroDetailsFragment;
import com.example.fshmo.businesscard.activities.intro.fragments.IntroMainFragment;
import com.example.fshmo.businesscard.activities.main.NewsFeedFragment;

public class IntroActivity extends FragmentActivity {

    private static final String LTAG = IntroActivity.class.getName();

    private static final int NUM_PAGES = 3;
    public static final String SHARED_PREF_NAME = "START_SHARED_PREF";
    public static final String KEY_STARTED = "BOOLEAN_KEY_STARTER";

    private ViewPager viewPager;
    private ScreenSlidePagerAdapter viewPagerAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        viewPager = findViewById(R.id.intro_pager);
        viewPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        textView = findViewById(R.id.intro_main_text);
        textView.setOnClickListener(v -> NewsFeedFragment.start(this));

        SharedPreferences preference = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        Log.i(LTAG, String.valueOf(preference.getBoolean(KEY_STARTED, true)));

        if (preference.getBoolean(KEY_STARTED, true)) {
            saveSharedPreferences(preference, false);
            Log.i(LTAG, "Showing Intro Activity");
        } else {
            saveSharedPreferences(preference, true);
            Log.i(LTAG, "Showing Feed Activity");
            NewsFeedFragment.start(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 9) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private static void saveSharedPreferences(@NonNull SharedPreferences preference,
                                              @NonNull boolean bool) {
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(KEY_STARTED, bool);
        editor.apply();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment[] fragments = new Fragment[]{
                    new IntroMainFragment(),
                    new IntroDetailsFragment(),
                    new IntroAboutFragment()
            };
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
