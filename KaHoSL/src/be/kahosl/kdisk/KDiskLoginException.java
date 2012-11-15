package be.kahosl.kdisk;

import android.util.Log;

public class KDiskLoginException extends KDiskException {
	private static final long serialVersionUID = 3522123745635312289L;

	public KDiskLoginException(int code) {
		super(Log.ERROR, code, "Gebruikersnaam/wachtwoord verkeerd.");
	}
}
