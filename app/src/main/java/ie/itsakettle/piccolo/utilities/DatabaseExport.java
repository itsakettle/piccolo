package ie.itsakettle.piccolo.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import ie.itsakettle.piccolo.contentprovider.DBHelper;

public class DatabaseExport extends AsyncTask<String, Void, Boolean> {

	private Context context;
	public static final String piccoloData = "/data/ie.itsakettle.piccolo/databases/";
	private ProgressDialog dialog;
	
	
	public DatabaseExport(Context c)
	{
		super();
		context=c;
		dialog = new ProgressDialog(context);
	}
	
	
	
	// can use UI thread here
    protected void onPreExecute() {
       this.dialog.setMessage("Exporting database...");
       this.dialog.show();
    }

    // automatically done on worker thread (separate from UI thread)
    protected Boolean doInBackground(final String... args) {

    	String databaseName = DBHelper.DATABASE_NAME;
    	String copyDatabaseName;
    	boolean result =false;
    	//if( databaseName.endsWith(".db"))
    	
    		
    	//File test = context.getDatabasePath(databaseName);
    	//Log.i("DB Export",test.getAbsolutePath());
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
    	Date dTime = new Date();
    	copyDatabaseName = "piccoloDB" + " " + sdf.format(dTime) + ".db";
    	
       File dbFile =
                new File(Environment.getDataDirectory() + piccoloData + databaseName);

       File exportDir = new File(Environment.getExternalStorageDirectory(), "");
       if (!exportDir.exists()) {
          exportDir.mkdirs();
       }
       File file = new File(exportDir, copyDatabaseName);

       try {
          file.createNewFile();
          this.copyFile(dbFile, file);
          result=true;
       } catch (IOException e) {
          e.printStackTrace();
       }
       
       return result;
    }

    // can use UI thread here
    protected void onPostExecute(final Boolean success) {
       if (this.dialog.isShowing()) {
          this.dialog.dismiss();
       }
       if (success) {
          Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
       } else {
          Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
       }
       this.context=null;
    }

    void copyFile(File src, File dst) throws IOException {
       FileChannel inChannel = new FileInputStream(src).getChannel();
       FileChannel outChannel = new FileOutputStream(dst).getChannel();
       try {
          inChannel.transferTo(0, inChannel.size(), outChannel);
       }
       finally {
          if (inChannel != null)
             inChannel.close();
          if (outChannel != null)
             outChannel.close();
          
       }
    }

    
	
	

}
