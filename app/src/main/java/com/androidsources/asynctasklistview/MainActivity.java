package com.androidsources.asynctasklistview;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {


    CustomListViewAdapter listViewAdapter;
    ListView listView;
    Button button;

    public static final String URL =
            "http://www.androidsources.com/wp-content/uploads/2015/09/Android-Login-and-Registration.png";
    public static final String URL1 =
            "http://www.androidsources.com/wp-content/uploads/2015/09/android-flashlight-app-tutorial.png";
    public static final String URL2 =
            "http://www.androidsources.com/wp-content/uploads/2015/08/banner1.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.imageList);
        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Creating and executing background task*/
                GetXMLTask task = new GetXMLTask(MainActivity.this);
                task.execute(new String[]{URL, URL1, URL2});
            }
        });


    }


    private class GetXMLTask extends AsyncTask<String, Integer, List<RowItem>> {
        ProgressDialog progressDialog;
        private Activity context;
        List<RowItem> rowItems;
        int noOfURLs;

        public GetXMLTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("In progress...");
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setIcon(R.drawable.ic_arrow_drop_down_circle_24dp);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected List<RowItem> doInBackground(String... urls) {
            noOfURLs = urls.length;
            rowItems = new ArrayList<RowItem>();
            Bitmap map = null;
            for (String url : urls) {
                //taking 1 url at a time and downloading
                map = downloadImage(url);
                //after downloading the bitmap is added to rowitems
                rowItems.add(new RowItem(map));
            }
            return rowItems;
        }

        private Bitmap downloadImage(String urlString) {

            int count = 0;
            Bitmap bitmap = null;

            URL url;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;

            try {
                url = new URL(urlString);
                URLConnection connection = url.openConnection();
                int lenghtOfFile = connection.getContentLength();

                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);

                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lenghtOfFile));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtils.close(inputStream);
                FileUtils.close(outputStream);
            }
            return bitmap;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
            if (rowItems != null) {
                progressDialog.setMessage("Loading " + (rowItems.size() + 1) + "/" + noOfURLs);
            }
        }

        @Override
        protected void onPostExecute(List<RowItem> rowItems) {
            //passing the row items to the custom listView adapter
            listViewAdapter = new CustomListViewAdapter(context, rowItems);
            listView.setAdapter(listViewAdapter);
            progressDialog.dismiss();
        }
    }
}