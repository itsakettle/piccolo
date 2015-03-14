package ie.itsakettle.piccolo.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "RemedyDB";
	private static final int DATABASE_VERSION = 1;
    
	
    private static final String TABLE_CREATE_SCREEN_LOG =
            "create table " + DatabaseAccessScreenLog.TABLE_NAME + " ("+ 
            		DatabaseAccessScreenLog.KEY_ID +   		" integer primary key autoincrement, " +
            		DatabaseAccessScreenLog.KEY_TIME_ON +		" integer , " +
                    DatabaseAccessScreenLog.KEY_TIME_OFF +		" integer , " +
                    DatabaseAccessScreenLog.KEY_DELTA +		" integer , " +
                    DatabaseAccessScreenLog.KEY_DAY +		" text , " +
                    DatabaseAccessScreenLog.KEY_HOUR +		" text ) " ;
    	
	
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_CREATE_SCREEN_LOG);
        Log.i("DBHelper", "onCreate called");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Remedy DB", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS energy");
        onCreate(db);
    }
    
}