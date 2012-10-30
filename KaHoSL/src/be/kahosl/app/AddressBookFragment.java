package be.kahosl.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddressBookFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("AB");
		return inflater.inflate(R.layout.address_book_view, container, false);
	}
	
	// TODO : on pause, on create ....
}
