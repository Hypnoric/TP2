package com.inf4215.tp2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Cursor resultSet = MainActivity.trajets.rawQuery("Select * from Trajet1", null);
        resultSet.moveToFirst();
        String depart = resultSet.getString(0);
        String arrivee = resultSet.getString(1);

        final Button btn1 = (Button) findViewById(R.id.button);
        btn1.setText(depart + " - " + arrivee);
        btn1.setEnabled(true);

        resultSet = MainActivity.trajets.rawQuery("Select * from Trajet2",null);
        resultSet.moveToFirst();
        depart = resultSet.getString(0);
        arrivee = resultSet.getString(1);

        final Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setText(depart + " - " + arrivee);
        btn2.setEnabled(true);

        resultSet = MainActivity.trajets.rawQuery("Select * from Trajet3",null);
        resultSet.moveToFirst();
        depart = resultSet.getString(0);
        arrivee = resultSet.getString(1);

        final Button btn3 = (Button) findViewById(R.id.button3);
        btn3.setText(depart + " - " + arrivee);
        btn3.setEnabled(true);
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
