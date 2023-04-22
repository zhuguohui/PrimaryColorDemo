package com.example.orderlayoutdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity2 extends AppCompatActivity {
    private ImageView mImageView;


    private Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //获取图片
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test1);

        mImageView = (ImageView) findViewById(R.id.image_view);

        mImageView.setImageBitmap(bitmap);


    }

    int color = Color.RED;

    public void selectPrimaryColor(View v) {
        new ColorPickerPopup.Builder(this)
                .initialColor(color) // Set initial color
                .enableBrightness(false) // Enable brightness slider or not
                .enableAlpha(false) // Enable alpha slider or not
                .okTitle("选择颜色")
                .cancelTitle("取消")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        MainActivity2.this.color = color;
                        v.setBackgroundColor(color);
                        mImageView.setImageBitmap(ImageUtils.handleImageEffect(bitmap, color));
                    }


                });
    }
}
