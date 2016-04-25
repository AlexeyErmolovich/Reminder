package com.alexey.reminder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class SettingsActivity extends AppCompatActivity {

    private PreferenceSettings settings;

    private SwitchCompat switchTextSpeech;
    private SwitchCompat switchVibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = PreferenceSettings.getInstance(getApplicationContext());
        initToolBar();
        initItemTextSpeech();
        initItemVibration();
        initSendEmail();
    }

    private void initSendEmail() {
        RelativeLayout layoutSendEmail = (RelativeLayout) findViewById(R.id.itemSendEmail);
        if (layoutSendEmail != null) {
            layoutSendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
//                    intent.setType("message/rfc822");
                    intent.setData(Uri.parse("mailto:" + "alexeyermolovich@gmail.ru"));
//                    putExtra(Intent.EXTRA_EMAIL, new String[]{"alexeyermolovich@gmail.ru"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Reminder");
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_comment));
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            });
        }
    }

    private void initItemVibration() {
        this.switchVibration = (SwitchCompat) findViewById(R.id.switchVibration);
        if (switchVibration != null) {
            switchVibration.setChecked(settings.getBooleanItem(PreferenceSettings.VIBRATION));
        }
    }

    private void initItemTextSpeech() {
        switchTextSpeech = (SwitchCompat) findViewById(R.id.switchTextSpeech);
        if (switchTextSpeech != null) {
            switchTextSpeech.setChecked(settings.getBooleanItem(PreferenceSettings.TEXT_SPEECH));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolBarSettings));
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.settings);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        this.settings.setBooleanItem(PreferenceSettings.TEXT_SPEECH, this.switchTextSpeech.isChecked());
        this.settings.setBooleanItem(PreferenceSettings.VIBRATION, this.switchVibration.isChecked());
        setResult(RESULT_OK);
        finish();
    }
}
