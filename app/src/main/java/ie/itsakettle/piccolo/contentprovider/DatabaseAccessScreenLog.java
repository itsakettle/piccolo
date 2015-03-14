package ie.itsakettle.piccolo.contentprovider;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import ie.itsakettle.piccolo.App;


public class DatabaseAccessScreenLog extends ContentProvider {
	
	public static final String KEY_ID = "_id";
	public static final String KEY_TIME_ON = "time_on";
    public static final String KEY_TIME_OFF = "time_off";
    public static final String KEY_DELTA = "delta";
    public static final String KEY_DAY = "date";
    public static final String KEY_HOUR = "hour";
	
	

	public static final String TABLE_NAME = "SCREEN_LOG";
	private static final int DATABASE_VERSION = 1;
	
	private static final int SCREEN_LOG = 10;
	  private static final int SCREEN_LOG_ID = 20;
	  private static final String AUTHORITY = "ie.itsakettle.piccolo.contentprovider.screen_log";
	  private static final String BASE_PATH = "screen_log";
	  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
              + "/" + BASE_PATH);
	  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
		      + "/screenlogs";
		  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
		      + "/screenlog";

		  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		    static {
		      sURIMatcher.addURI(AUTHORITY, BASE_PATH, SCREEN_LOG);
		      sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SCREEN_LOG_ID);
		    }
		     
	  
    private static final String TAG = "Piccolo ScreenLog DB";
   

   

   
    
    @Override
    public boolean onCreate() {
      return false;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
    	      String[] selectionArgs, String sortOrder) {

    	    // Uisng SQLiteQueryBuilder instead of query() method
    	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    	    // Check if the caller has requested a column which does not exists
    	    //checkColumns(projection);

    	    // Set the table
    	    queryBuilder.setTables(TABLE_NAME);

    	    int uriType = sURIMatcher.match(uri);
    	    switch (uriType) {
    	    case SCREEN_LOG:
    	      break;
    	    case SCREEN_LOG_ID:
    	      // Adding the ID to the original query
    	      queryBuilder.appendWhere(KEY_ID + "="
    	          + uri.getLastPathSegment());
    	      break;
    	    default:
    	      throw new IllegalArgumentException("Unknown URI: " + uri);
    	    }

    	    SQLiteDatabase db = App.getDb();
    	    Cursor cursor = queryBuilder.query(db, projection, selection,
    	        selectionArgs, null,

                    null, sortOrder);
    	    // Make sure that potential listeners are getting notified
    	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    	    return cursor;
    	  }

        
    @Override
    public String getType(Uri uri) {
      return null;
    }
        
    @Override
    public Uri insert(Uri uri, ContentValues values) {
      int uriType = sURIMatcher.match(uri);
      SQLiteDatabase sqlDB =  App.getDb();
      int rowsDeleted = 0;
      long id = 0;
      switch (uriType) {
      case SCREEN_LOG:
        id = sqlDB.insert(TABLE_NAME, null, values);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri + "with type " + uriType );
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH + "/" + id);
    }
        
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
      int uriType = sURIMatcher.match(uri);
      SQLiteDatabase sqlDB =  App.getDb();
      int rowsDeleted = 0;
      switch (uriType) {
      case SCREEN_LOG:
        rowsDeleted = sqlDB.delete(TABLE_NAME, selection,
            selectionArgs);
        break;
      case SCREEN_LOG_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsDeleted = sqlDB.delete(TABLE_NAME,
              KEY_ID + "=" + id, 
              null);
        } else {
          rowsDeleted = sqlDB.delete(TABLE_NAME,
              KEY_ID + "=" + id 
              + " and " + selection,
              selectionArgs);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return rowsDeleted;
    }

        
    @Override
    public int update(Uri uri, ContentValues values, String selection,
        String[] selectionArgs) {

      int uriType = sURIMatcher.match(uri);
      SQLiteDatabase sqlDB = App.getDb();
      int rowsUpdated = 0;
      switch (uriType) {
      case SCREEN_LOG:
        rowsUpdated = sqlDB.update(TABLE_NAME, 
            values, 
            selection,
            selectionArgs);
        break;
      case SCREEN_LOG_ID:
        String id = uri.getLastPathSegment();
        if (TextUtils.isEmpty(selection)) {
          rowsUpdated = sqlDB.update(TABLE_NAME, 
              values,
              KEY_ID + "=" + id, 
              null);
        } else {
          rowsUpdated = sqlDB.update(TABLE_NAME, 
              values,
              KEY_ID + "=" + id 
              + " and " 
              + selection,
              selectionArgs);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
      String[] available = { KEY_ID, KEY_TIME_ON, KEY_TIME_OFF,KEY_DELTA,KEY_HOUR,KEY_DAY, };
      if (projection != null) {
        HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
        HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
        // Check if all columns which are requested are available
        if (!availableColumns.containsAll(requestedColumns)) {
          throw new IllegalArgumentException("Unknown columns in projection");
        }
      }
    }

  } 


        





