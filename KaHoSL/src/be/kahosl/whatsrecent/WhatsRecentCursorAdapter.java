package be.kahosl.whatsrecent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.TextView;
import be.kahosl.R;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;

public class WhatsRecentCursorAdapter extends CursorAdapter {
    private Context context;
    LayoutInflater mInflater;
    
    private HashMap<Integer, Boolean> checkedItems = new HashMap<Integer, Boolean>();
    
    public WhatsRecentCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
            
            final int id = cursor.getInt(cursor.getColumnIndex(WhatsRecentDatabase.ID));
            
//            DateTimeFormatter parser = ISODateTimeFormat.dateTime();
//            DateTime dt = parser.parseDateTime(cursor.getString(cursor.getColumnIndex(WhatsRecentDatabase.COL_DATE)));
//
//            DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
//            Log.e("date", formatter.print(dt));
            

            final CheckBox cBox = (CheckBox) view.findViewById(R.id.checkBox);
            // CheckBox
            cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedItems.put(id, true);
                        // do some operations here
                    } else if (!isChecked) {
                        checkedItems.put(id, false);
                        // do some operations here
                    }
                }
            });
            cBox.setChecked(checkedItems.get(id));
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
	    newCursor.moveToFirst();
        while (newCursor.isAfterLast() == false) {
        	int id = newCursor.getInt(newCursor.getColumnIndex(WhatsRecentDatabase.ID));
        	if (!checkedItems.containsKey(id))
        		checkedItems.put(id, false);
        	newCursor.moveToNext();
        }
		return super.swapCursor(newCursor);
	}
    
	public void hideCheckedItems() {
		ContentValues editedValues = new ContentValues();
		editedValues.put(WhatsRecentDatabase.COL_VISIBLE, 0);
   
        Iterator it = checkedItems.entrySet().iterator();
        while (it.hasNext()) {
            Entry pairs = (Entry)it.next();
            
            if ((Boolean)pairs.getValue()) {
	            context.getContentResolver().update(
		        		Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
								(Integer)pairs.getKey() + ""), 
		        	      editedValues, 
		        	      null, 
		        	      null);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
	}
	
	public void showAllItems() {
		ContentValues editedValues = new ContentValues();
		editedValues.put(WhatsRecentDatabase.COL_VISIBLE, 1);
		
		context.getContentResolver().update(WhatsRecentProvider.CONTENT_URI, 
        	      editedValues, 
        	      null, 
        	      null);
	}
}