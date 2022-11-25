package android.com.jamsand.io.fileinputoutputtrick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText readView;
    EditText writeView;

    FileOutputStream outputStream;
    FileInputStream inputStream;
    String fileName = "temp.txt";

    File myExternalFile;
    private boolean requestGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readView = findViewById(R.id.editTextRead);
        writeView = findViewById(R.id.editTextWrite);

        Button externalWrite = findViewById(R.id.WriteExternalButton);
        Button externalRead = findViewById(R.id.ReadExternalButton);
        Button externalDelete = findViewById(R.id.DeleteExternalButton);

        //if not able to write to external storage, disable buttons
        if (!isExternalStorageWritable() && isExternalStorageReadable()){
            externalWrite.setEnabled(false);
            externalRead.setEnabled(false);
            externalDelete.setEnabled(false);
        }
        myExternalFile = getDocumentDir("temp.txt");

    }

    // get the directory for the users's public pictures directory
    public File getDocumentDir(String name){

        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),name);
    }

    public void WriteInternalOnClick(View view){
        String data = writeView.getText().toString();

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();

            Toast.makeText(this,"text written to file",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void ReadInternalOnClick(View view){

        try {
            InputStream instream = this.openFileInput(fileName);
            ReadData(instream);
        }
        catch (java.io.FileNotFoundException e){
            
        }
    }

    private void ReadData(InputStream instream) {
        try {
            InputStreamReader inputReader = new InputStreamReader(instream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            String line = new String();
            String allLines = new String();

            // read every line of the file into the line-variable, one line at the time

            while ((line = bufferedReader.readLine()) != null){
                    allLines +=  line;
            }
            readView.setText(allLines);

            //close file again
            instream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Check if external storage is available for  read and write
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
    //che if external storage is available to at least read
    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }

    public void DeleteInternalOnClick(View view){
        this.deleteFile("temp.txt");
    }

    public void WriteExternalOnClick(View view){
        String data = writeView.getText().toString();
        //ask permissions to write_external_storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    12);
        }
        try {
            outputStream = new FileOutputStream(myExternalFile);
            outputStream.write(data.getBytes());
            outputStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void ReadExternalOnClick(View view){
        try {
            InputStream inputStream = new FileInputStream(myExternalFile);
            ReadData(inputStream);

        }catch (java.io.FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void DeleteExternalOnClick(View view){
        myExternalFile.delete();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 12: {
                //if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestGranted = true;
                } else {
                    Toast.makeText(this,"Na, permission NOT granted",Toast.LENGTH_SHORT).show();
                    //permission denied, boo! Disable the functionality that depends on this permission
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}