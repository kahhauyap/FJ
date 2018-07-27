/**
 * @DataBaseHelper.java
 *
 * Database Helper class that is used to create the SQLite database for storing the user's
 * Constraints. Provides functionality to add user entries, update preferences, and query
 * user data.
 *
 */

package fj.foodjunkies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static java.lang.String.*;

public class DataBaseHelper extends SQLiteOpenHelper {
    //Database
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="FoodJunkies.db";

    //Table constraint entries
    private static final String TABLE_NAME="Constraints";
    private static final String KEY_ID="ID";
    private static final String KEY_BUDGET="Budget";
    private static final String KEY_DISTANCE="Distance";
    private static final String KEY_TIME="Time";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the SQLite table for constraints
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE="CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY NOT NULL UNIQUE,"
                + KEY_BUDGET + " INT DEFAULT 0,"
                + KEY_DISTANCE + " INT DEFAULT 0,"
                + KEY_TIME + " INT DEFAULT 0"
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE); //Execute SQL command to create a table with the proper fields
    }

    //Create the database or replace it
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); //If the table already exists then delete it
        onCreate(sqLiteDatabase); //Call onCreate to create the table
    }

    /*
     *  Insert a user entry into the table
     *  BUDGET, DISTANCE, TIME are default at 0
     */
    public void addUser(int ID) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_ID, ID);
        values.put(KEY_BUDGET, 0);
        values.put(KEY_TIME, 0);
        values.put(KEY_DISTANCE, 0);

        db.insert(TABLE_NAME, null, values); //Update and insert into table
        db.close();
    }

    /*
     *  Update the change in constraints for budget
     */
    public int updateBudget(int ID, int budget) {
        SQLiteDatabase db= this.getWritableDatabase(); //Get the database to make changes
        ContentValues values= new ContentValues(); //Prepare content values to update
        values.put(KEY_BUDGET, budget);
        String id_number= valueOf(ID); //Convert the ID to a string to update the table
        int returnValue = db.update(TABLE_NAME, values, KEY_ID + " =?", new String[]{id_number}); //Update constraints where IDs match
        db.close();
        return returnValue;
    }

    /*
     *  Update the change in constraints for distance
     */
    public int updateDistance(int ID, int distance) {
        SQLiteDatabase db= this.getWritableDatabase(); //Get the database to make changes
        ContentValues values= new ContentValues(); //Prepare content values to update
        values.put(KEY_DISTANCE, distance);
        String id_number= valueOf(ID); //Convert the ID to a string to update the table
        int returnValue = db.update(TABLE_NAME, values, KEY_ID + " =?", new String[]{id_number}); //Update constraints where IDs match
        db.close();
        return returnValue;
    }

    /*
     *  Update the change in constraints for budget
     */
    public int updateTime(int ID, int time) {
        SQLiteDatabase db= this.getWritableDatabase(); //Get the database to make changes
        ContentValues values= new ContentValues(); //Prepare content values to update
        values.put(KEY_TIME, time);
        String id_number= valueOf(ID); //Convert the ID to a string to update the table
        int returnValue = db.update(TABLE_NAME, values, KEY_ID + " =?", new String[]{id_number}); //Update constraints where IDs match
        db.close();
        return returnValue;
    }

    /*
     *  Delete the user entry
     */
    public void deleteUser(int ID) {
        SQLiteDatabase db = this.getWritableDatabase(); // Get database for writing to delete
        db.delete(TABLE_NAME, KEY_ID + " =?", new String[]{String.valueOf(ID)}); //Delete the entry in the table where IDs match
        db.close();
    }

    /*
     *  Check if the user entry exists in the database
     */
    public boolean userExists (int ID) {
        SQLiteDatabase db= this.getReadableDatabase(); //Get database for reading
        //Create cursor to query the table and finding user that matches the ID
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_BUDGET, KEY_DISTANCE, KEY_TIME},
                KEY_ID + " =?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor!=null) {
            //If the cursor doesn't point to anything the entry doesn't exist
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return false;
            }
        }
            cursor.close();
            db.close();
            return true;
    }

    /*
     *  Get budget constraint of the user ID
     */
    public int getBudget(int ID) {
        SQLiteDatabase db= this.getReadableDatabase(); //Get database for reading
        //Create cursor to query the table and finding user that matches the ID
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_BUDGET, KEY_DISTANCE, KEY_TIME},
                KEY_ID + " =?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor!=null) //If the cursor found the entry move to the first instance
            cursor.moveToFirst();
        db.close();
        return cursor.getInt(1); //Get the budget in position 1 of the table
    }

    /*
     *  Get distance constraint of the user ID
     */
    public int getDistance(int ID) {
        SQLiteDatabase db= this.getReadableDatabase(); //Get database for reading
        //Create cursor to query the table and finding user that matches the ID
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_BUDGET, KEY_DISTANCE, KEY_TIME},
                KEY_ID + " =?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor!=null)
            cursor.moveToFirst();
        db.close();
        return cursor.getInt(2);
    }

    /*
     *  Get user information
     */
    public int getTime(int ID) {
        SQLiteDatabase db= this.getReadableDatabase(); //Get database for reading
        //Create cursor to query the table and finding user that matches the ID
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_BUDGET, KEY_DISTANCE, KEY_TIME},
                KEY_ID + " =?",
                new String[]{String.valueOf(ID)}, null, null, null, null);
        if (cursor!=null)
            cursor.moveToFirst();
        db.close();
        return cursor.getInt(3);
    }
}
