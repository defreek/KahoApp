package be.kahosl.whatsrecent;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import be.kahosl.R;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;

public class WhatsRecentCursorAdapter extends CursorAdapter {
    private Context context;
    LayoutInflater mInflater;
    
    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();

    
    public WhatsRecentCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        
        this.context = context;
        mInflater = LayoutInflater.from(context);

        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, false); // initializes all items value with false
        }
    }
    
    

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	
    	if (cursor.getInt(cursor.getColumnIndex(WhatsRecentDatabase.COL_VISIBLE)) == 1) {
    		TextView title_view = (TextView)view.findViewById(R.id.title);
            title_view.setText(cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_TITLE)));
            
            TextView author_view = (TextView)view.findViewById(R.id.author);
            author_view.setText(cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_AUTHOR)));
            
            TextView date_view = (TextView)view.findViewById(R.id.date);
            date_view.setText(cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_DATE)));
            
//            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
//            DateTime dt = parser.parseDateTime(cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_DATE)));
//
//            DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
//            Log.e("date", formatter.print(dt));
            
    	
            
            final int position = cursor.getPosition();
            final CheckBox cBox = (CheckBox) view.findViewById(R.id.checkBox);
            // CheckBox
            cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemChecked.set(position, true);
                        // do some operations here
                    } else if (!isChecked) {
                        itemChecked.set(position, false);
                        // do some operations here
                    }
                }
            });
            cBox.setChecked(itemChecked.get(position));
    	}
    }
    

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
    }



	@Override
	public Cursor swapCursor(Cursor newCursor) {
	    for (int i = 0; i < newCursor.getCount(); i++) {
	        itemChecked.add(i, false); // initializes all items value with false
	    }
		return super.swapCursor(newCursor);
	}
    
	public void hideCheckedItems() {
		ContentValues editedValues = new ContentValues();
        editedValues.put(WhatsRecentDatabase.COL_VISIBLE, 0);
        
		for (int i=0; i<itemChecked.size(); i++) {
			if (itemChecked.get(i)) {
		        context.getContentResolver().update(
		        		Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
								i + ""), 
		        	      editedValues, 
		        	      null, 
		        	      null);
			}
		}
		
		
	}
}