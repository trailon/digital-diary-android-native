package com.digitaldiary;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewRemembranceActivity extends AppCompatActivity {
    Bundle extras;
    TextView date;
    EditText title,location,mood,content;
    Button update,delete,share,generate;
    String remembrance_id;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String remembranceDetails;
    JSONObject object = null;
    int pageHeight = 1120;
    int pageWidth = 792;
    String titleS = "",moodS = "", dateS = "",locationS = "",contentS = "";
    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_remembrance);
        extras = getIntent().getExtras();
        remembrance_id = extras.getString("remembrance_id");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        System.out.println(remembrance_id);
        remembranceDetails = sharedPref.getString(remembrance_id,"");
        date = findViewById(R.id.view_remembrance_date);
        title = findViewById(R.id.view_remembrance_title);
        location = findViewById(R.id.view_remembrance_location);
        mood = findViewById(R.id.view_remembrance_mood);
        content = findViewById(R.id.view_remembrance_content);
        update = findViewById(R.id.remembrance_update);
        delete = findViewById(R.id.remembrance_delete);
        share = findViewById(R.id.remembrance_share);
        generate = findViewById(R.id.remembrance_generate_pdf);
        if (checkPermission()) {
            Toast.makeText(ViewRemembranceActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

        } else {
            requestPermission();
            Toast.makeText(ViewRemembranceActivity.this, "Press Again!", Toast.LENGTH_SHORT).show();
        }
        setData();
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ViewRemembranceActivity.this)
                        .setTitle("Delete Remembrance")
                        .setMessage("Are you sure you want to delete this remembrance?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.remove(remembrance_id);
                                editor.apply();
                                Toast.makeText(getApplicationContext(),"Remembrance Deleted!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    String shareBody = "I want to share a remembrance of mine with you!\n"+object.getString("title")+" | "+object.getString("mood")+"\n"+object.getString("date")+"\n"+object.getString("location")+"\n"+object.getString("content");
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "A remembrance of mine");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(intent, "Share via"));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        update.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        generate.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void setData(){
        try {
            object = new JSONObject(remembranceDetails);
            System.out.println(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object != null){
            try {
                date.setText(object.getString("date"));
                title.setText(object.getString("title"));
                location.setText(object.getString("location"));
                mood.setText(object.getString("mood"));
                content.setText(object.getString("content"));
                titleS = object.getString("title");
                moodS = object.getString("mood");
                dateS = object.getString("date");
                locationS = object.getString("location");
                contentS = object.getString("content");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateData(){
        JSONObject remembranceJson =new JSONObject();
        Date currentTime = Calendar.getInstance().getTime();

        try {
            remembranceJson.put("id",remembrance_id);
            remembranceJson.put("title",title.getText().toString());
            remembranceJson.put("content",content.getText().toString());
            remembranceJson.put("date",currentTime.toLocaleString());
            remembranceJson.put("location",location.getText().toString());
            remembranceJson.put("mood",mood.getText().toString());
            remembranceJson.put("password",object.getString("password"));
            editor.putString(remembrance_id,remembranceJson.toString());
            editor.apply();
            Toast.makeText(getApplicationContext(),"Remembrance Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            System.out.println("Exception: Error occurred at putting values into JSON Object");
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"An Error Occurred", Toast.LENGTH_SHORT).show();
        }

    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();
        Paint info = new Paint();
        Paint content = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();
        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        content.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(25);
        info.setTextSize(18);
        content.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        info.setColor(ContextCompat.getColor(this, R.color.teal_700));
        content.setColor(ContextCompat.getColor(this, R.color.black));
        canvas.drawText(titleS, (float) (pageWidth * 0.5), 110, title);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(moodS, (float) (pageWidth * 0.5), 140, info);
        canvas.drawText(dateS, (float) (pageWidth * 0.7), 220, info);
        canvas.drawText(locationS, (float) (pageWidth * 0.1), 220, info);
        String temp = "";
        int initialPosition = 300;
        if(contentS.length() < 90){
            canvas.drawText(contentS, (float) (pageWidth * 0.1), initialPosition, content);
        }else{
            for (int i = 0; i < contentS.length(); i++){
                temp+=contentS.charAt(i);
                if(temp.length() > 80 && contentS.charAt(i) == ' '){
                    canvas.drawText(temp, (float) (pageWidth * 0.1), initialPosition, content);
                    initialPosition+=40;
                    temp = "";
                }
            }
        }

        info.setTextAlign(Paint.Align.CENTER);
        pdfDocument.finishPage(myPage);
        //List<File> myFileList = new ArrayList<File>();
        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), titleS+".pdf");
        /*File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), titleS+".pdf");
        File file3 = new File(Environment.getDataDirectory(), titleS+".pdf");
        File file4 = new File(Environment.getDownloadCacheDirectory(), titleS+".pdf");
        File file5 = new File(Environment.getExternalStorageDirectory(), titleS+".pdf");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File file6 = new File(Environment.getStorageDirectory(), titleS+".pdf");
            myFileList.add(file6);
        }*/
        /*myFileList.add(file1);
        myFileList.add(file2);
        myFileList.add(file3);
        myFileList.add(file4);
        myFileList.add(file5);*/
        /*for (File file:myFileList){
            try {
                System.out.println(file);
                file.createNewFile();
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(ViewRemembranceActivity.this, "PDF file generated to "+file.getPath()+" successfully.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ViewRemembranceActivity.this, "PDF file generate failed.", Toast.LENGTH_SHORT).show();
            }
        }*/
        try {
            System.out.println(file1);
            file1.createNewFile();
            pdfDocument.writeTo(new FileOutputStream(file1));
            Toast.makeText(ViewRemembranceActivity.this, "PDF file generated to downloads directory successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ViewRemembranceActivity.this, "PDF file generate failed.", Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
    }

}