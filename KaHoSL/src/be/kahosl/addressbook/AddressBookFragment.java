package be.kahosl.addressbook;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AddressBookFragment extends Fragment implements TabFragment, OnQueryTextListener {
	
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
        ((SearchView) abView.findViewById(R.id.searchbox)).setOnQueryTextListener(this);
        ((ListView) abView.findViewById(R.id.searchList)).setAdapter(contactAdapter);
        
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

	public boolean onQueryTextChange(String q) {
		return false;
	}

	public boolean onQueryTextSubmit(String q) {
		xml.searchContacts(q);
		return false;
	}
}
