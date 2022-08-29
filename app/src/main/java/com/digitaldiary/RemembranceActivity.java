package com.digitaldiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RemembranceActivity extends AppCompatActivity {
    ImageButton addRemembranceButton;
    private RecyclerView remembranceRecyclerView;
    private ArrayList<Remembrance> remembranceList;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        remembranceList.clear();
        getRemembrances();
        setAdapter();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remembrance_list);
        remembranceRecyclerView = findViewById(R.id.remembrance_list);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        addRemembranceButton = findViewById(R.id.button_add_remembrance);
        remembranceList = new ArrayList<Remembrance>();
        getRemembrances();
        setAdapter();
        addRemembranceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAddRemembrance();
            }

            private void toAddRemembrance() {
                Intent intent = new Intent(RemembranceActivity.this,AddRemembranceActivity.class);
                RemembranceActivity.this.startActivity(intent);
            }
        });
    }

    private void getRemembrances() {
        Map<String,?> allEntries = sharedPref.getAll();
        Set<Integer> keyIds = new TreeSet<>();
        for (String key : allEntries.keySet()){
            try {
                keyIds.add(Integer.parseInt(key));
            }catch(Exception e){
                System.out.println(e);
            }
        }
        if(keyIds.isEmpty()){
            Toast.makeText(getApplicationContext(),"You don't have any remembrance go add some!", Toast.LENGTH_SHORT).show();
        }else{
            TreeSet<Integer> sorted = new TreeSet<Integer>(keyIds);
            for(Integer i:sorted){
                String remembranceDetails = sharedPref.getString(Integer.toString(i),"");
                JSONObject object = null;
                try {
                    object = new JSONObject(remembranceDetails);
                    System.out.println(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (object != null){
                    System.out.println(remembranceDetails);
                    try {
                        remembranceList.add(new Remembrance(object.getString("title"),object.getString("location"),object.getString("date"),object.getString("mood"),Integer.toString(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void setAdapter() {
        Intent intent = getIntent();
        recyclerAdapter adapter = new recyclerAdapter(remembranceList,RemembranceActivity.this,intent);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        remembranceRecyclerView.setLayoutManager(layoutManager);
        remembranceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        remembranceRecyclerView.setAdapter(adapter);
    }
}