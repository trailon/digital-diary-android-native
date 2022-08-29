package com.digitaldiary;

import android.os.Parcel;
import android.os.Parcelable;

class Remembrance implements Parcelable {
   private String title;
   private String location;
   private String date;
   private String mood;
   private String id;

   Remembrance(String title, String location, String date,String mood,String id) {
      this.title = title;
      this.location = location;
      this.date = date;
      this.mood = mood;
      this.id = id;
   }


   protected Remembrance(Parcel in) {
      title = in.readString();
      location = in.readString();
      date = in.readString();
      mood = in.readString();
      id = in.readString();
   }

   public static final Creator<Remembrance> CREATOR = new Creator<Remembrance>() {
      @Override
      public Remembrance createFromParcel(Parcel in) {
         return new Remembrance(in);
      }

      @Override
      public Remembrance[] newArray(int size) {
         return new Remembrance[size];
      }
   };


   public String getdate() {
      return date;
   }

   public void setdate(String date) {
      this.date = date;
   }

   public String getlocation() {
      return location;
   }

   public void setlocation(String location) {
      this.location = location;
   }

   public String gettitle() {
      return title;
   }

   public void settitle(String title) {
      this.title = title;
   }

   public String getmood() {
      return mood;
   }

   public void setmood(String mood) {
      this.mood = mood;
   }

   public String getid() {
      return id;
   }

   public void setid(String id) {
      this.id = id;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(title);
      dest.writeString(location);
      dest.writeString(date);
      dest.writeString(mood);
      dest.writeString(id);
   }
}
