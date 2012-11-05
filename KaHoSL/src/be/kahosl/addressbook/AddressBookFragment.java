package be.kahosl.addressbook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class AddressBookFragment extends TabFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "Adresboek");
		
		return inflater.inflate(R.layout.address_book_view, container, false);
	}
	
	@Override
	public int getIcon(){
		return R.drawable.ic_menu_address_book;
	}
}