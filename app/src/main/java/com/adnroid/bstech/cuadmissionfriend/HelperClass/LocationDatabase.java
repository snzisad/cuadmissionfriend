package com.adnroid.bstech.cuadmissionfriend.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationDatabase extends SQLiteOpenHelper{
        private String Table="LOCATIONDETAILS";

        private String Location="LOCATION";
        private String lat="LATITUDE";
        private String lng="LONGITUDE";

        private Context context;

        public LocationDatabase(Context context) {
            super(context,"LOCATION.DB",null,1);
            this.context=context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String query="CREATE TABLE "+Table+"("+Location+" TEXT, "+lat+" TEXT, "+lng+" TEXT "+")";
            sqLiteDatabase.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            String query="DROP TABLE IF EXISTS "+Table;
            sqLiteDatabase.execSQL(query);
            onCreate(sqLiteDatabase);
        }

        public void empty(){
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            String query="DELETE FROM "+Table;
            sqLiteDatabase.execSQL(query);
        }

        public void add(String location, String latitude, String longitude){
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Location,location);
            contentValues.put(lat,latitude);
            contentValues.put(lng,longitude);

            sqLiteDatabase.insert(Table,null,contentValues);
        }



        public List<String> getAllLocation(){
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM "+Table,null);
            List<String> a = new ArrayList<String>();
            if(cursor.moveToFirst()){
                do{
                    a.add(cursor.getString(0));
                }while(cursor.moveToNext());
            }

            return a;
        }
    }