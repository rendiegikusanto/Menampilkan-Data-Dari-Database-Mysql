package com.rendi.tutorial.json_parsing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {


    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "Id_lokasi";
    private static final String TAG_nama_lokasi = "nama_lokasi";
    private static final String TAG_latitude ="latitude";
    private static final String TAG_longitude ="longitude";
    private static final String TAG_alamat ="alamat";
    private static final String TAG_data_parameter ="data_parameter";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listview);
        personList = new ArrayList<HashMap<String,String>>();
        getData();
    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String nama = c.getString(TAG_nama_lokasi);
                String lat = c.getString(TAG_latitude);
                String longitude = c.getString(TAG_longitude);
                String alamat = c.getString(TAG_alamat);
                String param = c.getString(TAG_data_parameter);

                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put(TAG_ID,id);
                persons.put(TAG_nama_lokasi,nama);
                persons.put(TAG_latitude,lat);
                persons.put(TAG_longitude,longitude);
                persons.put(TAG_alamat,alamat);
                persons.put(TAG_data_parameter,param);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, personList, R.layout.list_item,
                    new String[]{TAG_ID,TAG_nama_lokasi,TAG_latitude,TAG_longitude,TAG_alamat,TAG_data_parameter},
                    new int[]{R.id.id, R.id.nama, R.id.lat,  R.id.longitudee,  R.id.alamat,  R.id.data_param }
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://probalam.com/probal_server/tampil.php");

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();

    }
}