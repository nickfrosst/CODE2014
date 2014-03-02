package com.canadianopendataexperience.datadrop.code2014;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.canadianopendataexperience.datadrop.code2014.data.RelevantActResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smcintyre on 01/03/14.
 */
public class TagCloudView extends View {

    private RelevantActResult actResult;
    private float baseTextSize; //should be relative to canvas size
    private Paint textPaint, whitePaint, boldTextPaint;
    private Rect tempRect;
    private float minTextSize;
    private int minOpacity = 100;
    private int maxOpacity = 255;
    private float baseRelevance;
    private boolean [][] grid;
    private Rect titleRect;
    private String titleString;
    private float angle;
    private float curX;
    private float curY;
    //TODO a and b must be made pixel independent
    private float a;
    private float b;
    private float gridColumnWidth, gridColumnHeight;
    private int numHorizontalColumns = 100;
    private int numVerticalColumns = 100;

    public TagCloudView(Context context) {
        super(context);
        this.init(context);
    }

    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public TagCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    private void init(Context context) {
        tempRect = new Rect();

        //Do all your init here, including but not limited to:
        //- set up Paint for text
        textPaint = new TextPaint();
        textPaint.setColor(context.getResources().getColor(R.color.orange));
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        if (!this.isInEditMode()) {
            textPaint.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "fonts/Roboto-Bold.ttf"));
        }
        textPaint.setSubpixelText(true);

        boldTextPaint = new TextPaint(textPaint);
        boldTextPaint.setColor(Color.WHITE);
        if (!this.isInEditMode()) {
            boldTextPaint.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "fonts/Roboto-Bold.ttf"));
        }
        whitePaint = new TextPaint(textPaint);
        whitePaint.setColor(Color.WHITE);

    }

    public void setRelevantActResult(RelevantActResult actResult) {
        this.actResult = actResult;
        //TODO init free/occupied grid array here?

        //TODO compute tag cloud locations here?
        //TODO reset animations?
        this.invalidate(); //this tells android to start redrawing this view
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setRelevantActResult(this.actResult);

    }

    @Override
    protected void onDraw(Canvas c) {
        float width = this.getWidth();
        float height = this.getHeight();
        //compute base text size
        baseTextSize = Math.min(width, height)*0.1f;

        //clear the canvas for this frame, so that it's translucent
        c.drawColor(0x00000000, PorterDuff.Mode.OVERLAY);
        if(actResult != null && actResult.getRelevantWords().size() > 0){

            grid = new boolean[numHorizontalColumns][numVerticalColumns];
            gridColumnWidth = width/numHorizontalColumns;
            gridColumnHeight = height/numVerticalColumns;
            boolean widescreen = false;

            if (width > height){
                widescreen = true;
            }

            if(widescreen){
                baseTextSize = Math.min(width, height)*0.15f;
            }else{
                baseTextSize = Math.min(width, height)*0.1f;
            }

            float centerx = width / 2;
            float centery = height / 2;
            //draw act name at centre of canvas
            boldTextPaint.setAlpha(255);
            boldTextPaint.setTextSize(baseTextSize);

            while (boldTextPaint.measureText(actResult.getActName()) > width) {
                baseTextSize = baseTextSize*0.9f;
                boldTextPaint.setTextSize(baseTextSize);
            }

            boldTextPaint.getTextBounds(actResult.getActName(), 0, actResult.getActName().length(), tempRect);
            int titleHeight = tempRect.height();
            int titleWidth = tempRect.width();
            tempRect.left = Math.round(width/2 - titleWidth/2);
            tempRect.right = tempRect.left + titleWidth;
            tempRect.top = Math.round(height/2 - titleHeight/2);
            tempRect.bottom = tempRect.top + titleHeight;

            c.drawText(actResult.getActName(), tempRect.left, tempRect.bottom, boldTextPaint);
            Dirty(tempRect);
            titleRect = new Rect();
            titleRect.left = tempRect.left;
            titleRect.right = tempRect.right;
            titleRect.top = tempRect.top;
            titleRect.bottom = tempRect.bottom;
            titleString = actResult.getActName();

            Log.d("DEBUG",titleRect.left+" "+titleRect.right +" "+titleRect.top+" "+ titleRect.bottom);

            //TODO draw tag cloud on canvas using locations computed in setRelevanceResult()
            minTextSize = baseTextSize * 0.3f;
            baseRelevance = actResult.getRelevance().get(0);
            angle = 0;
            a = titleHeight/2;
            b = baseTextSize/6;
            float angle2 = 0;
            /*for (int j = 0; j < 720; j++) {
                angle = (float) 0.1 * j;
                float x;
                float y;
                if(widescreen){
                    x = (float) (centerx + (a + b * angle) * Math.cos(angle));
                    y = (float)(centery + ((a + b * angle) * Math.sin(angle))*(height/width));
                }else{
                    x = (float) (centerx + ((a + b * angle) * Math.cos(angle))*(width/height));
                    y = (float)(centery + (a + b * angle) * Math.sin(angle));
                }
               ;

                c.drawCircle(x, y, 1, textPaint);
            }*/

            List<String> relevantWords =  new ArrayList<String>();
            relevantWords.addAll(actResult.getRelevantWords());

            List<Integer> relevance = new ArrayList<Integer>();
            relevance.addAll(actResult.getRelevance());

            if (!relevantWords.contains(actResult.getQuery())){
                relevantWords.add(actResult.getQuery());
                relevance.add(actResult.getQueryRelevance());
            }

            for( int i = 0;  i < relevantWords.size();i++)
            {
                float curRelevance = (float)Math.sqrt(relevance.get(i)/baseRelevance);
                float curTextSize = minTextSize + (baseTextSize - minTextSize) * curRelevance;
                int curOpacity = (int)(minOpacity + (maxOpacity - minOpacity) * curRelevance);

                textPaint.setAlpha(curOpacity);
                textPaint.setTextSize(curTextSize);
                whitePaint.setAlpha(curOpacity);
                whitePaint.setTextSize(curTextSize);

                angle2 = 1;

                int marginal = 1;
                do  {
                    angle2 += marginal;
                    if (widescreen == true) {
                        curX = (float) (centerx + (a + b * angle2) * Math.cos(angle2));
                        curY = (float) (centery + ((a + b * angle2) * Math.sin(angle2)) * (height / width));
                    } else {
                        curX = (float) (centerx + ((a + b * angle2) * Math.cos(angle2)) * (width / height));
                        curY = (float) (centery + (a + b * angle2) * Math.sin(angle2));
                    }
                    textPaint.getTextBounds(relevantWords.get(i), 0, relevantWords.get(i).length(), tempRect);
                    float textWidth = tempRect.width();
                    float textHeight = tempRect.height();
                    tempRect.left = Math.round(curX - textWidth/2);
                    tempRect.right = Math.round(tempRect.left + textWidth);
                    tempRect.top = Math.round(curY - textHeight/2);
                    tempRect.bottom = Math.round(tempRect.top + textHeight);

                } while(doesOverlap(tempRect));
                if (actResult.getQuery().equals(relevantWords.get(i))){
                    c.drawText(relevantWords.get(i), tempRect.left, tempRect.bottom, whitePaint);
                }else{
                    c.drawText(relevantWords.get(i), tempRect.left, tempRect.bottom, textPaint);
                }
                Dirty(tempRect);
            }

            //drawDirty(c);
        }
    }
    private void Dirty(Rect curRect){
        Rect newRect = new Rect();
        transformRect(curRect, newRect);
        for(int x = Math.max(newRect.left, 0); x < Math.min(newRect.right, grid.length); x++ ){
            for(int y = Math.max(newRect.top,0); y < Math.min(newRect.bottom, grid[x].length); y++){
                grid[x][y] = true;
            }
        }

    }
    private boolean doesOverlap(Rect curRect){
        Rect newRect = new Rect();
        transformRect(curRect, newRect);
        if (newRect.left < 0 || newRect.right > getWidth()) return true;
        for(int x = Math.max(newRect.left, 0); x < Math.min(newRect.right, grid.length); x++ ){
            for(int y = Math.max(newRect.top,0); y < Math.min(newRect.bottom, grid[x].length); y++){
               if(grid[x][y]){
                   return true;
               }
            }
        }
        return false;
    }

    private void transformRect(Rect curRect, Rect collRect){
        collRect.left = (int)(curRect.left/ gridColumnWidth);
        collRect.right = (int)(curRect.right/ gridColumnWidth);
        collRect.top = (int)(curRect.top/ gridColumnHeight);
        collRect.bottom = (int)(curRect.bottom/ gridColumnHeight);
    }

    private void drawDirty(Canvas c){
        for (int i=0;i<grid.length;i++) {
            for (int j=0;j<grid[i].length;j++) {
                if (grid[i][j]) {
                    whitePaint.setAlpha(225);
                    c.drawRect(i* gridColumnWidth, j* gridColumnHeight,(i+1)* gridColumnWidth,(j+1)* gridColumnHeight, whitePaint);
                } else{
                    whitePaint.setAlpha(1);
                    c.drawRect(i* gridColumnWidth, j* gridColumnHeight,(i+1)* gridColumnWidth,(j+1)* gridColumnHeight, whitePaint);
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent tv){
        if (titleRect == null) return false;
        Log.d("DEBUG", tv.getAction()+"");
        switch (tv.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("DEBUG", "thing" + 1);
                final int action = tv.getAction();
                final float x  = tv.getX();
                final float y = tv.getY();
                Log.d("DEBUG",x +" "+ y+" "+titleRect.left+" "+titleRect.right +" "+titleRect.top+" "+ titleRect.bottom);
                if(x >= titleRect.left && x <= titleRect.right && y >= titleRect.top && y <= titleRect.bottom){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    titleString.replace(' ','+');
                    i.setData(Uri.parse("http://laws-lois.justice.gc.ca/Search/Search.aspx?txtS3archA11="+actResult.getQuery()+"&txtT1tl3=%22"+titleString+"%22&h1ts0n1y=0&ddC0nt3ntTyp3=Acts"));
                    Context context = getContext();
                    context.startActivity(i);
                }
                break;
            default:
                break;
        }
        return false;
    }

}
