package com.digitaldiary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetPasswordActivity extends AppCompatActivity {
   SharedPreferences sharedPref;
   SharedPreferences.Editor editor;
   EditText oldPasswordEditText;
   EditText newPasswordEditText;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.set_password);
      final Button setPasswordButton = findViewById(R.id.set_password_button);
      oldPasswordEditText = findViewById(R.id.old_password);
      newPasswordEditText = findViewById(R.id.new_password);
      oldPasswordEditText.setVisibility(View.INVISIBLE);
      sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      editor = sharedPref.edit();
      if(sharedPref.contains("main_password")){
         oldPasswordEditText.setVisibility(View.VISIBLE);
      }
      setPasswordButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if(sharedPref.contains("main_password")){
               if(sharedPref.getString("main_password","Primary password doesn't exist").equals(oldPasswordEditText.getText().toString())){
                  editor.putString("main_password",newPasswordEditText.getText().toString());
                  editor.apply();
                  Toast.makeText(getApplicationContext(), "You have successfully set your new password!", Toast.LENGTH_SHORT).show();
                  finish();
               }else{
                  Toast.makeText(getApplicationContext(), "Your old password is not correct!", Toast.LENGTH_SHORT).show();
               }
            }else{
               if(newPasswordEditText.getText().toString().length()>0){
                  editor.putString("main_password",newPasswordEditText.getText().toString());
                  editor.apply();
                  Toast.makeText(getApplicationContext(), "You have successfully set your password!", Toast.LENGTH_SHORT).show();
                  finish();
               }
            }
         }
      });
   }
}