package be.kahosl.whatsrecent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WhatsRecentDatabase extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "WhatsRecentDatabase";
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "whatsrecent_data";

    public static final String TABLE_WHATSRECENT = "whatsrecent";
    public static final String ID = "_id";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "url";
    public static final String COL_COURSE = "course";
    public static final String COL_AUTHOR = "author";
    public static final String COL_TYPE = "type";
    public static final String COL_DATE = "date";
    public static final String COL_VISIBLE = "visible";

    private static final String CREATE_TABLE_WHATSRECENT = "CREATE TABLE "
            + TABLE_WHATSRECENT + " (" + ID + " integer PRIMARY KEY AUTOINCREMENT, "
            + COL_ID + " text UNIQUE NOT NULL, "
    		+ COL_TITLE + " text NOT NULL, "
            + COL_URL + " text NOT NULL, "
            + COL_COURSE + " text NOT NULL, "
            + COL_AUTHOR + " text NOT NULL, "
            + COL_TYPE + " text, "
            + COL_VISIBLE + " integer default 1, "
            + COL_DATE + " datetime NOT NULL);";

    private static final String DB_SCHEMA = CREATE_TABLE_WHATSRECENT;

    public WhatsRecentDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DB_SCHEMA);
    }

    @Override
	public void onOpen(SQLiteDatabase db) {
    	
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHATSRECENT);
		db.execSQL(DB_SCHEMA);
		
		super.onOpen(db);
	}

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WHATSRECENT);
        onCreate(db);
    }
}
