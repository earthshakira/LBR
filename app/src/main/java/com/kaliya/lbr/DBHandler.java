package com.kaliya.lbr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "reminder.db";
    public static final String TABLE_NAME = "taskDetails";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASKNAME = "_taskname";
    public static final String COLUMN_LOCATION = "_location";
    public static final String COLUMN_DATE = "_date";
    public static final String COLUMN_TIME = "_time";
    public static final String COLUMN_PLACE = "_place";



    //Constructor.
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME +"(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TASKNAME + " TEXT," +
                COLUMN_LOCATION + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TIME + " TEXT," +
                COLUMN_PLACE + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    //Add a new row to the database.
    public void addReminder(Reminder reminder) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASKNAME, reminder.get_taskname()); //Where, what. (Not writing to database yet.)
        values.put(COLUMN_LOCATION, reminder.get_location());
        values.put(COLUMN_DATE, reminder.get_date());
        values.put(COLUMN_TIME, reminder.get_time());
        values.put(COLUMN_PLACE, reminder.get_place());
            Log.d("yolo", "addReminder: "+reminder.get_place());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME, null, values); //Writing to database now.
        sqLiteDatabase.close();
    }
    void reset(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "CREATE TABLE " + TABLE_NAME +"(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TASKNAME + " TEXT," +
                COLUMN_LOCATION + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TIME + " TEXT" +
                COLUMN_PLACE + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }
    // Getting single reminder
    Reminder getReminder(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where "+COLUMN_ID+"="+id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2), cursor.getString(3),  cursor.getString(4),cursor.getString(5));
        // return contact
        db.close();
        return reminder;
    }

    // Getting All reminder
    public List<String> getAllReminders() {
        List<String> taskList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.set_id(Integer.parseInt(cursor.getString(0)));
                reminder.set_taskname(cursor.getString(1));
                reminder.set_location(cursor.getString(5));
                reminder.set_date(cursor.getString(3));
                reminder.set_time(cursor.getString(4));

                String name = cursor.getString(1) +"\n"+ cursor.getString(5);
                MainActivity.ArrayofTask.add(name);
                // Adding reminder to list
                taskList.add(name);
            } while (cursor.moveToNext());
        }
        db.close();
       return taskList;
    }

    public ArrayList<Reminder> fetchAllReminders() {
        ArrayList<Reminder> ret = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.set_id(Integer.parseInt(cursor.getString(0)));
                reminder.set_taskname(cursor.getString(1));
                reminder.set_location(cursor.getString(2));
                reminder.set_date(cursor.getString(3));
                reminder.set_time(cursor.getString(4));
                reminder.set_place(cursor.getString(5));
                // Adding reminder to list
                ret.add(reminder);
            } while (cursor.moveToNext());
        }
        db.close();
        return ret;
    }

    // Updating single reminder
    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TASKNAME, reminder.get_taskname());
        values.put(COLUMN_LOCATION, reminder.get_location());
        values.put(COLUMN_DATE, reminder.get_date());
        values.put(COLUMN_TIME, reminder.get_time());
        // updating row
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(reminder.get_id()) });
    }

    // Deleting single reminder
    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[] { String.valueOf(reminder.get_id()) });
        db.close();
    }

    // Getting reminders Count
    public int getReminderCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
