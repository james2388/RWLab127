package com.ruswizards.rwlab127;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = "MainActivity";
    private ListView listView_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<CustomViewForList> customArray  = new ArrayList<CustomViewForList>();

        for (int i = 0; i < 10; i++){
            CustomViewForList customViewForList = new CustomViewForList(this, randomString(5), randomString(10), new Random().nextInt(5));
            customArray.add(customViewForList);
        }

        listView_ = (ListView)findViewById(R.id.list_view);
        CustomAdapter customAdapter = new CustomAdapter(this, customArray);
        listView_.setAdapter(customAdapter);
    }

    private String randomString(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char temp;
        for (int i = 0; i < length; i++){
            temp = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(temp);
        }
        return randomStringBuilder.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSpecialClick(View v){
        Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show();
    }
}
