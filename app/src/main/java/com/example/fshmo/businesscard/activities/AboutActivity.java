package com.example.fshmo.businesscard.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fshmo.businesscard.R;

public class AboutActivity extends AppCompatActivity {

    private static final String LTAG = AboutActivity.class.getName();
    private final String[] addresses = new String[]{"fshmorgilov@gmail.com"};

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
        instagramBtn.setOnClickListener(v -> openWebPage("https://www.instagram.com/fedorthemaker/"));

        final Button facebookBtn = findViewById(R.id.social_facebook);
        facebookBtn.setBackgroundResource(R.drawable.facebook_logo);
        facebookBtn.setOnClickListener(v -> openWebPage("https://www.facebook.com/fedor.shmorgilov"));

        final Button telegramBtn = findViewById(R.id.social_telegram);
        telegramBtn.setBackgroundResource(R.drawable.telegram_logo);
        telegramBtn.setOnClickListener(v -> openWebPage("https://t.me/Iyanamas"));

        final EditText sendMail = findViewById(R.id.send_mail);
        final String text = sendMail.getText().toString().trim();
        sendMail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                composeEmail(addresses, "Nice to meet you!", text);
            }
            return false;
        });
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        final String errorMessage = "No app compatible";
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(LTAG, "Navigated to: " + url);
            startActivity(intent);
        } else {
            Log.i(LTAG, errorMessage);
            showToast(errorMessage);
        }
    }

    private void showToast(String s) {
        Toast.makeText(AboutActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private void composeEmail(String[] addresses, @NonNull final String subject, @NonNull final String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        Log.i(LTAG, "Composing email. \n Subject: " + subject + "\n Body : " + body);
        final String errorMessage = "No email app";
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.i(LTAG, "Composed");
            startActivity(intent);
        } else {
            Log.i(LTAG, errorMessage);
            showToast(errorMessage);
        }
    }
}
