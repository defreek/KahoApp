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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class FTPSHandler extends Handler {


	private String host, username, password;
	private KDiskFragment ui;

	private FTPSClient ftp;

	/* status FTPSHandler
	 * <0 = Busy
	 * 0  = Ready
	 * >0 = Error
	 * 	* 1 	= netwerk fout
	 * 	* 10 	= geen internet verbinding
	 * 	* 11 	= geen login gegevens
	 * 	* >=100	= ftp error code
	 */
	private int status;
	private String statusDescription;

	// Current Working Directory
	private String cwd;
	private String root;

	// Folders and files in cwd
	private FTPFile[] fileList;


	protected FTPSHandler(String host, String username, String password, KDiskFragment ui) {
		this.host = host;
		this.ui = ui;

		updateLoginCredentials(username, password);
	}

	protected void updateLoginCredentials(String username, String password) {
		this.username = username;
		this.password = password;

		// Root directory
		if(username.indexOf('@') > 1)
			root = "/kschijf/" + username.substring(0, username.indexOf('@'));
		else
			root = "/kschijf/";

		cwd = root;
	}

	
	/* Status bewerkingen */
	protected boolean isConnected() {
		if(ftp != null)
			return ftp.isAvailable();
		else
			return false;
	}

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
	
	private void setError(int status, String statusDescription) {
		this.status = status;
		this.statusDescription = statusDescription;

		ui.updateUIError(statusDescription);
	}

	protected FTPFile[] getList() {
		return fileList;
	}

	protected String getCWD() {
		return "K:" + cwd.substring(root.length());
	}


	/* Connectie bewerkingen */
	protected void connect() {
		connect(host, username, password);
	}

	private void connect(final String host, final String username, final String password) {
		
		// Vorige verbinding sluiten
		disconnect();
		
		// Beginnen verbinden
		setError(-1, "Verbinden");

		Runnable r = new Runnable() {

			public void run() {
				try {
					if(!isOnline())
						throw new KDiskException(Log.ERROR, 10, "Er is een internetverbinding nodig.");

					if(username.equals("") || password.equals(""))
						throw new KDiskException(Log.ERROR, 11, "Gelieve eerst je gebruikersnaam en wachtwoord in te stellen.");
					
					ftp = new FTPSClient();
					ftp.connect(host, 21);

					// verbonden ?
					if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

						if (ftp.login(username, password)) {
							// Specifieke instellingen ftp kahosl
				            ftp.execPBSZ(0);
							ftp.execPROT("P");

							// Overdracht instellingen
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
				            ftp.enterLocalPassiveMode();

							changeToLastWorkingDirectory();

						} else {
							throw new KDiskException(Log.ERROR, ftp.getReplyCode(), "Gebruikersnaam/wachtwoord verkeerd.");
						}

					} else {
						throw new KDiskException(Log.ERROR, ftp.getReplyCode(), "Kan geen verbinding maken met de server.");
					}

				} catch (KDiskException e) {
					setError(e.getCode(), e.getDescription());

				} catch (IOException e) {
					setError(1, "Netwerk fout, probeer later opnieuw.");
				}
			}
		};

		new Thread(r).start();
	}
	
	private void disconnect() {
		setStatus(-1, "Verbinding verbreken");

		Runnable r = new Runnable() {

			public void run() {
				try {
					if(ftp != null && ftp.isConnected()) {
						ftp.disconnect();
					}
					
					// CWD aanpassen
					fileList = new FTPFile[0];
					cwd = root;
					
					ready();
				} catch (IOException e) {
					setError(1, "Netwerk fout, probeer later opnieuw.");
				}
			}
		};

		new Thread(r).start();
	}
	
	// Verbinding met internet?
	private boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) ui.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return (netInfo != null && netInfo.isConnectedOrConnecting());
	}

	
	/* Navigatie bewerkingen */
	private void changeToLastWorkingDirectory() {
		changeWorkingDirectory(cwd);
	}

	protected void changeWorkingDirectory(final String path) {
		setStatus(-1, "Map openen");

		Runnable r = new Runnable() {

			public void run() {

				// Testen of connectie niet is weggevallen
				if(!ftp.isAvailable())
					connect();

				try {
					ftp.changeWorkingDirectory(path);
					fileList = ftp.listFiles();
					cwd = ftp.printWorkingDirectory();

					ready();

				} catch (IOException e) {
					setError(1, "Netwerk fout, probeer later opnieuw.");
				}
			}
		};

		new Thread(r).start();
	}

	protected boolean inRoot() {
		return cwd.equals(root);
	}


	/* Bestands bewerkingen */
	// Bestand openen
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
					setError(1, "Netwerk fout, probeer later opnieuw.");
				} catch (IOException e) {
					setError(1, "Netwerk fout, probeer later opnieuw.");
				}
			}
		};

		new Thread(r).start();
	}
	
	// Bestand verwijderen
	protected void deleteFile(final FTPFile file) {
		setStatus(-1, "Bestand verwijderen");

		Runnable r = new Runnable() {

			public void run() {
				delete(cwd, file);
				changeToLastWorkingDirectory();
			}
		};

		new Thread(r).start();
	}
	
	// Recursief verwijderen
	private void delete(String path, FTPFile file) {
		try {
			if(file.isDirectory()) {
				for(FTPFile f : ftp.listFiles(path + "/" + file.getName()))
					delete(path + "/" + file.getName(), f);
				
				ftp.removeDirectory(path + "/" + file.getName());
				
			} else {
				ftp.deleteFile(path + "/" + file.getName());
			}
			
		} catch (IOException e) {
			setError(1, "Netwerk fout, probeer later opnieuw.");
		}
	}
	
	// Bestand verplaatsen
	protected void moveFile(FTPFile from, FTPFile to) {
		try {
			ftp.rename(from.getName(), to.getName());
		} catch (IOException e) {
			setError(1, "Netwerk fout, probeer later opnieuw.");
		}
	}
}