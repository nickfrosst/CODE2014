package com.canadianopendataexperience.datadrop.code2014;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by smcintyre on 01/03/14.
 */
public class PageIndicatorView extends View {

    private Paint whitePaint;
    int pageIndex, totalPages, tickRadius, tickPadding;


    public PageIndicatorView(Context context) {
        super(context);
        init();
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        tickRadius = this.getContext().getResources().getDimensionPixelSize(R.dimen.view_pager_tick_radius);
        tickPadding = this.getContext().getResources().getDimensionPixelOffset(R.dimen.view_pager_tick_padding);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setDither(true);
        whitePaint.setAntiAlias(true);
    }

    public void setPageInfo(int pageIndex, int totalPages) {
        this.pageIndex = pageIndex;
        this.totalPages = totalPages;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int totalWidth = totalPages*tickRadius + (totalPages-1)*tickPadding;
        int currX = Math.round(this.getWidth()/2f-totalWidth/2f) + tickRadius;
        int currY = this.getHeight()/2;
        for (int i=0;i<totalPages;i++) {
            if (i != this.pageIndex) {
                whitePaint.setAlpha(50);
                canvas.drawCircle(currX, currY, tickRadius, whitePaint);
            } else {
                whitePaint.setAlpha(255);
                canvas.drawCircle(currX, currY, tickRadius, whitePaint);
            }
            currX += tickRadius+tickPadding;
        }
    }
}
