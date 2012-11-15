package be.kahosl.kdisk;

import android.util.Log;

public abstract class KDiskException extends Exception {
	
	private static final long serialVersionUID = -8905427340964691972L;
	
	private int level;	
	private int code;
	private String description;

	public KDiskException(int level, int code, String description) {
		this.level = level;
		this.code = code;
		this.description = description;
		
		logException();
	}

	public void logException(){
		Log.wtf(code + "", description, this);
		// TODO: Exception loggen
	}

	public int getLevel() {
		return level;
	}

	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
}