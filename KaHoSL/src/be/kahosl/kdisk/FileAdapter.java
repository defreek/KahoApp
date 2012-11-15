package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<FTPFile> {
	
	
    private FTPFile[] fileList = new FTPFile[0];

    public FileAdapter(Context c, int resource, int textViewResourceId) {
    	super(c, resource, textViewResourceId);
    	fileList = null;
    }
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	
        TextView label=(TextView)convertView.findViewById(android.R.id.text1);
        label.setText(fileList[position].getName());
        
        //ImageView icon=(ImageView)view.findViewById(android.R.id.listIcon);
        
        /*if (DayOfWeek[position]=="Sunday"){
            icon.setImageResource(R.drawable.file);
        }else{
            icon.setImageResource(R.drawable.icon);
        }
        TextView desc = (TextView)view.findViewById(R.id.description);
        if (DayOfWeek[position]=="Sunday"){
            desc.setText("Description1");
        }else{
            desc.setText("desc");
        }*/
        
        return convertView;
        
    	/*
	    TextView label = (TextView) view.;
        if (arg1 == null) {  // if it's not recycled, initialize some attributes
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(85, 85));
            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) arg1;
        }

        textView.setText(fileList[arg0].getName());
        return textView;*/
	}
}
