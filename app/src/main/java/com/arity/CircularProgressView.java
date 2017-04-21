package com.arity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ravikanth on 4/20/2017.
 */

public class CircularProgressView extends View {

    private final int default_finished_color;
    private final int default_unfinished_color;
    private final float default_arc_angle = 360;
    private final float default_stroke_width;
    private final int min_size;
    private final float default_primary_text_size;
    private final int default_secondary_text_color;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float progress;
    private int default_max = 100;
    private float arcAngle;
    private float strokeWidth;
    private int max;
    private Paint paint;

    private RectF rectF = new RectF();
    private float default_secondary_text_size;
    private float primaryTextSize;
    private float secondaryTextSize;
    private int primaryTextColor;
    private int default_primary_text_color;
    private String secondaryText;
    private String primaryText;
    private int secondaryTextColor;
    private TextPaint textPaint;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_finished_color = Color.parseColor("#2a7ee3");
        default_unfinished_color = Color.parseColor("#bfd8f6");
        min_size = (int) Utils.dp2px(getResources(), 100);
        default_stroke_width = Utils.dp2px(getResources(), 4);
        default_primary_text_size = Utils.sp2px(getResources(), 40);
        default_secondary_text_size = Utils.sp2px(getResources(), 15);
        default_primary_text_color = Color.parseColor("#2a7ee3");
        default_secondary_text_color = Color.parseColor("#0055c3");

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularProgressView, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.CircularProgressView_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.CircularProgressView_arc_unfinished_color, default_unfinished_color);
        arcAngle = attributes.getFloat(R.styleable.CircularProgressView_arc_angle, default_arc_angle);
        setMax(attributes.getInt(R.styleable.CircularProgressView_arc_max, default_max));
        setProgress(attributes.getFloat(R.styleable.CircularProgressView_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.CircularProgressView_arc_stroke_width, default_stroke_width);

        primaryTextColor = attributes.getColor(R.styleable.CircularProgressView_arc_primary_text_color, default_primary_text_color);
        primaryTextSize = attributes.getDimension(R.styleable.CircularProgressView_arc_primary_text_size, default_primary_text_size);
        primaryText = attributes.getString(R.styleable.CircularProgressView_arc_primary_text);

        secondaryTextColor = attributes.getColor(R.styleable.CircularProgressView_arc_secondary_text_color, default_secondary_text_color);
        secondaryTextSize = attributes.getDimension(R.styleable.CircularProgressView_arc_secondary_text_size, default_secondary_text_size);
        secondaryText = attributes.getString(R.styleable.CircularProgressView_arc_secondary_text);

    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(default_unfinished_color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (this.progress > 100) {
            this.progress %= 100;
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f,
                MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 270;
        float finishedSweepAngle = progress / (float) getMax() * arcAngle;
        float finishedStartAngle = startAngle;
        if (progress == 0) finishedStartAngle = 0.01f;
        paint.setColor(unfinishedStrokeColor);
        canvas.drawArc(rectF, 270, arcAngle, false, paint);
        paint.setColor(finishedStrokeColor);
        canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint);

        int yPos = 0;
        if (!TextUtils.isEmpty(primaryText)) {
            textPaint.setTextSize(primaryTextSize);
            textPaint.setColor(primaryTextColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText(primaryText, canvas.getWidth() / 2, yPos, textPaint);
        }
        if (!TextUtils.isEmpty(secondaryText)) {
            textPaint.setTextSize(secondaryTextSize);
            textPaint.setColor(secondaryTextColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            yPos += textPaint.descent() - textPaint.ascent();
            canvas.drawText(secondaryText, canvas.getWidth() / 2, yPos, textPaint);
        }
    }

}
