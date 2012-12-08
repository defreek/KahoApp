package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
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
import be.kahosl.KahoslActivity;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class KDiskFragment extends Fragment implements TabFragment, OnItemClickListener, OnItemLongClickListener, OnClickListener, OnSharedPreferenceChangeListener {
	
	private FTPSHandler ftpHandler;
	private FileAdapter fileAdapter;
	private TextView status;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Adapter
        fileAdapter = new FileAdapter(getActivity());
        
        // Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
        
        // FTP Handler
		ftpHandler = new FTPSHandler("ftps.ikdoeict.be", preferences.getString("pref_login", ""), preferences.getString("pref_pass", ""), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Views
        View kDiskView = inflater.inflate(R.layout.kdisk_view, container, false);
        status = (TextView) kDiskView.findViewById(R.id.status);
        GridView gridView = (GridView) kDiskView.findViewById(R.id.fileList);
        gridView.setAdapter(fileAdapter);
        
        // Contextmenu
        registerForContextMenu(gridView);
        
        // Listeners
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        ((ImageButton) kDiskView.findViewById(R.id.btnUp)).setOnClickListener(this);

        return kDiskView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(!ftpHandler.isConnected())
			ftpHandler.connect();
	}

	public int getIcon(){
		return R.drawable.ic_menu_kdisk;
	}
	
	
	/* UI functies */
	protected void updateUIStatus(final String statusDescription){
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if(status != null)
					status.setText(statusDescription);
			}
		});
	}
	
	protected void updateUIFiles(final FTPFile[] files, final String cwd){
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				fileAdapter.updateData(files);
				if(status != null)
					status.setText(cwd);
			}
		});
	}
	
	protected void showDialog(String message) {
		((KahoslActivity) getActivity()).showDialog(message);
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
	
	// Bestand of map lang aangeklikt
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		fileAdapter.setSelected(position);
		parent.showContextMenu();
		return true;
	}
	
	// Instellingen veranderd
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		Log.wtf("update", key);
		
		if(key.equals("pref_login") || key.equals("pref_pass")) {
			ftpHandler.updateLoginCredentials(preferences.getString("pref_login", ""), preferences.getString("pref_pass", ""));
			ftpHandler.connect();
		}
	}
	

	// Context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(fileAdapter.getSelected().getName());
        getActivity().getMenuInflater().inflate(R.menu.kdisk_file_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.toString().equals(getString(R.string.kdisk_open)))
			open(fileAdapter.getSelected());
		
		else if (item.toString().equals(getString(R.string.kdisk_delete)))
			delete(fileAdapter.getSelected());
		
		/* TODO
		else if (item.toString().equals(getString(R.string.kdisk_cut)))
			cut(fileAdapter.getSelected());
		
		else if (item.toString().equals(getString(R.string.kdisk_paste)))
			cut(fileAdapter.getSelected());
		*/

		return super.onContextItemSelected(item);
	}
	
	
	/* Bestandsoperaties */
	private void open(FTPFile file) {
		if(file.isDirectory())
			ftpHandler.changeWorkingDirectory(file.getName());
		else
			ftpHandler.getFile(file);
	}
	
	private void delete(FTPFile file) {
		ftpHandler.deleteFile(file);
	}
	
	/* TODO
	private void cut(FTPFile file) {
		temp = file;
	}
	
	private void paste(FTPFile dir) {
		ftpHandler.moveFile(dir, temp);
	}
	*/

}
