package com.canadianopendataexperience.datadrop.code2014;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by smcintyre on 01/03/14.
 */
public class BackgroundView extends View {
    private Paint p;
    private static float[] hsvDark, hsvBright;

    public BackgroundView(Context context) {
        super(context);
        init();
    }

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);

        //set up color
        //choose a random base color
        hsvDark = new float[3];
        hsvBright = new float[3];
        hsvDark[0] = 185;
        hsvBright[0] = 185;
        hsvDark[1] = 1;
        hsvBright[1] = 1;
        hsvDark[2] = 0.28f;
        hsvBright[2] = 0.8f;
    }

    private void updateColour() {
        //TODO animation with pulsing?
        RadialGradient gradient = new RadialGradient(this.getWidth(), this.getHeight(), Math.max(this.getWidth(), this.getHeight()), Color.HSVToColor(hsvDark),
                Color.HSVToColor(hsvBright), android.graphics.Shader.TileMode.CLAMP);
        p.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas c) {
        this.updateColour();
        c.drawColor(Color.HSVToColor(hsvBright));
        c.drawCircle(this.getWidth(), this.getHeight(), Math.max(this.getWidth(), this.getHeight()), p);
    }
}
