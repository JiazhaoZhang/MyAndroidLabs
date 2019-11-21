package com.example.androidlab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class WeatherForecast extends AppCompatActivity {

    ProgressBar weatherProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_forecast);
        weatherProgressBar = findViewById(R.id.weatherProgressBar);
        weatherProgressBar.setVisibility(View.VISIBLE);

        ForecastQuery fq = new ForecastQuery();
        fq.execute();
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        String wind;
        String uv;
        String min;
        String max;
        String curTemp;
        Bitmap weatherImg;
        String queryURL =
                "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
        String uvUrl =
                "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
        TextView currentTemp = findViewById(R.id.curTemp);
        TextView minTemp = findViewById(R.id.minTemp);
        TextView maxTemp = findViewById(R.id.maxTemp);
        TextView uvRating_View = findViewById(R.id.uvRating);
        ImageView weatherImgView = findViewById(R.id.weatherImg);

        @Override
        protected String doInBackground(String... strings) {
            String ret = null;


            try {
                // Connect to the server:
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                //Set up the XML parser:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");

                //Iterate over the XML tags:
                int EVENT_TYPE;         //While not the end of the document:
                while((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT)
                {
                    switch(EVENT_TYPE)
                    {
                        case START_TAG:         //This is a start tag < ... >
                            String tagName = xpp.getName(); // What kind of tag?
                            if(tagName.equals("temperature"))
                            {
                                curTemp = xpp.getAttributeValue(null, "value"); //What is the String associated with message?
                                publishProgress(25);

                                min = xpp.getAttributeValue(null, "min");
                                publishProgress(50);

                                max = xpp.getAttributeValue(null, "max");
                                publishProgress(75);
                            }
                            else if(tagName.equals("weather")) {
                                String iconName = xpp.getAttributeValue(null, "icon");
                                Log.i("WeatherForecast", "Finding image " + iconName + ".png");
                                String iconFileName = iconName+".png";
                                if(fileExistance(iconFileName)){
                                    FileInputStream fis = openFileInput(iconFileName);
                                    weatherImg = BitmapFactory.decodeStream(fis);
                                    Log.i("Imgae Found","Local image:"+iconFileName);
                                }else{
                                    //Bitmap image = null;
                                    URL imgDownloadURL = new URL("http://openweathermap.org/img/w/" + iconFileName);
                                    HttpURLConnection imgDownloadConnection = (HttpURLConnection) imgDownloadURL.openConnection();
                                    imgDownloadConnection.connect();
                                    int responseCode = imgDownloadConnection.getResponseCode();
                                    if (responseCode == 200) {
                                        weatherImg = BitmapFactory.decodeStream(imgDownloadConnection.getInputStream());
                                    }
                                    if(weatherImg!=null) {
                                        FileOutputStream outputStream = openFileOutput(iconFileName, Context.MODE_PRIVATE);
                                        weatherImg.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                        publishProgress(100);
                                    }
                                }
                            }
                            break;
                        case END_TAG:           //This is an end tag: </ ... >
                            break;
                        case TEXT:              //This is text between tags < ... > Hello world </ ... >
                            break;
                    }
                    xpp.next(); // move the pointer to next XML element
                }
                URL uv_URL = new URL(uvUrl);
                HttpURLConnection uvURLConnection = (HttpURLConnection) uv_URL.openConnection();
                InputStream is_uv = uvURLConnection.getInputStream();
                InputStreamReader isr_uv = new InputStreamReader(is_uv);
                BufferedReader br = new BufferedReader(isr_uv);
                StringBuilder sBuilder = new StringBuilder();
                String line;
                while((line = br.readLine())!=null){
                    sBuilder.append(line+"\n");
                }
                String result = sBuilder.toString();
                JSONObject jObj = new JSONObject(result);
                float uv_value = (float) jObj.getDouble("value");
                uv = String.valueOf(uv_value);
            }
            catch(MalformedURLException mfe){ ret = "Malformed URL exception"; }
            catch(IOException ioe)          { ret = "IO Exception. Is the Wifi connected?";}
            catch(XmlPullParserException pe){ ret = "XML Pull exception. The XML is not properly formed" ;}
            catch(JSONException je){ ret = "JSON is not readable"; }
            //What is returned here will be passed as a parameter to onPostExecute:
            return ret;
        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            weatherProgressBar.setVisibility(View.VISIBLE);
            weatherProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            currentTemp.setText(curTemp);
            maxTemp.setText(max);
            minTemp.setText(min);
            weatherImgView.setImageBitmap(weatherImg);
            uvRating_View.setText(uv);
            weatherProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
