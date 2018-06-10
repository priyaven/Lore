package com.loreaudio.lore;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CircleGraphActivity extends AppCompatActivity {

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

        @SuppressLint("WrongViewCast")
        RelativeLayout lyt = (RelativeLayout) findViewById(R.id.layoutid);
        Button b = new Button(this);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        b.setLayoutParams(lp1);
        b.setText("wheeeeeeee");
        b.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle));
        lyt.addView(b);

        ImageView imgv = new ImageView(this);
        GradientDrawable ringa = new GradientDrawable();
        ringa.setShape(GradientDrawable.OVAL);
        ringa.setColor(Color.TRANSPARENT);
        ringa.setStroke(2, Color.GRAY);
        imgv.setBackground(ringa);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(500, 500);

        lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgv.setLayoutParams(lp2);
        lyt.addView(imgv);



    }

}
