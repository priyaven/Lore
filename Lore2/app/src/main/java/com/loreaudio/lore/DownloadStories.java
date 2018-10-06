package com.loreaudio.lore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class DownloadStories
{
    //https://github.com/mdkess/ProgressBarListView/tree/master/src/ca/kess/demo
    //String url = "https://s3-us-west-1.amazonaws.com/loreaudio/story_";
    Story mStory;
    Context context;


    public DownloadStories()
    {

    }

    public DownloadStories(Story story)
    {
        mStory = story;
    }


    public void downloadStory(String storyUrl, String path, Context ctx, ProgressBar bar)
    {
        context = ctx;
        Download download = new Download(ctx, bar);
        download.execute(storyUrl, path);
    }

    public void downloadStory(String storyUrl, String path, Context ctx) {
        context = ctx;
        Download download = new Download(ctx, null);
        download.execute(storyUrl, path);
    }


    public void downloadWait(String storyUrl, String path, Context ctx) throws ExecutionException, InterruptedException {
        // Blocking download, so the method calling this will wait until download completes.
        context = ctx;
        Download download = new Download(ctx, null);
        Object result = download.execute(storyUrl, path).get();
    }

    private InputStream openHTTPConnection(String myurl)
            throws IOException
    {
        InputStream is = null;
        int checkConn = -1;
        URL url = new URL(myurl);
        URLConnection conn = url.openConnection();
        try
        {
            HttpURLConnection httpCon = (HttpURLConnection) conn;
            httpCon.setAllowUserInteraction(false);
            httpCon.setInstanceFollowRedirects(true);
            httpCon.setRequestMethod("GET");
            httpCon.connect();
            checkConn = httpCon.getResponseCode();
            if(checkConn == HttpURLConnection.HTTP_OK)
            {
                is = httpCon.getInputStream();
            }
            else
            {
                httpCon.disconnect();
            }
        }
        catch (Exception e)
        {
            throw new IOException("Error Connecting");
        }
        return (is);
    }

    private class Download extends AsyncTask<String, Integer, String> {

        private ProgressDialog progressDialog;
        ProgressBar bar;

        public Download(Context ctx, ProgressBar bar)
        {
            super();
            this.progressDialog = new ProgressDialog(ctx);
            this.bar = bar;
        }

        @Override
        protected void onProgressUpdate(Integer...values)
        {
            //mStory.setProgress(values[0]);
            //bar = mStory.getProgressBar();
//            if (bar != null)
//            {
//                //bar.setProgress(mStory.getProgress());
//                bar.invalidate();
//            }
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
//            progressDialog.setMessage("Doing something, please wait.");
//            progressDialog.show();
            if(bar != null) {
                bar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(bar != null) {
                bar.setVisibility(View.GONE);
            }
            Button b = (Button)((Activity)context).findViewById(R.id.download);
            b.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                  String loreDir = Environment.getExternalStorageDirectory()+ File.separator + strings[1];
                  File target = new File(loreDir);
                  Log.i("In doinbackground","Target file:" + loreDir);
                  Log.i("In doinbackground","Url:" + strings[0]);

                File parent = target.getParentFile();

                  if (!parent.exists() && !parent.mkdirs())
                  {
                      throw new IllegalStateException("Couldn't create directory: " + parent);
                  }

//                String storyDir = loreDir + File.separator + strings[1];
//                String chapDir = storyDir + File.separator + strings[2];
//                File mainDirectory = new File(loreDir);
//                File subDirectory = new File(storyDir);
//                File chapDirectory = new File(chapDir);
//
//                //create folder if it doesn't exist
//                if (!mainDirectory.exists())
//                {
//                    File folder = new File(loreDir); //folder name
//                    folder.mkdirs();
//                }
//
//                if (!subDirectory.exists())
//                {
//                    File folder = new File(storyDir);
//                    folder.mkdirs();
//                }
//
//                if (!chapDirectory.exists())
//                {
//                    File folder = new File(chapDir);
//                    folder.mkdirs();
//                }

                InputStream is = openHTTPConnection(strings[0]);
                FileOutputStream os = new FileOutputStream(target);
                byte[] buf = new byte[4096];

                int len;
                int total = 0;
                while ((len = is.read(buf)) > 0)
                {
                    os.write(buf, 0, len);
                    publishProgress(total);
                    total += 1;
                }
                os.flush();
                os.close();
                is.close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return "Finished downloading!";
        }
    }
}
