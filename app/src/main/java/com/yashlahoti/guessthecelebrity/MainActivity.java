package com.yashlahoti.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String siteContent=null;
    int correctlocation,correctCeleb;
    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celebName = new ArrayList<String >();
    ImageView imageView;
    Button btn1,btn2,btn3,btn4;

    public class ImageDownloader extends AsyncTask<String ,Void ,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String result="";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                Bitmap myBitmap = BitmapFactory.decodeStream(is);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class GetSiteContent extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data=isr.read();
                while(data!=-1){
                    char current= (char)data;
                    result+= current;
                    data=isr.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Failed";
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView) findViewById(R.id.imageView);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        GetSiteContent obj = new GetSiteContent();
        try {
            siteContent = obj.execute("http://www.posh24.se/kandisar/").get();
            Log.i("SiteContent : ",siteContent);
            String[] stringSplit = siteContent.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(stringSplit[0]);
            while(m.find()) {
                celebURL.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(stringSplit[0]);
            while(m.find()) {
                celebName.add(m.group(1));
            }

            generateOptions();
        }catch (Exception e){
            e.printStackTrace();
            Log.i("SiteContent : ","Failed in onCreate");
        }

    }

    public void generateOptions() throws ExecutionException, InterruptedException {
        Random rand = new Random();
        correctlocation = rand.nextInt(4);
        correctCeleb = rand.nextInt(celebURL.size());
        ArrayList<String> options = new ArrayList<String>();
        for(int i=0;i<4;i++) {
            if(i==correctlocation)
                options.add(i,celebName.get(correctCeleb));
            else {
                int incorrectCeleb = rand.nextInt(celebURL.size());
                while(incorrectCeleb==correctCeleb) {
                    incorrectCeleb = rand.nextInt(celebURL.size());
                }
                options.add(i,celebName.get(incorrectCeleb));
            }
        }
        btn1.setText(options.get(0));
        btn2.setText(options.get(1));
        btn3.setText(options.get(2));
        btn4.setText(options.get(3));
        ImageDownloader test = new ImageDownloader();
        Bitmap celebImg = test.execute(celebURL.get(correctCeleb)).get();
        imageView.setImageBitmap(celebImg);
    }

    public void selectOption(View view) {

        String selected = view.getTag().toString();
        if(correctlocation==Integer.parseInt(selected)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Wrong!The correct anser is "+celebName.get(correctCeleb), Toast.LENGTH_SHORT).show();
        }

        try {
            generateOptions();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
