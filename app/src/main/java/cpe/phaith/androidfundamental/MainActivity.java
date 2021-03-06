package cpe.phaith.androidfundamental;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private Context context;
    private Button btnSave;
    private EditText editText;
    public MediaRecorder recorder;
    private String outputFile = null;
    private String oF = null;
    private String fileName = null;
    private Button play;
    private SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oF = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sound.aac";
        context = this;

        mydatabase = openOrCreateDatabase("song database",MODE_PRIVATE,null);
        btnSave = (Button)findViewById(R.id.btnSave);
        play = (Button)findViewById(R.id.play);
        editText = (EditText)findViewById(R.id.username);
        editText.setText(getText("username"));


        btnSave.setText("Record");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS SoundInfo3(ID INTEGER PRIMARY KEY,Filename VARCHAR,Name VARCHAR);");
        Cursor resultSet = mydatabase.rawQuery("Select max(ID) from SoundInfo3",null);
        resultSet.moveToFirst();
        if(resultSet.getString(0) == null){
            mydatabase.execSQL("INSERT INTO SoundInfo3 (ID,Filename, Name) VALUES (0,'test','test');;");
        }
        //mydatabase.execSQL("INSERT INTO SoundInfo2 (ID,Filename, Name) VALUES (1,'test','test');;");
        //mydatabase.execSQL("CREATE TABLE IF NOT EXISTS TagTable(Filename VARCHAR,Type VARCHAR,Start VARCHAR,End VARCHAR);");
        //Cursor resultSet = mydatabase.rawQuery("Select ID from SoundInfo",null);
        //resultSet.getString(resultSet.getColumnIndex("ID"));
        //resultSet.toString();

        //String id = resultSet.getString(1);

        //Toast.makeText(context, resultSet.toString(), Toast.LENGTH_SHORT).show();

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                //saveText("username", editText.getText().toString());
                fileName = editText.getText().toString();
                if (btnSave.getText() == "Record") {
                    recorder= new MediaRecorder();
                    recorder.reset();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                    Cursor resultSet = mydatabase.rawQuery("Select max(ID) from SoundInfo3",null);
                    //resultSet.getString(resultSet.getColumnIndex("ID"));

                    resultSet.moveToFirst();
                    //resultSet.moveToPosition(3);

                    String id = ""+(resultSet.getInt(0)+1);
                    /*if(id == null){
                        id = "1";
                    }*/
                    //id = "test";
                    outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sound "+id+".aac";
                    recorder.setOutputFile(outputFile);

                    start(v);
                    btnSave.setText("Stop");
                } else {
                    stop(v);
                    mydatabase.execSQL("INSERT INTO SoundInfo3 (ID,Filename, Name) VALUES ((SELECT max(ID) FROM SoundInfo3)+1,'"+outputFile+"','"+fileName+"');;");

                    btnSave.setText("Record");
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    play(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void start(View view){
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

    }
    public void stop(View view){
        recorder.stop();
        recorder.release();
        recorder  = null;
        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                Toast.LENGTH_LONG).show();
    }
    public void play(View view) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException{

        MediaPlayer m = new MediaPlayer();
        m.setDataSource(outputFile);
        m.prepare();
        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();

    }
    private void saveText(String key, String text){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, text);
        editor.commit();
        Toast.makeText(context, "username is saved.", Toast.LENGTH_SHORT).show();
    }

    private String getText(String key){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key,"");
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
}
