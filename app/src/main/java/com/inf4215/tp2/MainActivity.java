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
    private static int cptBD;
    private static boolean trajet1Existe;
    private static boolean trajet2Existe;
    private static boolean trajet3Existe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(trajets == null)
        {
            cptBD = 1;
            trajets = openOrCreateDatabase("trajets", MODE_PRIVATE, null);

            trajet1Existe = false;
            trajet2Existe = false;
            trajet3Existe = false;
            //////////a enlever
            //trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet1(Depart VARCHAR,Arrivee VARCHAR);");
            //trajets.execSQL("INSERT INTO Trajet1 VALUES('depart1','arrivee1');");
            //trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet2(Depart VARCHAR,Arrivee VARCHAR);");
            //trajets.execSQL("INSERT INTO Trajet2 VALUES('depart2','arrivee2');");
            //trajets.execSQL("CREATE TABLE IF NOT EXISTS Trajet3(Depart VARCHAR,Arrivee VARCHAR);");
            //trajets.execSQL("INSERT INTO Trajet3 VALUES('depart3','arrivee3');");
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

    static public boolean getTrajet1Existe(){
        return trajet1Existe;
    }

    static public boolean getTrajet2Existe(){
        return trajet2Existe;
    }

    static public boolean getTrajet3Existe(){
        return trajet3Existe;
    }

    static public int getCptDb(){
        return cptBD;
    }

    static public void addToDataBase(String adresseDepart, String adresseArrivee, MapsActivity.PointDeMarquage[] ptsMarquages){
        String tableName = "Trajet" + Integer.toString(cptBD);
        trajets.execSQL("DROP TABLE IF EXISTS " + tableName);
        trajets.execSQL("CREATE TABLE IF NOT EXISTS "+ tableName + "(Depart VARCHAR,Arrivee VARCHAR);");
        String inserts = "'" + adresseDepart + "','" + adresseArrivee + "'";
        trajets.execSQL("INSERT INTO "+ tableName + " VALUES(" + inserts + ");");
        for(int i = 0; i < ptsMarquages.length; ++i){
            trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Latitude" + Integer.toString(i) + " DOUBLE");
            trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Longitude" + Integer.toString(i) + " DOUBLE");
            trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Altitude" + Integer.toString(i) + " DOUBLE");
            /*trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Deplacement" + Integer.toString(i) + " VARCHAR");
            trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Distance" + Integer.toString(i) + " DOUBLE");
            trajets.execSQL("ALTER TABLE " + tableName + " ADD COLUMN Batterie" + Integer.toString(i) + " DOUBLE");*/
            inserts = "'" + ptsMarquages[i].latitude + "','" + ptsMarquages[i].longitude + "','" + ptsMarquages[i].altitude + "'";
            trajets.execSQL("INSERT INTO "+ tableName + " VALUES(" + inserts + ");");
        }

        if(cptBD == 1)
            trajet1Existe = true;
        if(cptBD == 2)
            trajet2Existe = true;
        if(cptBD == 3)
            trajet3Existe = true;

        ++cptBD;
        if(cptBD > 3)
            cptBD = 1;
    }

    public int frequencyValue(int frequenceProgressBarValue){
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
