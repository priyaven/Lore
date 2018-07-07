package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        /*int[] lox = new int[2];
        imgv.getLocationInWindow(lox);
        int imgvX = lox[0];
        int imgvY = lox[1];*/

        final Button b2 = new Button(this);
        final Button b3 = new Button(this);

        b2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

        //size of button
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

        //x is bottom left edge of ringa
        //params.leftMargin = imgvX - buttonsize/3;
        //params.topMargin = imgvY;
        b2.setLayoutParams(params);
        b2.setX(width/2  + ringsize/2 -buttonsize/2);
        b2.setY(height/2 - marginy);
        lyt.addView(b2);


        b2.setText("test1");

        b3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

        //size of button
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

        //x is bottom left edge of ringa
        //params3.leftMargin = imgvX - buttonsize/3;
        //params3.topMargin = imgvY;
        b3.setLayoutParams(params3);
        b3.setX(width/2 - buttonsize/2 - ringsize/2);
        b3.setY(height/2 - marginy);
        lyt.addView(b3);


        b3.setText("test2");

        /*lyt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                lyt.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                imgv.getLocationOnScreen(locations);
                int x = locations[0];
                int y = locations[1];

                b2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

                //size of button
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

                //x is bottom left edge of ringa
                params.leftMargin = x - buttonsize/3;
                params.topMargin = y;
                b2.setLayoutParams(params);
                lyt.addView(b2);


                b2.setText("test");

                b3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));

                //size of button
                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(buttonsize, buttonsize);

                //x is bottom left edge of ringa
                params3.leftMargin = x - buttonsize/3;
                params3.topMargin = y;
                b2.setLayoutParams(params3);
                lyt.addView(b3);


                b3.setText("reee");
            }
        });
        */


    }
}
