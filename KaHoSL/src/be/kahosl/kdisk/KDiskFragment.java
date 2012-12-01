package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class KDiskFragment extends TabFragment implements OnItemClickListener, OnClickListener {
	
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
        
        // Listeners
        gridView.setOnItemClickListener(this);
        ((ImageButton) kDiskView.findViewById(R.id.btnUp)).setOnClickListener(this);
        
        // Adapter
        fileAdapter = new FileAdapter(kDiskView.getContext());
        gridView.setAdapter(fileAdapter);
        
        // FTP Handler
		ftpHandler = new FTPSHandler("ftps.ikdoeict.be", "jarno.goossens@kahosl.be", "J7tej7ET", this);
		ftpHandler.connect();
			// TODO: updaten login credentials indien gewijzigd
		
        return kDiskView;
	}

	@Override
	public int getIcon(){
		return R.drawable.ic_menu_kdisk;
	}
	
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

	// Bestand of map aangeklikt
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FTPFile file = fileAdapter.getItem(position);
		
		if(file.isDirectory()) {
			ftpHandler.changeWorkingDirectory(file.getName());
		} else {
			Log.wtf("Open file", file.getName());
		}
	}

	// Map omhoog
	public void onClick(View v) {
		Log.wtf("root?", ftpHandler.inRoot() + "");
		if(!ftpHandler.inRoot())
			ftpHandler.changeWorkingDirectory("..");
	}

}
