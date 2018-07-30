package com.kkroo.dheeraj.nitcgpa;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ResultActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        this.getActionBar().setIcon(R.drawable.ic_launcher);
        String msg = getIntent().getStringExtra("key");
        TextView resText = (TextView) findViewById(R.id.textRes);
        resText.setText(msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_result, menu);
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

    public void onClickSave(View v)
    {
        //MainActivity mainActivity = new MainActivity();
        //mainActivity.new AsyncProcess().writeData(mainActivity.storageDir);
        Intent resIntent = new Intent();
        resIntent.putExtra("Res", true);
        setResult(Activity.RESULT_OK, resIntent);
        finish();
    }

}
