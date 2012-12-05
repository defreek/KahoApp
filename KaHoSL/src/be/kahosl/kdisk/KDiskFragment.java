package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class KDiskFragment extends Fragment implements TabFragment, OnItemClickListener, OnItemLongClickListener, OnClickListener {
	
	private FTPSHandler ftpHandler;
	private FileAdapter fileAdapter;
	private TextView status;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "K-Schijf");
		super.onCreate(savedInstanceState);
		
		// Views
        View kDiskView = inflater.inflate(R.layout.kdisk_view, container, false);
        status = (TextView) kDiskView.findViewById(R.id.status);
        GridView gridView = (GridView) kDiskView.findViewById(R.id.fileList);
        
        // Contextmenu
        registerForContextMenu(gridView);
        
        // Listeners
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        ((ImageButton) kDiskView.findViewById(R.id.btnUp)).setOnClickListener(this);
        
        // Adapter
        fileAdapter = new FileAdapter(kDiskView.getContext());
        gridView.setAdapter(fileAdapter);
        
        // Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        
        // FTP Handler
		ftpHandler = new FTPSHandler("ftps.ikdoeict.be", preferences.getString("pref_login", ""), preferences.getString("pref_pass", ""), this);
		ftpHandler.connect();
		
		
		// TODO: updaten login credentials indien gewijzigd
		
        return kDiskView;
	}

	public int getIcon(){
		return R.drawable.ic_menu_kdisk;
	}
	
	
	/* UI functies */
	public void updateUIStatus(final String statusDescription){
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				status.setText(statusDescription);
			}
		});
	}
	
	public void updateUIFiles(final FTPFile[] files, final String cwd){
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				fileAdapter.updateData(files);
				status.setText(cwd);
			}
		});
	}

	
	/* Listeners implementaties */
	// Bestand of map aangeklikt
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		open(fileAdapter.getItem(position));
	}
	
	// Map omhoog
	public void onClick(View v) {
		if(!ftpHandler.inRoot())
			ftpHandler.changeWorkingDirectory("..");
	}

	private int menuPosition;
	
	// Bestand of map lang aangeklikt
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		menuPosition = position; // bijhouden welk bestand is aangeklikt
		parent.showContextMenu();
		return true;
	}
	

	// Context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
		
	    FTPFile file = fileAdapter.getItem(menuPosition);
	    
        menu.setHeaderTitle(file.getName());
        String[] menuItems = {"Openen", "Verwijderen", "Kopiëren", "Knippen"};
        
        for (int i = 0; i<menuItems.length; i++) 
            menu.add(Menu.NONE, menuPosition, i, menuItems[i]);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.toString().equals("Openen"))
			open(fileAdapter.getItem(item.getItemId()));

		return super.onContextItemSelected(item);
	}
	
	private void open(FTPFile file) {
		if(file.isDirectory()) {
			ftpHandler.changeWorkingDirectory(file.getName());
		} else {
			ftpHandler.getFile(file);
		}
	}
}
