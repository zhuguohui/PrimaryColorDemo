package com.example.orderlayoutdemo.view;

import android.content.Context;
import android.content.res.TypedArray;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.example.orderlayoutdemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在布局时，优先考虑优先级高的view。
 * 如果当前剩余空间不足以显示当前级别的空间，则隐藏当前级别的控件
 */
public class TRSOrderLayout extends LinearLayout {

    Map<View, String> contentMap = new HashMap<>();
    View setView;//正在设置text的view
    Set<View> hideViewSet = new HashSet<>();

    private List<TextView> orderView = new ArrayList<>();

    public TRSOrderLayout(Context context) {
        this(context, null);
    }

    public TRSOrderLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TRSOrderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof TextView) {
            ((TextView) child).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (setView == child) {
                        return;
                    }
                    contentMap.put(child, ((TextView) child).getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            contentMap.put(child, ((TextView) child).getText().toString());
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        orderView.clear();
        getOrderView(orderView);


        int totalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        boolean hideAll = false;
        for (TextView textView : orderView) {
            if (hideViewSet.contains(textView)) {
                textView.setVisibility(GONE);
                continue;
            }
            if (hideAll) {
                textView.setVisibility(GONE);
                continue;
            }
            OrderLayoutParams orderParams = (OrderLayoutParams) textView.getLayoutParams();
            int marginStart = 0;
            int marginEnd = 0;

            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            if (layoutParams instanceof MarginLayoutParams) {
                marginStart = ((MarginLayoutParams) layoutParams).leftMargin;
                marginEnd = ((MarginLayoutParams) layoutParams).rightMargin;
            }
            int needWidth = measureTextViewLength(textView);
            if (needWidth > totalWidth) {

                //判断压缩模式
                if (orderParams.getCompressMode() == OrderLayoutParams.CompressMode.ELLIPSES) {
                    //省略号模式
                    CharSequence ellipsizeStr = TextUtils.ellipsize(contentMap.get(textView),
                            textView.getPaint(),
                            totalWidth - marginStart - marginEnd,
                            TextUtils.TruncateAt.END);
                    //判断省略后显示的字数和属性设置的值是否匹配，加1是考虑省略号的长度
                    if (ellipsizeStr.length() < (orderParams.getEllipsesMinSize() + 1)) {
                        textView.setVisibility(GONE);
                    } else {
                        setView = textView;
                        textView.setText(ellipsizeStr);
                        setView = null;
                        textView.setVisibility(VISIBLE);
                    }

                } else {
                    //隐藏模式
                    textView.setVisibility(GONE);
                }

                hideAll = true;
            } else {

                textView.setText(contentMap.get(textView));
                textView.setVisibility(VISIBLE);


                totalWidth -= needWidth;
            }


        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void hideView(View view) {
        hideViewSet.add(view);
        requestLayout();
    }

    public void showView(View view) {
        hideViewSet.remove(view);
        requestLayout();
    }

    /**
     * 获取textView最原始的内容。
     *
     * @param textView
     * @return
     */
    public String getOriginContent(View textView) {
        return contentMap.get(textView);
    }

    /**
     * 对所有的view进行排序
     *
     * @param orderView
     */
    private void getOrderView(List<TextView> orderView) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextView) {
                orderView.add((TextView) getChildAt(i));
            } else {
                throw new RuntimeException("the view that index is " + i + "  is not TextView,The orderLayout just support TextView");
            }
        }
        Collections.sort(orderView, (o1, o2) -> {
            OrderLayoutParams o1p = (OrderLayoutParams) o1.getLayoutParams();
            OrderLayoutParams o2p = (OrderLayoutParams) o2.getLayoutParams();
            return o2p.orderIndex - o1p.orderIndex;
        });

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    /**
     * 计算textview宽度（包含margin padding）
     *
     * @param textView
     * @return
     */
    private int measureTextViewLength(TextView textView) {

        MarginLayoutParams lp = (MarginLayoutParams) textView.getLayoutParams();
        return (int) (textView.getPaint().measureText(contentMap.get(textView).toString())
                + textView.getCompoundPaddingStart()
                + textView.getCompoundPaddingEnd()
                + lp.getMarginStart()
                + lp.getMarginEnd());
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof OrderLayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new OrderLayoutParams(getContext(), attrs);
    }

    @Override
    protected OrderLayoutParams generateDefaultLayoutParams() {
        return new OrderLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected OrderLayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new OrderLayoutParams(lp);
    }


    public static class OrderLayoutParams extends LayoutParams {
        private int orderIndex = 1;
        private CompressMode compressMode = CompressMode.HIDE;
        private int ellipsesMinSize = 1;


        public int getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(int orderIndex) {
            this.orderIndex = orderIndex;
        }

        public CompressMode getCompressMode() {
            return compressMode;
        }

        public void setCompressMode(CompressMode compressMode) {
            this.compressMode = compressMode;
        }

        public int getEllipsesMinSize() {
            return ellipsesMinSize;
        }

        public void setEllipsesMinSize(int ellipsesMinSize) {
            this.ellipsesMinSize = ellipsesMinSize;
        }

        public OrderLayoutParams(ViewGroup.LayoutParams lp) {
            super(lp);
        }

        public OrderLayoutParams(int matchParent, int matchParent1) {
            super(matchParent, matchParent1);
        }

        public enum CompressMode {
            HIDE, ELLIPSES
        }

        public OrderLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.TRSOrderLayout_layout);
            orderIndex = ta.getInt(R.styleable.TRSOrderLayout_layout_layout_order_index, 1);
            ellipsesMinSize = ta.getInt(R.styleable.TRSOrderLayout_layout_layout_ellipses_min_size, 1);
            int mode = ta.getInt(R.styleable.TRSOrderLayout_layout_layout_compress_mode, 0);
            compressMode = mode == 0 ? CompressMode.HIDE : CompressMode.ELLIPSES;
            ta.recycle();
        }
    }

}
