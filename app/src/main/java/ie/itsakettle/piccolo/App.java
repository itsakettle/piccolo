package ie.itsakettle.piccolo;

import android.app.Application;
import ie.itsakettle.piccolo.contentprovider.DBHelper;
import android.database.sqlite.SQLiteDatabase;

public class App extends Application {
    private static SQLiteDatabase db;

    public static SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DBHelper(getApplicationContext()).getWritableDatabase();
    }
    
    public void reOpenDB()
    {
    	db.close();
    	db = new DBHelper(getApplicationContext()).getWritableDatabase();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        db.close();
    }
}