package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
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

    public FileAdapter(Context c) {
        mContext = c;
        files = null;
    }

    public int getCount() {
    	if (files != null)
    		return files.length;
    	else
    		return 1;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
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
        	 fileView.setLayoutParams(new GridView.LayoutParams(150, 150));
         } else {
        	 fileView = (TextView) convertView;
         }
         
    	 if(files == null) {
    		 fileView.setText("Loading");
    	 } else {
    		 fileView.setText(files[position].getName());
    		 fileView.setCompoundDrawablesWithIntrinsicBounds(0, files[position].isDirectory() ? R.drawable.ic_kdisk_folder : R.drawable.ic_kdisk_file, 0, 0);
    	 }
    	 
    	 fileView.setGravity(Gravity.CENTER);
    	 
    	 return fileView;
    }
}
