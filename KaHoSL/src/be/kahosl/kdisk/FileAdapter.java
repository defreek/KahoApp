package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import be.kahosl.R;

public class FileAdapter extends BaseAdapter {

    private Context mContext;
    private FTPFile[] files;

    // TODO: Lege map
    
    public FileAdapter(Context c) {
        mContext = c;
        files = new FTPFile[0];
    }

    public int getCount() {
   		return files.length;
    }
    
    @Override
	public boolean isEmpty() {
		return files.length == 0;
	}

    public FTPFile getItem(int position) {
        return files[position];
    }
    
	public long getItemId(int position) {
		// Not implemented
		return 0;
	}
    
    public void updateData(FTPFile[] files) {
    	this.files = files;
    	notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView fileView;
    	
         if (convertView == null) {
        	 fileView = new TextView(mContext);
        	 fileView.setLayoutParams(new GridView.LayoutParams(160, 160));
        	 fileView.setMaxLines(2);
        	 fileView.setEllipsize(TruncateAt.END);
         } else {
        	 fileView = (TextView) convertView;
         }

		 fileView.setText(files[position].getName());
		 fileView.setCompoundDrawablesWithIntrinsicBounds(0, files[position].isDirectory() ? R.drawable.ic_kdisk_folder : R.drawable.ic_kdisk_file, 0, 0);
    	 fileView.setGravity(Gravity.CENTER);
    	 
    	 return fileView;
    }
}
