package j.ex.com.firebasenotificationtestapp;

import android.provider.BaseColumns;

/**
 * Created by Jayanth on 11/26/2016.
 */
public class NotificationContract {
    private NotificationContract(){}

    public static class NotificationEntry implements BaseColumns{
        public static final String TABLE_NAME = "Data";
        public static final String COLUMN_NAME_KEY = "Key";
        public static final String COLUMN_NAME_VALUE = "Value";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    NotificationEntry.COLUMN_NAME_KEY + TEXT_TYPE + COMMA_SEP +
                    NotificationEntry.COLUMN_NAME_VALUE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;
}
