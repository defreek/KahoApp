package be.kahosl.kdisk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class FTPSHandler extends Handler {
	

	private String host, username, password;
	private KDiskFragment ui;
	
	private FTPSClient ftp;
	
	// status FTPSHandler
	// <0 = Busy
	// 0  = Ready
	// >0 = Error
	private int status;
	private String statusDescription;
	
	// Current Working Directory
	private String cwd;
	private String root;
	
	// Folders and files in cwd
	private FTPFile[] fileList;
	
	
	public FTPSHandler(String host, String username, String password, KDiskFragment ui) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.ui = ui;
		
		// Root directory
		root = "/kschijf/" + username.substring(0, username.indexOf('@'));
		cwd = root;
	}
	
	
	// Status bewerkingen
	protected int getStatus() {
		return status;
	}
	
	protected boolean isReady() {
		return status == 0;
	}
	
	protected String getStatusDescription() {
		return statusDescription;
	}

	private void ready() {
		status = 0;
		statusDescription = "Klaar";
		
		ui.updateUIFiles(fileList, getCWD());
	}
	
	private void setStatus(int status, String statusDescription) {
		this.status = status;
		this.statusDescription = statusDescription;
		
		ui.updateUIStatus(statusDescription);
	}
	
	protected FTPFile[] getList() {
		return fileList;
	}
	
	protected String getCWD() {
		return "K:" + cwd.substring(root.length());
	}
	
	
	// Connectie bewerkingen
	protected void connect() {
		connect(host, username, password);
	}
	
	private void connect(final String host, final String username, final String password) {
		setStatus(-1, "Verbinden");
		
		Runnable r = new Runnable() {
			
			private boolean loggedIn;

			public void run() {
				try {
					ftp = new FTPSClient();
					ftp.connect(host, 21);
	
					// verbonden ?
					if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
						loggedIn = ftp.login(username, password);
						
						// Specifieke instellingen ftp kahosl
			            ftp.execPBSZ(0);
						ftp.execPROT("P");
						
						// Overdracht instellingen
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
			            ftp.enterLocalPassiveMode();
			            
					} else {
						throw new KDiskNoConnectionException(ftp.getReplyCode());
					}
					
					if (!loggedIn) {
						throw new KDiskLoginException(ftp.getReplyCode());
					}
					
					changeToLastWorkingDirectory();
					
				} catch (KDiskException e) {
					setStatus(e.getCode(), e.getDescription());
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};

		new Thread(r).start();
	}
	
	
	// Navigatie bewerkingen
	private void changeToLastWorkingDirectory() {
		changeWorkingDirectory(cwd);
	}
	
	protected void changeWorkingDirectory(final String path) {
		setStatus(-1, "Map openen");
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				// TODO: testen connectie onderbroken
				if(!ftp.isConnected())
					connect();
					
				try {
					ftp.changeWorkingDirectory(path);
					fileList = ftp.listFiles();
					cwd = ftp.printWorkingDirectory();

					ready();
					
				} catch (IOException e) {
					setStatus(-1, "TODO ERROR");
					// TODO KDISK EXCEPTION maken
				}
			}
		};

		new Thread(r).start();
	}
	
	protected boolean inRoot() {
		return cwd.equals(root);
	}
	
	
	// Download bewerkingen
	protected void getFile(final FTPFile file) {
		setStatus(-1, "Bestand downloaden");
		
		Runnable r = new Runnable() {
			
			public void run() {
				
				try {
					File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file.getName());
					OutputStream output = new FileOutputStream(localFile);
					
					ftp.retrieveFile(file.getName(), output);
					output.close();
					
					ready();
					
					// Open bestand met standaardapplicatie
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setData(Uri.fromFile(localFile));
					ui.getActivity().startActivity(intent);
				      
				} catch (FileNotFoundException e) {
					// TODO
					setStatus(-1, "TODO ERROR");
				} catch (IOException e) {
					setStatus(-1, "TODO ERROR");
				}
			}
		};

		new Thread(r).start();
	}
}
