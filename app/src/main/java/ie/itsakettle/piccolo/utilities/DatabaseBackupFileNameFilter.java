package ie.itsakettle.piccolo.utilities;

import java.io.File;
import java.io.FilenameFilter;

public class DatabaseBackupFileNameFilter implements FilenameFilter {

	public DatabaseBackupFileNameFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean accept(File dir, String filename) {
	
		boolean result=false;

		if(filename.endsWith(".db"))
		{
			result=true;
		}
		
		return result;
	}

}
