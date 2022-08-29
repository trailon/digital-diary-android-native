package com.digitaldiary;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder>{
   private ArrayList<Remembrance> remembranceList;
   private Context context;
   private Intent intent;
   public recyclerAdapter(ArrayList<Remembrance> remembranceList,Context context,Intent remembranceListintent){
      this.remembranceList = remembranceList;
      this.context = context;
      this.intent = remembranceListintent;
   }

   public class MyViewHolder extends RecyclerView.ViewHolder{
      private TextView title;
      private TextView location;
      private TextView date;
      private TextView mood;
      private EditText password;
      private ConstraintLayout itemLayout;
      private Button deleteButton;
      ConstraintLayout constraintLayout;
      private recyclerAdapter adapter;

      public MyViewHolder linkAdapter(recyclerAdapter adapter){
         this.adapter = adapter;
         return this;
      }
      public MyViewHolder(final View view) {
         super(view);
         constraintLayout = itemView.findViewById(R.id.remembrance_item);
         title = view.findViewById(R.id.title);
         location = view.findViewById(R.id.remembrance_location);
         date = view.findViewById(R.id.remembrance_date);
         mood = view.findViewById(R.id.remembrance_image);
         password = view.findViewById(R.id.password_list_item);
         itemLayout = view.findViewById(R.id.remembrance_item);
         deleteButton = view.findViewById(R.id.delete_button);
         itemView.findViewById(R.id.delete_button).setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Remembrance")
                    .setMessage("Are you sure you want to delete this remembrance?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                          String id = remembranceList.get(getAdapterPosition()).getid();
                          adapter.remembranceList.remove(getAdapterPosition());
                          adapter.notifyItemRemoved(getAdapterPosition());
                          SharedPreferences sharedPref;
                          SharedPreferences.Editor editor;
                          sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                          editor = sharedPref.edit();
                          editor.remove(id);
                          editor.apply();
                       }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
         });
      }
   }

   @NonNull
   @Override
   public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_remembrance,parent,false);
      return new MyViewHolder(itemView).linkAdapter(this);
   }



   @Override
   public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
      String title = remembranceList.get(position).gettitle();
      String location = remembranceList.get(position).getlocation();
      String date = remembranceList.get(position).getdate();
      String mood = remembranceList.get(position).getmood();
      String id = remembranceList.get(position).getid();
      holder.title.setText(title);
      holder.location.setText(location);
      holder.date.setText(date);
      holder.mood.setText(mood);
      SharedPreferences sharedPref;
      SharedPreferences.Editor editor;
      sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
      editor = sharedPref.edit();
      String remembranceDetails = sharedPref.getString(id,"");
      JSONObject object;
      String password = "";
      try {
         object = new JSONObject(remembranceDetails);
         password = object.getString("password");
         System.out.println(object);
      } catch (JSONException e) {
         e.printStackTrace();
      }
      String finalPassword = password;
      holder.itemLayout.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v) {
            if(finalPassword.isEmpty()){
               Intent intent = new Intent(context,ViewRemembranceActivity.class);
               Bundle bundle = new Bundle();
               bundle.putString("remembrance_id",remembranceList.get(position).getid());
               intent.putExtras(bundle);
               context.startActivity(intent,bundle);
            }else{
               if(finalPassword.equals(holder.password.getText().toString())){
                  Intent intent = new Intent(context,ViewRemembranceActivity.class);
                  Bundle bundle = new Bundle();
                  bundle.putString("remembrance_id",remembranceList.get(position).getid());
                  intent.putExtras(bundle);
                  context.startActivity(intent,bundle);
               }else{
                  Toast.makeText(context,"Your password for this remembrance is not correct!", Toast.LENGTH_SHORT).show();
               }
            }

         }
      });
   }

   @Override
   public int getItemCount() {
      return remembranceList.size();
   }

}
