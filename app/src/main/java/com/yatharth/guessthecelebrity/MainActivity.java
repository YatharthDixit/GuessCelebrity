 package com.yatharth.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 public class MainActivity extends AppCompatActivity {
     ImageView imageView;
     ArrayList<String> celebURL = new ArrayList<String>();
     String[] ans = new String[4];
     int locationOfAns=0;
     Button button0, button1, button2, button3;

     ArrayList<String> celebName = new ArrayList<String>();
     int chosenCeleb;
     public void CelebSelected(View view) throws ExecutionException, InterruptedException {
         if (view.getTag().toString().equals(Integer.toString(locationOfAns))){
             Toast.makeText(this, "Correct Answer!!", Toast.LENGTH_SHORT).show();
         }else{
             Toast.makeText(this, "Incorrect Answer!! It was "+ans[locationOfAns], Toast.LENGTH_SHORT).show();
         }
         setupInterface();

     }

     public void setupInterface() throws ExecutionException, InterruptedException {
         Random rand = new Random();
         chosenCeleb = rand.nextInt(celebURL.size());
         ImageDownloader imageTask = new ImageDownloader();
         Bitmap celebImage = imageTask.execute(celebURL.get(chosenCeleb)).get();
         imageView.setImageBitmap(celebImage);
         locationOfAns = rand.nextInt(4);
         int incorrectAnsLocation;
         for (int i=0; i<4; i++){
             if (i==locationOfAns){
                 ans[i]=celebName.get(chosenCeleb);
             }else{

                 incorrectAnsLocation = rand.nextInt(celebURL.size());
                 while(incorrectAnsLocation==chosenCeleb){
                     incorrectAnsLocation = rand.nextInt(celebURL.size());
                 }
                 ans[i]=celebName.get(incorrectAnsLocation);
             }
         }
         button0.setText(ans[0]);
         button1.setText(ans[1]);
         button2.setText(ans[2]);
         button3.setText(ans[3]);



 }
     public class ImageDownloader extends AsyncTask<String, Void , Bitmap>{

         @Override
         protected Bitmap doInBackground(String... urls) {
             try {
                 URL url = new URL(urls[0]);
                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                 conn.connect();
                 InputStream in = conn.getInputStream();
                 Bitmap myBitmap= BitmapFactory.decodeStream(in);
                 return myBitmap;
             }catch (Exception e){
                 e.printStackTrace();
                 return null;
             }
         }
     }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        String result = "";

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Nikal pehli";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.buttton0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        String result=null;
        try {
            result = task.execute("https://xploringindia.in/best-youtubers-in-india/").get();
            Log.i("content of URL", result);
            String[] splitresult = result.split("<h2><span id=\"Frequently_Asked_Questions_FAQs\">Frequently Asked Questions (FAQs)</span></h2>");
            Pattern p = Pattern.compile("<img loading=\"lazy\" width=\"1024\" height=\"536\" src=\"(.*?)\" alt=");
            Matcher m = p.matcher(splitresult[0]);

            while (m.find()) {
                celebURL.add(m.group(1));
            }
            p = Pattern.compile("1024x536.jpg\" alt=\"(.*?)\"");
            m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebName.add(m.group(1));
            }
            setupInterface();


    }catch (Exception e){
            e.printStackTrace();
        }
}
     }