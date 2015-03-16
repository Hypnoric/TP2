package com.inf4215.tp2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    public static SQLiteDatabase trajets;
    private static int cptBD;
    private static boolean trajet1Existe;
    private static boolean trajet2Existe;
    private static boolean trajet3Existe;

    private float zoomFactor = 0;
    private int locatingFrequency = 0;

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
        locatingFrequency = frequencyValue(seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                seekBarValue.setText(frequencyValue(seekBar.getProgress()) + " secondes");
                locatingFrequency = frequencyValue(seekBar.getProgress());
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
        zoomSeekBar.setMax(20);
        zoomSeekBar.setProgress(10);
        final TextView zoomSeekBarValue = (TextView)findViewById(R.id.zoomText);
        zoomSeekBarValue.setText(String.valueOf(zoomSeekBar.getProgress() + " X"));
        zoomFactor = zoomSeekBar.getProgress();
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar zoomSeekBar, int progress,
                                          boolean fromUser) {
                zoomSeekBarValue.setText(zoomSeekBar.getProgress() + " X");
                zoomFactor = zoomSeekBar.getProgress();
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
        AlertDialog diaBox = askOption();
        diaBox.show();
    }

    private AlertDialog askOption()
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

    private boolean isOk(String adresse)
    {
        boolean ok = true;
        for(int i = 0; i < adresse.length(); ++i)
        {
            if(!Character.isLetterOrDigit(adresse.charAt(i)) && adresse.charAt(i) != ' '
                    && adresse.charAt(i) != ',' && adresse.charAt(i) != '-'){
                ok = false;
            }
        }
        return ok;
    }

    private AlertDialog askTrajet()
    {
        final EditText depart = new EditText(this);
        final EditText arrivee = new EditText(this);
        final TextView labelDep = new TextView(this);
        final TextView labelArr = new TextView(this);

        labelDep.setText("Depart");
        labelArr.setText("Arrivee");
        depart.setText("4202 Joffre, Montreal");
        arrivee.setText("2900 Boulevard Edouard-Montpetit, Montreal");

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(labelDep);
        lay.addView(depart);
        lay.addView(labelArr);
        lay.addView(arrivee);

        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Trajet")
                .setMessage("Entrez les adresses de depart et d'arrivee")
                .setView(lay)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isOk(depart.getText().toString()) && isOk(arrivee.getText().toString())){
                                openMap(depart.getText().toString(), arrivee.getText().toString());
                        } else {
                            Toast.makeText(MainActivity.this, "Les adresses ne doivent pas contenir de caracteres speciaux", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private void openMap(String depart, String arrivee){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("depart", depart);
        intent.putExtra("arrivee", arrivee);
        intent.putExtra("zoomFactor", zoomFactor);
        intent.putExtra("locatingFrequency", locatingFrequency);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onGPSButtonClick(View v){
        AlertDialog diaBox = askTrajet();
        diaBox.show();
    }

    public void onQuitButtonClick(View v){
        AlertDialog diaBox = askOption();
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
