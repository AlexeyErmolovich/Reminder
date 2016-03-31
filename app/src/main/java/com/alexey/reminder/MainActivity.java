package com.alexey.reminder;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class  MainActivity extends AppCompatActivity {

    private final int NOTE_ADD = 1;

    private NoteAdapter adapter;

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
        if(resultCode==RESULT_OK){
            adapter.notifyDataSetChanged();
        }
    }

    private void initListView() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeNoteActivity.class);
                intent.putExtra("NoteUUID", "");
                startActivityForResult(intent, NOTE_ADD);
            }
        });
        this.adapter = new NoteAdapter(this);

        ListView listView = (ListView) findViewById(R.id.listViewMain);
        listView.setAdapter(this.adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if(scrollState==SCROLL_STATE_FLING){
                    fab.hide();
                }
                if(scrollState==SCROLL_STATE_IDLE){
                    fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarMain);
        toolbar.setTitle(R.string.app_name);
    }

}
