package com.example.fshmo.businesscard.activities.about;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.arellomobile.mvp.InjectViewState;
import com.example.fshmo.businesscard.App;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.base.BasePresenter;

import androidx.annotation.NonNull;

@InjectViewState
public class AboutPresenter extends BasePresenter<AboutView> {

    private static final String TAG = AboutPresenter.class.getName();
    private static Resources resources = App.INSTANCE.getResources();
    private static Context ctx = App.INSTANCE.getApplicationContext();

    void openInstagram() {
        openWebPage(resources.getString(R.string.instagram));
    }

    void openFacebook() {
        openWebPage(resources.getString(R.string.facebook));
    }

    void openTelegram() {
        openWebPage(resources.getString(R.string.telegram));
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        final String errorMessage = "No app compatible";
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            Log.d(TAG, "Navigated to: " + url);
            ctx.startActivity(intent);
        } else {
            Log.i(TAG, errorMessage);
            getViewState().showToast(errorMessage);
        }
    }

    void composeEmail(int actionId, String text) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            final String[] addresses = resources.getStringArray(R.array.email_addresses);
            composeEmail(addresses, "Nice to meet you!", text);
        } else getViewState().showToast("Sorry, can't do that");
    }

    private void composeEmail(String[] addresses, @NonNull final String subject, @NonNull final String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        Log.i(TAG, "Composing email. \n Subject: " + subject + "\n Body : " + body);
        final String errorMessage = "No email app";
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            Log.i(TAG, "Composed");
            ctx.startActivity(intent);
        } else {
            Log.i(TAG, errorMessage);
            getViewState().showToast(errorMessage);
        }
    }
}
