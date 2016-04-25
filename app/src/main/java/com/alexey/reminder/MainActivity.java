package com.alexey.reminder;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int NOTE_ADD = 1;
    private NoteAdapter adapter;
    private static final int REQUEST_CODE = 150;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();
        initListView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            adapter.updateList();
        }
    }

    private void initListView() {
        final com.melnykov.fab.FloatingActionButton fab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeNoteActivity.class);
                intent.putExtra("NoteUUID", "");
                intent.putExtra("edit", true);
                startActivityForResult(intent, NOTE_ADD);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });
        this.adapter = new NoteAdapter(this);

        ListView listView = (ListView) findViewById(R.id.listViewMain);
        listView.setAdapter(this.adapter);
        fab.attachToListView(listView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarMain);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.inflateMenu(R.menu.menu);
        initItemSearchToolBar(toolbar);
        initItemSettingsToolBar(toolbar);
    }

    private void initItemSettingsToolBar(Toolbar toolbar) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_item_settings){
                    Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(settings);
                    return true;
                }
                return false;
            }
        });
    }

    private void initItemSearchToolBar(Toolbar toolbar) {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.menu_item_searsh);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0) {
                    adapter.searchItems(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    adapter.updateList();
                }
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                adapter.updateList();
            }
        });
    }
}
