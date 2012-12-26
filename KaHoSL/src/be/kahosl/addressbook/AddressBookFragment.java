package be.kahosl.addressbook;

import java.io.Serializable;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AddressBookFragment extends Fragment implements TabFragment, Serializable, Parcelable, OnQueryTextListener, OnItemClickListener {
	
	private static final long serialVersionUID = -2245246540546614731L;
	
	private XMLParser xml;
	private ContactAdapter contactAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Adapter
		contactAdapter = new ContactAdapter(getActivity());
		
		// XML parser
		xml = new XMLParser(this);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Views
        View abView = inflater.inflate(R.layout.address_book_view, container, false);
        ListView searchList = (ListView) abView.findViewById(R.id.searchList);
        SearchView searchBox = (SearchView) abView.findViewById(R.id.searchbox);
        
        searchList.setEmptyView(abView.findViewById(R.id.emptyABView));
        
        // Adapter
        searchList.setAdapter(contactAdapter);
        
        // Listeners
        searchList.setOnItemClickListener(this);
        searchBox.setOnQueryTextListener(this);

		return abView;
	}
	
	protected void updateUI(final Contact[] contacts){
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				contactAdapter.updateData(contacts);
			}
		});
	}

	public int getIcon(){
		return R.drawable.ic_menu_address_book;
	}

	// Zoekterm aan het ingeven
	public boolean onQueryTextChange(String q) {
		if(q.length() > 2)
			xml.searchContacts(q);
		else
			contactAdapter.clear();
		
		return false;
	}

	// Zoekterm ingegeven
	public boolean onQueryTextSubmit(String q) {
		if(q.length() > 2)
			xml.searchContacts(q);
		
		// Hide keyboard
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        
		return false;
	}

	// Contact clicked
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        newMail(contactAdapter.getItem(position));
	}
	
	// Nieuwe mail maken naar contact
	public void newMail(Contact c) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + c.getMail()));
        startActivity(intent);
	}


	public int describeContents() {
		return 0;
	}


	public void writeToParcel(Parcel dest, int flags) {
		// Nothing
	}
}
