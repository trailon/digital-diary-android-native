package com.digitaldiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AddRemembranceActivity extends AppCompatActivity {
    TextView sad,desperate,happy,lovely,loved,angry,shocked;
    EditText title,content,location,password;
    String pickedMood;
    Button addButton;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remembrance);
        sad = findViewById(R.id.emoji_sad);
        desperate = findViewById(R.id.emoji_desperate);
        happy = findViewById(R.id.emoji_happy);
        lovely = findViewById(R.id.emoji_lovely);
        loved = findViewById(R.id.emoji_loved);
        angry = findViewById(R.id.emoji_angry);
        shocked = findViewById(R.id.emoji_shocked);
        ArrayList<TextView> emojis = new ArrayList<TextView>();
        emojis.addAll(Arrays.asList(sad,desperate,happy,lovely,loved,angry,shocked));
        for (TextView emoji : emojis){
            emoji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickedMood = emoji.getText().toString();
                    Toast.makeText(getApplicationContext(),"Mood picked: "+pickedMood, Toast.LENGTH_SHORT).show();
                }
            });
        }
        title = findViewById(R.id.add_remembrance_title);
        content = findViewById(R.id.add_remembrance_content);
        password = findViewById(R.id.add_remembrance_password);
        location = findViewById(R.id.add_remembrance_location);
        addButton = findViewById(R.id.remembrance_add);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(title.getText().toString().isEmpty() || location.getText().toString().isEmpty() || pickedMood == null || content.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"All non-optional fields must be filled!", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,?> allEntries = sharedPref.getAll();
                    Set<Integer> keyIds = new TreeSet<>();
                    Date currentTime = Calendar.getInstance().getTime();
                    JSONObject remembranceJson =new JSONObject();
                    for (String key : allEntries.keySet()){
                        try {
                            keyIds.add(Integer.parseInt(key));
                        }catch(Exception e){
                            System.out.println(e);
                        }
                    }
                    try {
                        remembranceJson.put("id","0");
                        remembranceJson.put("title",title.getText().toString());
                        remembranceJson.put("content",content.getText().toString());
                        remembranceJson.put("date",currentTime.toLocaleString());
                        remembranceJson.put("location",location.getText().toString());
                        remembranceJson.put("mood",pickedMood);
                        remembranceJson.put("password",password.getText().toString());
                    }catch (Exception e){
                        System.out.println("Exception: Error occured at putting values into JSON Object");
                    }
                    if(keyIds.isEmpty()){
                        editor.putString("0",remembranceJson.toString());
                    }else{
                        int lastId = Collections.max(keyIds)+1;
                        try {
                            remembranceJson.put("id",Integer.toString(lastId));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.putString(Integer.toString(lastId),remembranceJson.toString());
                    }
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Remembrance Added!", Toast.LENGTH_SHORT).show();
                    finish();
                }



            }
        });

    }
}