package com.example.fshmo.businesscard.activities.about;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.base.MvpAppCompatActivity;

public class AboutActivity extends MvpAppCompatActivity implements AboutView {

    private static final String LTAG = AboutActivity.class.getName();

    @InjectPresenter
    private AboutPresenter presenter;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button instagramBtn = findViewById(R.id.social_instagram);
        instagramBtn.setBackgroundResource(R.drawable.instagram_logo);
        instagramBtn.setOnClickListener(v -> presenter.openInstagram());

        final Button facebookBtn = findViewById(R.id.social_facebook);
        facebookBtn.setBackgroundResource(R.drawable.facebook_logo);
        facebookBtn.setOnClickListener(v -> presenter.openFacebook());

        final Button telegramBtn = findViewById(R.id.social_telegram);
        telegramBtn.setBackgroundResource(R.drawable.telegram_logo);
        telegramBtn.setOnClickListener(v -> presenter.openTelegram());

        final EditText sendMail = findViewById(R.id.send_mail);
        final String text = sendMail.getText().toString().trim();
        sendMail.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
                presenter.composeEmail(actionId, text);
            return false;
        });
    }

    public void showToast(String s) {
        Toast.makeText(AboutActivity.this, s, Toast.LENGTH_LONG).show();
    }

}
