package com.loreaudio.lore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Integer buttonId = intent.getIntExtra("downloadButton", R.id.download);
        Button b = (Button)((Activity)context).findViewById(R.id.download);
        b.setVisibility(View.VISIBLE);
    }
}
