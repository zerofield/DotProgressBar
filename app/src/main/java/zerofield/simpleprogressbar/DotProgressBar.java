package zerofield.simpleprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 点式进度条
 */
public class DotProgressBar extends View {

    private static final String TAG = DotProgressBar.class.getSimpleName();
    private static final int DEFAULT_MIN_VALUE = 20;
    private static final int DEFAULT_STEP = 20;
    private static final int DEFAULT_SEGMENT_COUNT = 4;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xfffce324;
    private static final int DEFAULT_FOREGROUND_COLOR = 0xffff8943;

    private float mDensity;

    /**
     * 最小值
     */
    private int mMinVal;

    /**
     * 最大值
     */
    private int mMaxVal;

    /**
     * 每段的长度
     */
    private int mStep;

    /**
     * 分段数量
     */
    private int mSegmentCount;

    /**
     * 进度值
     */
    private int mValue;

    /**
     * 背景色
     */
    private int mBackgroundColor;

    /**
     * 前景色
     */
    private int mForegroundColor;

    /**
     * 圆形半径大小
     */
    private int mCircleRadius;

    /**
     * 白圈半径大小
     */
    private int mInnerCircleRadius;

    /**
     * 每一段进度的高度
     */
    private int mSegmentHeight;

    /**
     * 文字大小
     */
    private float mTextSize;

    /**
     * 文字颜色
     */
    private int mTextColor;

    /**
     * 文字上边距
     */
    private int mTextMarginTop;

    private final Rect mRect = new Rect();


    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DotProgressBar(Context context) {
        this(context, null);
    }

    public DotProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(context, attrs);
        init(context);
    }

    private void initValue(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dot_progress);

        mMinVal = typedArray.getInt(R.styleable.dot_progress_min_value, DEFAULT_MIN_VALUE);
        mStep = typedArray.getInt(R.styleable.dot_progress_step, DEFAULT_STEP);
        mSegmentCount = typedArray.getInt(R.styleable.dot_progress_segment_count, DEFAULT_SEGMENT_COUNT);
        mValue = typedArray.getInt(R.styleable.dot_progress_value, mMinVal);
        mBackgroundColor = typedArray.getColor(R.styleable.dot_progress_background_color, DEFAULT_BACKGROUND_COLOR);
        mForegroundColor = typedArray.getColor(R.styleable.dot_progress_foreground_color, DEFAULT_FOREGROUND_COLOR);
        mTextColor = typedArray.getColor(R.styleable.dot_progress_text_color, Color.BLACK);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.dot_progress_text_size, 10);
        mTextMarginTop = typedArray.getDimensionPixelSize(R.styleable.dot_progress_text_margin_top, 0);
        mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.dot_progress_circle_radius, 30);

        typedArray.recycle();

        mStep = Math.max(1, mStep);
        mSegmentCount = Math.max(1, mSegmentCount);
        mMaxVal = mMinVal + mSegmentCount * mStep;
        mInnerCircleRadius = mCircleRadius >> 1;
        mSegmentHeight = mCircleRadius / 3;
    }

    private void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mPaint.setTextSize(mTextSize);
        String text = "100";
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        int height = mCircleRadius * 2 + mTextMarginTop + mRect.height() + getPaddingTop() + getPaddingBottom() + (int) mDensity;

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        int innerWidth = width - paddingLeft - paddingRight - 2 * mCircleRadius;
        int segmentLength = innerWidth / mSegmentCount;

        int x;
        int y;
        int startX = paddingLeft + mCircleRadius;
        int endX = width - paddingRight - mCircleRadius;

        //绘制背景线条
        int segmentY = paddingTop + mCircleRadius - (mSegmentHeight >> 1);
        mRect.set(startX, segmentY, endX, segmentY + mSegmentHeight);
        mPaint.setColor(mBackgroundColor);
        canvas.drawRect(mRect, mPaint);

        //可见部分的线条长度
        int compX = (int) mDensity;//弥补计算中发生的数值损失
        int invisibleSegmentLength = (int) Math.sqrt(mCircleRadius * mCircleRadius + mSegmentHeight * mSegmentHeight / 4) - compX;
        int visibleSegmentLength = (segmentLength - 2 * invisibleSegmentLength);

        //循环绘制点
        for (int i = 0; i < mSegmentCount + 1; ++i) {
            x = startX + i * segmentLength;
            y = paddingTop + mCircleRadius;

            //绘制背景球
            int pointValue = mMinVal + i * mStep;
            //对于小于当前值的球都使用前景值
            if (mValue >= pointValue) {
                mPaint.setColor(mForegroundColor);
            } else {
                mPaint.setColor(mBackgroundColor);
            }
            canvas.drawCircle(x, y, mCircleRadius, mPaint);

            //绘制细的进度跳
            if (i < mSegmentCount) {
                if (mValue >= pointValue) {
                    float percent = Math.min(1.0f, 1.0f * (mValue - pointValue) / (mStep - 1));
                    mPaint.setColor(mForegroundColor);

                    mRect.set(x + invisibleSegmentLength, segmentY, (int) (x + invisibleSegmentLength + percent * visibleSegmentLength), segmentY + mSegmentHeight);
                    canvas.drawRect(mRect, mPaint);
                }
            }

            //绘制两端的白球
            if (i == 0 || i == mSegmentCount) {
                mPaint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, mInnerCircleRadius, mPaint);
            }

            //绘制进度文字
            String text = "" + pointValue;
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            int textX = x - (mRect.width() >> 1);
            int textY = getPaddingTop() + 2 * mCircleRadius + mTextMarginTop + mRect.height();
            mPaint.setColor(mTextColor);
            canvas.drawText(text, textX, textY, mPaint);
        }
    }

    public void setValue(int value) {
        value = Math.max(mMinVal, Math.min(mMaxVal, value));
        this.mValue = value;
        Log.d(TAG, "setValue: value = " + value);
        invalidate();
    }

    public int getValue() {
        return mValue;
    }
}
