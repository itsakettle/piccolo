package ie.itsakettle.piccolo.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ie.itsakettle.piccolo.contentprovider.DBHelper;
import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;

public class DatabaseImport extends AsyncTask<String, Void, Boolean> {

	private Context context;
	private String remedyData ;
	private ProgressDialog dialog ;
	private long lTransferred;

	
	public DatabaseImport(Context c)
	{
		super();
		context=c;
		remedyData = "/data/ie.itsakettle.remedy/databases/";
		dialog = new ProgressDialog(context);
		lTransferred = 0;

	}
	
	
	
	// can use UI thread here
    protected void onPreExecute() {
       this.dialog.setMessage("Importing database...");
       this.dialog.show();
    }

    // automatically done on worker thread (separate from UI thread)
    protected Boolean doInBackground(final String... args) {
    	
    	Boolean result = true;
    	File sd = Environment.getExternalStorageDirectory();
        File data  = Environment.getDataDirectory();
        		
        try{
        if (sd.canWrite()) {
            String currentDBPath= remedyData + DBHelper.DATABASE_NAME;
            String backupDBPath  = args[0];
            File backupDB= new File(sd, backupDBPath);
            File currentDB  = new File(data, currentDBPath);
            
            if ( currentDB.exists() && backupDB.exists()) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                //dst.truncate(0);
                long size = src.size();
               lTransferred = dst.transferFrom(src, 0, src.size());
              
                
            }
        }
       }
        catch(Exception e)
        {
        	e.printStackTrace();
        	result=false;
        }
        
        return result;
    }

    // can use UI thread here
    protected void onPostExecute(final Boolean success) {
  
    	if (this.dialog.isShowing()) {
          this.dialog.dismiss();
       }
       if (success) {
          Toast.makeText(context, "Import successful!", Toast.LENGTH_SHORT).show();
       } else {
          Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show();
       }
       Log.i("Database import", "bytes transferred: " + lTransferred);
		context.getContentResolver().notifyChange(DatabaseAccessScreenLog.CONTENT_URI, null);
       context=null;
    }

    void copyFile(File src, File dst) throws IOException {
       FileChannel inChannel = new FileInputStream(src).getChannel();
       FileChannel outChannel = new FileOutputStream(dst).getChannel();
       try {
          inChannel.transferTo(0, inChannel.size(), outChannel);
       } finally {
          if (inChannel != null)
             inChannel.close();
          if (outChannel != null)
             outChannel.close();
          
       }
    }

    
	
	

}
