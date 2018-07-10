package com.example.mostafa.whatstheweater;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    Button whatsTheWeatherButton;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEditText=(EditText)findViewById(R.id.city_editText);
        whatsTheWeatherButton=(Button)findViewById(R.id.whats_the_weatherbutton);
        resultTextView=(TextView)findViewById(R.id.result_textView);
        whatsTheWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);
                DownloadTask downloadTask =new DownloadTask();
                downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityEditText.getText().toString()+"&APPID=28423ebac549fdee77518eb747237606");
            }
        });


    }
    public class DownloadTask extends AsyncTask<String,Void,String>{



        @Override
        protected String doInBackground(String... params) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection=null;

            try {
                url=new URL(params[0]);
                httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();


                while (data!=-1){
                    char c=(char) data;
                    result+=c;
                    data=reader.read();
                }

            } catch (Exception e) {
                Handler handler =  new Handler(getApplicationContext().getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(getApplicationContext(), "Invalid city name",Toast.LENGTH_LONG).show();
                        resultTextView.setText("");
                    }
                });

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                JSONArray jsonArray=new JSONArray(weatherInfo);
                String message="";
                for (int i=0;i< jsonArray.length();i++){
                    JSONObject object=jsonArray.getJSONObject(i);
                    String main="";
                    String description="";
                    main=object.getString("main");
                    description=object.getString("description");
                    Log.i("description",description);
                    if (main!="" && description!=""){
                        message+=main+":"+description+"\r\n";


                    }
                }
                if(message!=""){
                    resultTextView.setText(message);
                }else Toast.makeText(getApplicationContext(), "Invalid city name", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
