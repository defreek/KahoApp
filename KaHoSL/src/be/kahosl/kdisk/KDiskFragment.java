package be.kahosl.kdisk;

import org.apache.commons.net.ftp.FTPFile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.kahosl.R;
import be.kahosl.TabFragment;

public class KDiskFragment extends TabFragment {
	
	private FTPSHandler ftpHandler;
	private FileAdapter fileAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.wtf("Start module", "K-Schijf");
		
		//fileAdapter = new FileAdapter(getActivity(), R.id.fileList, android.R.layout.simple_list_item_1);
		
		//GridView gridview = (GridView) kDiskView.findViewById(R.id.fileList);
	    //gridview.setAdapter(fileAdapter);
		
		// TODO: setItemonclicklistener
		
		// TODO: updaten login credentials indien gewijzigd
		ftpHandler = new FTPSHandler("ftps.ikdoeict.be", "jarno.goossens@kahosl.be", "J7tej7ET", this);
		
		ftpHandler.connect();

		return inflater.inflate(R.layout.kdisk_view, container, false);
	}

	@Override
	public int getIcon(){
		return R.drawable.ic_menu_kdisk;
	}
	
	public void updateUI(){
		Log.wtf("KDISK updateUI", ftpHandler.getStatusDescription());
		
		if (ftpHandler.isReady())
			showFileList();
	}
	
	public void showFileList(){
		FTPFile[] fileList = ftpHandler.getList();
        int length = fileList.length;

        for (int i = 0; i < length; i++) {
            String name = fileList[i].toString();
            boolean isFile = fileList[i].isFile();

            if (isFile) {
                Log.wtf("list", "File : " + name);
            }
            else {
                Log.wtf("list", "Directory : " + name);
            }
        }
        

        //fileAdapter.addAll(fileList);
	}
}
