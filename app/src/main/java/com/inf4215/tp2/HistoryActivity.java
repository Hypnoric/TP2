package com.inf4215.tp2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        String a = "1";
        String b = "2";
        String c = "3";
        if(MainActivity.getCptBD() == 1)
        {
            a = "3";
            b = "2";
            c = "1";
        }
        else if(MainActivity.getCptBD() == 2)
        {
            a = "1";
            b = "3";
            c = "2";
        }
        else if(MainActivity.getCptBD() == 3)
        {
            a = "2";
            b = "1";
            c = "3";
        }

        if(MainActivity.getTrajet1Existe()) {
            Cursor resultSet = MainActivity.trajets.rawQuery("Select * from Trajet" + a, null);
            resultSet.moveToFirst();
            String depart = resultSet.getString(0);
            String arrivee = resultSet.getString(1);

            final Button btn1 = (Button) findViewById(R.id.button);
            btn1.setText(depart + " - " + arrivee);
            btn1.setEnabled(true);
        }

        if(MainActivity.getTrajet2Existe()) {
            Cursor resultSet = MainActivity.trajets.rawQuery("Select * from Trajet" + b, null);
            resultSet.moveToFirst();
            String depart = resultSet.getString(0);
            String arrivee = resultSet.getString(1);

            final Button btn2 = (Button) findViewById(R.id.button2);
            btn2.setText(depart + " - " + arrivee);
            btn2.setEnabled(true);
        }

        if(MainActivity.getTrajet3Existe()) {
            Cursor resultSet = MainActivity.trajets.rawQuery("Select * from Trajet" + c, null);
            resultSet.moveToFirst();
            String depart = resultSet.getString(0);
            String arrivee = resultSet.getString(1);

            final Button btn3 = (Button) findViewById(R.id.button3);
            btn3.setText(depart + " - " + arrivee);
            btn3.setEnabled(true);
        }
    }

    public void onH1ButtonClick(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("depart", "depart");
        intent.putExtra("arrivee", "arrivee");
        intent.putExtra("zoomFactor", 10);
        intent.putExtra("locatingFrequency", 0);
        intent.putExtra("playbackMode", true);
        intent.putExtra("numeroTrajet", 1);
        startActivity(intent);
    }

    public void onH2ButtonClick(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("depart", "depart");
        intent.putExtra("arrivee", "arrivee");
        intent.putExtra("zoomFactor", 10);
        intent.putExtra("locatingFrequency", 0);
        intent.putExtra("playbackMode", true);
        intent.putExtra("numeroTrajet", 2);
        startActivity(intent);
    }

    public void onH3ButtonClick(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("depart", "depart");
        intent.putExtra("arrivee", "arrivee");
        intent.putExtra("zoomFactor", 10);
        intent.putExtra("locatingFrequency", 0);
        intent.putExtra("playbackMode", true);
        intent.putExtra("numeroTrajet", 3);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
}
