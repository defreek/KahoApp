package be.kahosl.kdisk;

import android.util.Log;

public class KDiskNoConnectionException extends KDiskException {
	private static final long serialVersionUID = 7658897464895211600L;

	public KDiskNoConnectionException(int code) {
		super(Log.ERROR, code, "Kan geen verbinding maken met de server.");
	}
}