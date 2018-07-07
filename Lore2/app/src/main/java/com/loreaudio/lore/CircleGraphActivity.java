package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CircleGraphActivity extends AppCompatActivity {

    DisplayMetrics displayMetrics = new DisplayMetrics();
    Context context = this;
    int marginy = 250;
    int marginx = 125;

    int margin = 10;

    int buttonsize = 0;
    int ringsize = 0;

    int numButtons = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isRoot = false;
        createNode(isRoot);

    }

    private void createNode(boolean isRoot){
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (height < width)
            buttonsize = (height)/4;
        else
            buttonsize = (width)/4;
        ringsize = 3*buttonsize;

        @SuppressLint("WrongViewCast")
        final RelativeLayout lyt = (RelativeLayout) findViewById(R.id.layoutid);
        Button b = new Button(this);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                buttonsize,
                buttonsize);

        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        b.setLayoutParams(lp1);
        b.setText("wheeeeeeee");
        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
        lyt.addView(b);

        final ImageView imgv = new ImageView(this);
        GradientDrawable ringa = new GradientDrawable();
        ringa.setShape(GradientDrawable.OVAL);
        ringa.setColor(Color.TRANSPARENT);
        ringa.setStroke(2, Color.GRAY);
        imgv.setBackground(ringa);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ringsize, ringsize);

        lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgv.setLayoutParams(lp2);
        lyt.addView(imgv);


        final Button b2 = new Button(this);
        final Button b3 = new Button(this);

        b2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

        //size of button
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

        b2.setLayoutParams(params);
        b2.setX(width/2  + ringsize/2 -buttonsize/2);
        b2.setY(height/2 - marginy);
        lyt.addView(b2);


        b2.setText("test1");

        b3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

        //size of button
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

        b3.setLayoutParams(params3);
        b3.setX(width/2 - buttonsize/2 - ringsize/2);
        b3.setY(height/2 - marginy);
        lyt.addView(b3);


        b3.setText("test2");

        if(!isRoot) {
            final Button bpar = new Button(this);
            bpar.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

            //size of button
            RelativeLayout.LayoutParams paramspar = new RelativeLayout.LayoutParams(buttonsize * 2, buttonsize * 2);

            bpar.setLayoutParams(paramspar);
            bpar.setX(0 - buttonsize);
            bpar.setY(0 - marginy);
            lyt.addView(bpar);


            bpar.setText("parent");

            ImageView line = new ImageView(this);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            line.setImageBitmap(bitmap);

            // Line
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(10);
            float startx = bpar.getX() + buttonsize*2 - buttonsize/3;
            float starty = bpar.getY()+ buttonsize*2 - buttonsize/3;
            int endx = width/2;
            int endy = height/2 - buttonsize/2 - margin;
            canvas.drawLine(startx, starty, endx, endy, paint);
            lyt.addView(line);

        }



    }
}
