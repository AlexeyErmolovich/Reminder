package com.alexey.reminder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CropperImageActivity extends AppCompatActivity {

    private CropView imageViewOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper_image);

        byte[] image = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        this.imageViewOriginal = (CropView) findViewById(R.id.imageOriginal);
        this.imageViewOriginal.setImageBitmap(bitmap);

        initButtoCancel();
        initButtonSave();
    }

    private void initButtonSave() {
        Button save = (Button) findViewById(R.id.toolBarSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data", imageViewOriginal.getCroppedImage());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initButtoCancel() {
        Button cancel = (Button) findViewById(R.id.toolBarCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
