package com.byph.lib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.byph.expandableview.R;

/**
 * @author assen
 * @date 2019/6/18
 */
public class ExpandableView extends LinearLayout {

    /**
     * 使用TextView.getLineTop返回TextView 的行数
     * 继而拿到每一行字的高度，通过平移动画，动态的改变View的高度
     */

    // 标题view
    private TextView titleView;

    // 内容view
    private TextView contentView;

    // 箭头view
    private ImageView arrowView;

    // 标题颜色
    private int titleTextColor;

    // 内容颜色
    private int contentTextColor;

    // 标题字体
    private float titleTextSize;

    // 内容字体
    private float contentTextSize;

    // 是否处于收起状态
    private boolean isCollapsed = true;

    // 收缩动画时间
    private int duration = 0;

    // 文本框真实高度
    private int realTextViewHeight = 0;

    // 收起时候的整体高度
    private int collapsedHeight = 0;

    // 是否正在执行动画
    private boolean isAnimate = false;

    // 是否发生过文字变动
    boolean isChange = false;

    // 是否默认收起，每次都强制设置成折叠或展开
    private boolean isCutLayout = true;

    private Context mContext;

    private OnExpandStateChangeListener listener;

    public ExpandableView(Context context) {
        this(context, null);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        setOrientation(VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableViewAttr);
        duration = array.getInteger(R.styleable.ExpandableViewAttr_duration, 500);
        titleTextColor = array.getColor(R.styleable.ExpandableViewAttr_title_text_color, Color.parseColor("#172641"));
        contentTextColor = array.getColor(R.styleable.ExpandableViewAttr_content_text_color, Color.parseColor("#707A8D"));
        titleTextSize = array.getDimension(R.styleable.ExpandableViewAttr_title_text_size, 18);
        contentTextSize = array.getDimension(R.styleable.ExpandableViewAttr_content_text_size, 14);
        isCutLayout = array.getBoolean(R.styleable.ExpandableViewAttr_is_collapsed, true);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 映射完成，初始化view

        // 标题container
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(30, 0, 30, 0);

        // 标题view
        titleView = new TextView(mContext);
        titleView.setPadding(0, 30, 0, 30);
        titleView.setTextColor(titleTextColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize);

        // 箭头view
        arrowView = new ImageView(mContext);
        arrowView.setImageResource(R.mipmap.ic_arrow_down);

        // 内容view
        contentView = new TextView(mContext);
        contentView.setPadding(30, 0, 30, 30);
        contentView.setTextColor(contentTextColor);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentTextSize);

        linearLayout.addView(titleView);
        linearLayout.addView(arrowView);

        // 标题view 设置权重
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        titleView.setLayoutParams(layoutParams);

        addView(linearLayout);
        addView(contentView);

        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startExpandAnimation();
            }
        });
    }

    private void startExpandAnimation() {

        ExpandCollapseAnimation animation;

        isCollapsed = !isCollapsed;
        if (isCollapsed) {
            if (listener != null) {
                listener.onExpandStateChanged(true);
            }

            ObjectAnimator.ofFloat(arrowView, "rotation", 180f, 0f).start();
            animation = new ExpandCollapseAnimation(getHeight(), collapsedHeight);
        } else {
            if (listener != null) {
                listener.onExpandStateChanged(false);
            }

            ObjectAnimator.ofFloat(arrowView, "rotation", 0f, 180f).start();
            animation = new ExpandCollapseAnimation(getHeight(), realTextViewHeight + collapsedHeight);
        }
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimate = false;
                clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        clearAnimation();
        startAnimation(animation);
    }

    /**
     * 赋值
     *
     * @param title
     * @param content
     * @return
     */
    public void setDatas(String title, String content) {
        isChange = true;
        isCutLayout = true;

        titleView.setText(title);
        contentView.setText(content);

        requestLayout();
        clearAnimation();
    }

    /**
     * 设置是否收起（处理listview复用导致的状态错误）
     * 逻辑有些杂
     *
     * @param isCollapsed
     * @return
     */
    public void setIsCollapsedR(boolean isCollapsed) {
        isChange = true;
        isCutLayout = isCollapsed;

        requestLayout();
    }

    /**
     * 设置是否收起（带动画）
     *
     * @param isCollapsed
     * @return
     */
    public void setIsCollapsed(boolean isCollapsed) {
        if (isCollapsed == this.isCollapsed) {
            return;
        }
        this.isCollapsed = isCollapsed;

        post(new Runnable() {
            @Override
            public void run() {
                startExpandAnimation();
            }
        });
    }

    /**
     * 设置是否收起（带动画）
     *
     * @param isCollapsed
     * @return
     */
    public void setIsCollapsed(boolean isCollapsed, int duration) {
        if (isCollapsed == this.isCollapsed) {
            return;
        }
        this.isCollapsed = isCollapsed;

        postDelayed(new Runnable() {
            @Override
            public void run() {
                startExpandAnimation();
            }
        }, duration);
    }

    /**
     * getIsCollapsed
     *
     * @return
     */
    public boolean getIsCollapsed() {
        return isCollapsed;
    }

    /**
     * 是否展开监听
     *
     * @param listener
     * @return
     */
    public void setOnExpandStateChangeListener(OnExpandStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //执行动画的过程中屏蔽事件
        return isAnimate;
    }

    private static final String TAG = "ExpandableView";

    private int count = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isChange) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        Log.d(TAG, "onMeasure" + ++count);

        isChange = false;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //初始化高度赋值，为后续动画事件准备数据
        realTextViewHeight = getRealTextViewHeight(contentView);
        collapsedHeight = getRealTextViewHeight(titleView);

        if (isCutLayout) {
            isCollapsed = true;
            isCutLayout = false;
            this.post(new Runnable() {
                @Override
                public void run() {
                    arrowView.setRotation(0);

                    // 高度
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = collapsedHeight;
                    setLayoutParams(layoutParams);
                }
            });
        } else {
            isCollapsed = false;
            this.post(new Runnable() {
                @Override
                public void run() {
                    arrowView.setRotation(180);
                    contentView.setMaxHeight(realTextViewHeight);

                    // 高度
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = realTextViewHeight + collapsedHeight;
                    setLayoutParams(layoutParams);
                }
            });
        }
    }

    /**
     * 获取textview的真实高度
     *
     * @param textView
     * @return
     */
    private int getRealTextViewHeight(TextView textView) {
        //getLineTop返回值是一个根据行数而形成等差序列，如果参数为行数，则值即为文本的高度
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        return textHeight + textView.getCompoundPaddingBottom() + textView.getCompoundPaddingTop();
    }

    private class ExpandCollapseAnimation extends Animation {
        int startValue;
        int endValue;

        ExpandCollapseAnimation(int startValue, int endValue) {
            setDuration(duration);
            this.startValue = startValue;
            this.endValue = endValue;
        }

        // 处理动画变化的过程，系统会高频率调用
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            int height = (int) ((endValue - startValue) * interpolatedTime + startValue);
            contentView.setMaxHeight(height - collapsedHeight);
            ExpandableView.this.getLayoutParams().height = height;
            ExpandableView.this.requestLayout(); // 重新绘制View
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public interface OnExpandStateChangeListener {
        void onExpandStateChanged(boolean isExpanded);
    }
}
