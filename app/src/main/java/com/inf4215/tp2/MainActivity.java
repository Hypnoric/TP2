package com.inf4215.tp2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends ActionBarActivity {

    public static SQLiteDatabase trajets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(trajets == null)
        {
            trajets = openOrCreateDatabase("trajets", MODE_PRIVATE, null);

            //////////a enlever
            trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet1(Depart VARCHAR,Arrivee VARCHAR);");
            trajets.execSQL("INSERT INTO Trajet1 VALUES('depart1','arrivee1');");
            trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet2(Depart VARCHAR,Arrivee VARCHAR);");
            trajets.execSQL("INSERT INTO Trajet2 VALUES('depart2','arrivee2');");
            trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet3(Depart VARCHAR,Arrivee VARCHAR);");
            trajets.execSQL("INSERT INTO Trajet3 VALUES('depart3','arrivee3');");
            //////////
        }

        SeekBar seekBar = (SeekBar)findViewById(R.id.frequenceBar);
        final TextView seekBarValue = (TextView)findViewById(R.id.freqText);
        seekBarValue.setText(String.valueOf(frequencyValue(seekBar.getProgress()) + " secondes"));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                seekBarValue.setText(frequencyValue(seekBar.getProgress()) + " secondes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        SeekBar zoomSeekBar = (SeekBar)findViewById(R.id.zoomBar);
        final TextView zoomSeekBarValue = (TextView)findViewById(R.id.zoomText);
        zoomSeekBarValue.setText(String.valueOf(zoomSeekBar.getProgress() + " X"));

        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar zoomSeekBar, int progress,
                                          boolean fromUser) {
                zoomSeekBarValue.setText(zoomSeekBar.getProgress() + " X");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    public int frequencyValue(int frequenceProgressBarValue)
    {
        int value = (frequenceProgressBarValue/5) * 15  + 30;
        return value;
    }

    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Quitter")
                .setMessage("Voulez vous quitter l'application?")

                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onGPSButtonClick(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onQuitButtonClick(View v){
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    public void onHistoriqueButtonClick(View v){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
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
