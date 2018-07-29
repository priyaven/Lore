package com.loreaudio.lore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadStories
{

    //String url = "https://s3-us-west-1.amazonaws.com/loreaudio/story_";

    public void downloadStory(String storyUrl, String path, Context ctx)
    {
        Download download = new Download(ctx);
        download.execute(storyUrl, path);
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

        public Download(Context ctx) {
            super();
            this.progressDialog = new ProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.setMessage("Doing something, please wait.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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

                while ((len = is.read(buf)) > 0)
                {
                    os.write(buf, 0, len);
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
