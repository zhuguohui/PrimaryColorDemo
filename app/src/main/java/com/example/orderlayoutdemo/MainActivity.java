package com.example.orderlayoutdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderlayoutdemo.view.TRSOrderLayout;

/**
 * 打包密码demo123
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout orderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orderLayout = findViewById(R.id.layout_order);
        int size = dip2px(this, 10);
        findViewById(R.id.btn_add).setOnClickListener(v -> changeSize(size));
        findViewById(R.id.btn_reduce).setOnClickListener(v -> changeSize(-size));

        LinearLayout layoutSet = findViewById(R.id.layout_set);
        setView(layoutSet);
    }

    @SuppressLint("SetTextI18n")
    private void setView(LinearLayout layoutSet) {
        View.OnClickListener[] clickListeners = new View.OnClickListener[3];
        for (int i = 0; i < 3; i++) {
            TRSOrderLayout.OrderLayoutParams layoutParams = (TRSOrderLayout.OrderLayoutParams) orderLayout.getChildAt(i).getLayoutParams();
            LinearLayout layout = (LinearLayout) layoutSet.getChildAt(i);
            //优先级
            EditText et_order = (EditText) layout.getChildAt(2);
            et_order.setText(layoutParams.getOrderIndex() + "");
            //最小字数
            EditText et_min_size = (EditText) layout.getChildAt(6);
            et_min_size.setText(layoutParams.getEllipsesMinSize() + "");
            //压缩模式
            RadioGroup radioGroup = (RadioGroup) layout.getChildAt(4);
            RadioButton rb0 = (RadioButton) radioGroup.getChildAt(0);
            RadioButton rb1 = (RadioButton) radioGroup.getChildAt(1);
            switch (layoutParams.getCompressMode()) {
                case HIDE:
                    rb0.performClick();
                    break;
                default:
                    rb1.performClick();
                    break;
            }

            int finalI = i;
            clickListeners[i] = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置相关参数
                    layoutParams.setOrderIndex(Integer.parseInt(et_order.getText().toString()));
                    layoutParams.setEllipsesMinSize(Integer.parseInt(et_min_size.getText().toString()));
                    layoutParams.setCompressMode(rb0.isChecked() ? TRSOrderLayout.OrderLayoutParams.CompressMode.HIDE : TRSOrderLayout.OrderLayoutParams.CompressMode.ELLIPSES);
                    orderLayout.getChildAt(finalI).setLayoutParams(layoutParams);
                }
            };


        }
        findViewById(R.id.btn_set).setOnClickListener(v -> {
            for (int i = 0; i < clickListeners.length; i++) {
                clickListeners[i].onClick(null);
            }
        });
    }


    private void hideView(View view) {
        if (view == null) {
            return;
        }

        ViewParent viewParent = view.getParent();
        if (viewParent instanceof TRSOrderLayout) {
            TRSOrderLayout orderLayout = (TRSOrderLayout) viewParent;
            orderLayout.hideView(view);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void showView(View view) {
        if (view == null) {
            return;
        }

        ViewParent viewParent = view.getParent();
        if (viewParent instanceof TRSOrderLayout) {
            TRSOrderLayout orderLayout = (TRSOrderLayout) viewParent;
            orderLayout.showView(view);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void changeSize(int size) {
        ViewGroup.LayoutParams layoutParams = orderLayout.getLayoutParams();
        layoutParams.width += size;
        orderLayout.setLayoutParams(layoutParams);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}