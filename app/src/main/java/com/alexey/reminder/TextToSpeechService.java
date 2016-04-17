package com.alexey.reminder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Alexey on 11.04.2016.
 */
public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private final String UTTERANCE_ID = "FINISHED_PLAYING";
    private String text;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            Log.i(getClass().getName(), "Exit");
            stopSelf();
        }

        @Override
        public void onError(String utteranceId) {
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            Log.i("Speak", Locale.getDefault().toString());
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(getClass().getName(), "This Language is not supported");
                Toast.makeText(TextToSpeechService.this, "Voice data is loaded", Toast.LENGTH_SHORT).show();
            }else{
                speak(text);
            }
        } else
            Log.e(getClass().getName(), "Initialization Failed!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("text");
        Log.i(getClass().getName(), text);
        this.tts = new TextToSpeech(getApplicationContext(), this);
        this.tts.setOnUtteranceProgressListener(utteranceProgressListener);

        speak(text);
        return TextToSpeechService.START_NOT_STICKY;
    }

    public void speak(String speak) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(speak, TextToSpeech.QUEUE_ADD, null, "FINISHED_PLAYING");
        } else {
            tts.speak(speak, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
