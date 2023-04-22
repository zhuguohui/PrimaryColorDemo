package com.example.orderlayoutdemo.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EllipsizeTextView extends androidx.appcompat.widget.AppCompatTextView {
    CharSequence originString;
    boolean setByProgram;
    boolean ellipsizeEnable = false;
    int originWidth;
    private int needWidth;


    public EllipsizeTextView(@NonNull Context context) {
        this(context, null);
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setEllipsizeEnable(boolean ellipsizeEnable) {
        this.ellipsizeEnable = ellipsizeEnable;
        if (!ellipsizeEnable) {
            setText(originString);

        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(Math.min(widthSize, needWidth), getMeasuredHeight());
        }


        if (ellipsizeEnable) {
            int realWidth = getMeasuredWidth();
            super.onMeasure(0, 0);

            if (realWidth < needWidth) {
                CharSequence ellipsizeStr = TextUtils.ellipsize(originString,
                        getPaint(),
                        realWidth,
                        TextUtils.TruncateAt.END);
                setByProgram = true;
                setText(ellipsizeStr);
                setByProgram = false;
            } else {
                setText(originString);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


    }


    /**
     * 计算textview宽度（包含margin padding）
     *
     * @return
     */
    private int measureTextViewLength() {
        return (int) (getPaint().measureText(originString.toString())
                + getCompoundPaddingStart()
                + getCompoundPaddingEnd());

    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!setByProgram) {
            originString = text;
            needWidth = measureTextViewLength();
        }
    }
}
