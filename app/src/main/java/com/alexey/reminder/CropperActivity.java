package com.alexey.reminder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;

public class CropperActivity extends AppCompatActivity {

    private int MAX_WIDTH;
    private int MAX_HEIGHT;

    private int leftCrop;
    private int topCrop;
    private int widthCrop;
    private Bitmap bitmapOriginal;
    private int zoomHeight;
    private int zoomWidth;


    private View cropView;
    private ImageView imageView;
    private ScrollView verticalScroll;
    private HorizontalScrollView horizontalScroll;
    private FrameLayout frameLayout;
    private RelativeLayout layoutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        final byte[] image = getIntent().getByteArrayExtra("image");
        bitmapOriginal = BitmapFactory.decodeByteArray(image, 0, image.length);
        cropView = findViewById(R.id.crop_image);

        horizontalScroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        verticalScroll = (ScrollView) findViewById(R.id.verticalScroll);
        layoutImage = (RelativeLayout) findViewById(R.id.layoutImage);
        initFrameLayout();
        initImageView(bitmapOriginal);
        initButtonSave();
        initButtonCancel();

        CountDownTimer timer = new CountDownTimer(100, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                leftCrop = cropView.getLeft();
                topCrop = cropView.getTop();
                widthCrop = cropView.getWidth();
                if (leftCrop != 0 && topCrop != 0) {
                    layoutImage.setPadding(leftCrop, topCrop, leftCrop, topCrop);
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.width = bitmapOriginal.getWidth();
                    layoutParams.height = bitmapOriginal.getHeight();
                    zoomHeight = layoutParams.height / 90;
                    zoomWidth = layoutParams.width / 90;
                    imageView.setLayoutParams(layoutParams);
                } else {
                    start();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void initButtonCancel() {
        Button cancel = (Button) findViewById(R.id.toolBarCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initButtonSave() {
        Button save = (Button) findViewById(R.id.toolBarSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data", getCroppedImage());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initImageView(Bitmap bitmap) {
        imageView = (ImageView) findViewById(R.id.imageOriginal);
        Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        MAX_WIDTH = defaultDisplay.getWidth();
        MAX_HEIGHT = defaultDisplay.getHeight();
        float zoomScale = Math.min(((float) MAX_WIDTH / bitmap.getWidth()), ((float) MAX_HEIGHT / bitmap.getHeight()));
        Matrix matrix = new Matrix();
        matrix.postScale(zoomScale, zoomScale);
        bitmapOriginal = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        MAX_WIDTH = bitmapOriginal.getWidth() * 2;
        MAX_HEIGHT = bitmapOriginal.getHeight() * 2;
        imageView.setImageBitmap(bitmapOriginal);
    }

    private void initFrameLayout() {
        frameLayout = (FrameLayout) findViewById(R.id.touchLayout);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            private float mx, my;
            private float oldDist;
            private boolean move = false;
            private boolean zoom = false;

            private double spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return Math.sqrt(x * x + y * y);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int dx = (int) (mx - event.getX());
                int dy = (int) (my - event.getY());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = (float) spacing(event);
                        if (oldDist > 10f) {
                            zoom = true;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        zoom = false;
                        move = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (zoom) {
                            float newDist = (float) spacing(event);
                            if (newDist > 10f) {
                                ViewGroup.LayoutParams layoutParamsImage = imageView.getLayoutParams();
                                if (newDist < oldDist) {

                                    layoutParamsImage.height -= zoomHeight;
                                    layoutParamsImage.width -= zoomWidth;

                                    if (layoutParamsImage.height > widthCrop && layoutParamsImage.width > widthCrop) {
                                        verticalScroll.scrollBy(0, -(zoomHeight / 2));
                                        horizontalScroll.scrollBy(-(zoomWidth / 2), 0);
                                    } else if (layoutParamsImage.height <= widthCrop) {
                                        layoutParamsImage.height = widthCrop;
                                        layoutParamsImage.width += zoomWidth;
                                    } else {
                                        layoutParamsImage.width = widthCrop;
                                        layoutParamsImage.height += zoomHeight;
                                    }
                                    imageView.setLayoutParams(layoutParamsImage);

                                } else {

                                    layoutParamsImage.height += zoomHeight;
                                    layoutParamsImage.width += zoomWidth;

                                    if (layoutParamsImage.height < MAX_HEIGHT && layoutParamsImage.width < MAX_WIDTH) {
                                        verticalScroll.scrollBy(0, zoomHeight / 2);
                                        horizontalScroll.scrollBy(zoomWidth / 2, 0);
                                    } else if (layoutParamsImage.height >= MAX_HEIGHT) {
                                        layoutParamsImage.height = MAX_HEIGHT;
                                        layoutParamsImage.width -= zoomWidth;
                                    } else {
                                        layoutParamsImage.width = MAX_WIDTH;
                                        layoutParamsImage.height -= zoomHeight;
                                    }
                                    imageView.setLayoutParams(layoutParamsImage);
                                }
                            }
                        } else if (move) {
                            verticalScroll.scrollBy(0, dy);
                            horizontalScroll.scrollBy(dx, 0);
                        } else {
                            move = true;
                        }
                        mx = event.getX();
                        my = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        verticalScroll.scrollBy(0, dy);
                        horizontalScroll.scrollBy(dx, 0);
                        move = false;
                        break;
                }
                return true;
            }
        });
    }

    public byte[] getCroppedImage() {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        float zoomScale;
        if (layoutParams.width < layoutParams.height) {
            zoomScale = (float) (layoutParams.width + 5) / bitmapOriginal.getWidth();
        } else {
            zoomScale = (float) (layoutParams.height + 5) / bitmapOriginal.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(zoomScale, zoomScale);
        Bitmap bitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
        float x = horizontalScroll.getScrollX();
        float y = verticalScroll.getScrollY();
        Bitmap cropped;
        try {
            cropped = Bitmap.createBitmap(bitmap, (int) x, (int) y, widthCrop, widthCrop);
        } catch (IllegalArgumentException e) {
            cropped = Bitmap.createBitmap(bitmap, (int) x, (int) y, widthCrop - 100, widthCrop - 100);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}