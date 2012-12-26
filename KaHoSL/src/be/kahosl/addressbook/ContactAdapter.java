package be.kahosl.addressbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import be.kahosl.R;

public class ContactAdapter extends BaseAdapter {

    private Context mContext;
    private Contact[] contacts;
    
	public ContactAdapter(Context c) {
		mContext = c;
		contacts = new Contact[0];
	}
	
	public int getCount() {
		return contacts.length;
	}
    
    @Override
	public boolean isEmpty() {
    	Log.wtf("empty", "" + (contacts.length == 0));
		return contacts.length == 0;
	}
    
	public Contact getItem(int position) {
		return contacts[position];
	}

	public long getItemId(int position) {
		// Not implemented
		return 0;
	}
	
	public void clear() {
		updateData(new Contact[0]);
	}
	
	public void updateData(Contact[] contacts) {
		this.contacts = contacts;
		notifyDataSetChanged();
	}

    public View getView(int position, View convertView, ViewGroup parent) {
         if (convertView == null)
        	 convertView = LayoutInflater.from(mContext).inflate(R.layout.address_book_item, null);

         ((TextView) convertView.findViewById(R.id.name)).setText(contacts[position].getName());
         ((TextView) convertView.findViewById(R.id.mail)).setText(contacts[position].getMail());
         
    	 return convertView;
    }
}
