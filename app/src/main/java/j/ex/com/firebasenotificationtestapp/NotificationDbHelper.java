package j.ex.com.firebasenotificationtestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jayanth on 11/26/2016.
 */
public class NotificationDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notification DataBase";
    private static NotificationDbHelper mdbHelper = null;

    private NotificationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static NotificationDbHelper getInstance(Context c){
        if(mdbHelper == null){
            mdbHelper = new NotificationDbHelper(c);
        }
        return mdbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NotificationContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(NotificationContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insert(String message, String type){
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NotificationContract.NotificationEntry.COLUMN_NAME_KEY, type);
        cv.put(NotificationContract.NotificationEntry.COLUMN_NAME_VALUE, message);
        db.insert(NotificationContract.NotificationEntry.TABLE_NAME, null, cv);
    }

    public Cursor queryWarningMessages(){
        SQLiteDatabase db = mdbHelper.getReadableDatabase();
        String query = "Select * from " + NotificationContract.NotificationEntry.TABLE_NAME + " where "
                + NotificationContract.NotificationEntry.COLUMN_NAME_KEY + " = \"" +
                NotificationTypes.WARNING_MESSAGE + "\"";
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    public Cursor queryInfoMessages(){
        SQLiteDatabase db = mdbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("Select * from " + NotificationContract.NotificationEntry.TABLE_NAME + " where "
                + NotificationContract.NotificationEntry.COLUMN_NAME_KEY + " = \"" +
                NotificationTypes.INFO_MESSAGE + "\"", null);
        return c;
    }
}
