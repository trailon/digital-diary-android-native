package com.digitaldiary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button loginButton = findViewById(R.id.login_button);
        passwordEditText = findViewById(R.id.password);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!passwordEditText.getText().toString().isEmpty()){
                    if(sharedPref.contains("main_password")){
                        if(sharedPref.getString("main_password","").equals(passwordEditText.getText().toString())){
                            Toast.makeText(getApplicationContext(), "You have successfully logged in!", Toast.LENGTH_SHORT).show();
                            ToLogin();
                        }else{
                            Toast.makeText(getApplicationContext(), "Your password is incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "You didn't set any password!", Toast.LENGTH_SHORT).show();
                        ToRegister();
                    }
                    // LOGİN İŞLEMLERİ
                }else{
                    Toast.makeText(getApplicationContext(), "Directing you to set your password!", Toast.LENGTH_SHORT).show();
                    ToRegister();
                    // REGISTERA YÖNLENDİR
                }

            }
        });
    }

    private void ToLogin(){
        Intent intent = new Intent(MainActivity.this,RemembranceActivity.class);
        MainActivity.this.startActivity(intent);
    }
    private void ToRegister(){
        Intent intent = new Intent(MainActivity.this,SetPasswordActivity.class);
        MainActivity.this.startActivity(intent);
    }
}