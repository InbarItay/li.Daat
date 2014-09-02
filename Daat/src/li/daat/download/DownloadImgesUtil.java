package li.daat.download;

import java.io.File;

import android.os.Environment;

public class DownloadImgesUtil {

	public static File getImageFile(String userName) {
		File file = new File(DownloadImgesUtil.getImagePath(userName));
        if(file.exists()) {
        	return file;
        }
        return null;
	}
	
	public static String getImagePath(String userName) {
		File imagesFolder = new File(Environment.getExternalStorageDirectory(),"Images");
		if(!imagesFolder.exists()) {
			imagesFolder.mkdir();
		}
        return imagesFolder.getPath() + "//"+ userName.replace(' ', '_') + ".jpg";
	}
}
