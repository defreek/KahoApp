package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class KDiskFragment extends TabFragment {
	
	private FTPSHandler ftpHandler;
	private FileAdapter fileAdapter;
	private TextView status;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "K-Schijf");
		super.onCreate(savedInstanceState);
		
        View kDiskView = inflater.inflate(R.layout.kdisk_view, container, false);
        
        status = (TextView) kDiskView.findViewById(R.id.status);
        status.setText(R.string.kdisk);
        
        GridView gridView = (GridView) kDiskView.findViewById(R.id.fileList);
        fileAdapter = new FileAdapter(kDiskView.getContext());
        gridView.setAdapter(fileAdapter);
        
		// TODO: setItemonclicklistener
		// TODO: updaten login credentials indien gewijzigd
        
		ftpHandler = new FTPSHandler("ftps.ikdoeict.be", "jarno.goossens@kahosl.be", "J7tej7ET", this);
		ftpHandler.connect();

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
}
