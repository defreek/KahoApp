package be.kahosl.whatsrecent.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;

public class WhatsRecentDownloaderService extends Service {

	private static final String DEBUG_TAG = "WhatsRecentDownloaderService";
	private DownloaderTask whatsrecentDownloader;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		URL whatsrecentPath;
		try {
			whatsrecentPath = new URL(intent.getDataString());
			whatsrecentDownloader = new DownloaderTask();
			whatsrecentDownloader.execute(whatsrecentPath);
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "Bad URL", e);
		}

		return Service.START_FLAG_REDELIVERY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class DownloaderTask extends AsyncTask<URL, Void, Boolean> {

		private static final String DEBUG_TAG = "WhatsRecentDownloaderService$DownloaderTask";

		@Override
		protected Boolean doInBackground(URL... params) {
			boolean succeeded = false;
			URL downloadPath = params[0];

			if (downloadPath != null) {
				succeeded = xmlParse(downloadPath);
			}
			return succeeded;
		}

		private boolean xmlParse(URL downloadPath) {
			boolean succeeded = false;
			
			XmlPullParserFactory factory = null;
		    StringBuilder out = new StringBuilder();
		    int entries = 0;
		    
		    try {
		        factory = XmlPullParserFactory.newInstance();
		        factory.setNamespaceAware(true);
		        XmlPullParser xpp = factory.newPullParser();
		        xpp.setInput(downloadPath.openStream(), null);

		        while (true) {
		            int eventType = xpp.next();
		            if (eventType == XmlPullParser.END_DOCUMENT) {
		                break;
		            } else if (eventType == XmlPullParser.START_DOCUMENT) {
		                out.append("Start document\n");
		            } else if (eventType == XmlPullParser.START_TAG) {
		                String tag = xpp.getName();
		                // out.append("Start tag " + tag + "\n");
		                if ("feed".equalsIgnoreCase(tag)) {
		                    entries = parseFeed(xpp);
		                }
		            } else if (eventType == XmlPullParser.END_TAG) {
		                // out.append("End tag " + xpp.getName() + "\n");
		            } else if (eventType == XmlPullParser.TEXT) {
		                // out.append("Text " + xpp.getText() + "\n");
		            }
		        }
		        out.append("End document\n");
		        succeeded = true;
			} catch (XmlPullParserException e) {
				Log.e(DEBUG_TAG, "Error during parsing", e);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "IO Error during parsing", e);
			}

			return succeeded;
		}

		private int parseFeed(XmlPullParser xpp) throws XmlPullParserException, IOException {
		    int depth = xpp.getDepth();
		    assert (depth == 1);
		    int eventType;
		    int entries = 0;
		    
		    xpp.require(XmlPullParser.START_TAG, null, "feed");
		    
		    ContentValues whatsrecentData = new ContentValues();
		    		    
		    while ((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT) {
		        // loop invariant: At this point, the parser is not sitting on
		        // end-of-document, and is at a level deeper than where it started.
		        if (eventType == XmlPullParser.START_TAG) {
		            String tag = xpp.getName();
		            //Log.d("parseFeed", "Start tag: " + tag);    // Uncomment to debug
		            if (FeedEntry.TAG_ENTRY.equalsIgnoreCase(tag)) {
		                FeedEntry feedEntry = new FeedEntry(xpp);
		                
		                Log.e("feedEntry", feedEntry.toString());
		                
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_ID,
                                feedEntry.id);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_URL,
                                feedEntry.link);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_TITLE,
                                feedEntry.title);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_COURSE,
                                feedEntry.course);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_AUTHOR,
                                feedEntry.author);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_DATE,
                                feedEntry.published);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_TYPE,
                                feedEntry.type);
		                whatsrecentData.put(
                                WhatsRecentDatabase.COL_VISIBLE,
                                1);
		                getContentResolver().insert(
                                WhatsRecentProvider.CONTENT_URI,
                                whatsrecentData);
		                
		                
		                
		                //feedEntry.persist(this);
		                entries++;
		                // Log.d("FeedEntry", feedEntry.title);    // Uncomment to debug
		                // xpp.require(XmlPullParser.END_TAG, null, tag);
		            }
		        }
		    }
		    assert (depth == 1);
		    return entries;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Log.w(DEBUG_TAG, "XML download and parse had errors");
			}
		}

	}

}

class FeedEntry {
	String id;
	String published;
	String title;
	String author;
	String type;
	String course;
	String link;
	// Media media;

	static final String TAG_ENTRY = "entry";
	static final String TAG_ENTRY_ID = "id";
	static final String TAG_TITLE = "title";
	static final String TAG_PUBLISHED = "published";
	static final String TAG_AUTHOR = "author";
	static final String TAG_CONTENT = "content";
	static final String TAG_ORIG_LINK = "link";

	/**
	 * Create a FeedEntry by pulling its bits out of an XML Pull Parser. Side
	 * effect: Advances XmlPullParser.
	 * 
	 * @param xpp
	 */
	public FeedEntry(XmlPullParser xpp) {
		int eventType;
		int depth = xpp.getDepth();
		assert (depth == 2);
				
		try {
			xpp.require(XmlPullParser.START_TAG, null, TAG_ENTRY);
						
			while ((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT) {
				
				if (eventType == XmlPullParser.START_TAG) {
					String tag = xpp.getName();
					if (TAG_ENTRY_ID.equalsIgnoreCase(tag)) {
						id = XmlPullTag(xpp, TAG_ENTRY_ID);
					} else if (TAG_TITLE.equalsIgnoreCase(tag)) {
						title = XmlPullTag(xpp, TAG_TITLE);
					} else if (TAG_PUBLISHED.equalsIgnoreCase(tag)) {
						published = XmlPullTag(xpp, TAG_PUBLISHED);
					} else if (TAG_CONTENT.equalsIgnoreCase(tag)) {
						
						readContent(xpp);
						// extractPreview();
					} else if (TAG_ORIG_LINK.equalsIgnoreCase(tag)) {
						link = readLink(xpp);
					}
				}
			} // while
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert (xpp.getDepth() == 2);
	}
	
	private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "link");
		
		if (parser.getAttributeValue(null, "rel").equals("alternate")) {
			link = "https://cygnus.cc.kuleuven.be" + parser.getAttributeValue(null, "href");
		}
	    return link;
	}
	
	private void readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "content");
		
		while (!parser.getName().equalsIgnoreCase("p")) {
			parser.nextTag();
		}
		parser.nextTag();
		parser.next();
		parser.next();
		parser.next();
		
		// type
		type = parser.getText().replaceAll("[ :]", "").replaceAll("\n", "");
		
		parser.nextTag();
		parser.nextTag();
		parser.nextTag();
		parser.next();
		parser.next();
		parser.next();
		// title again - already have that
		parser.nextTag();
		parser.nextTag();
		parser.nextTag();
		parser.next();
		parser.next();
		parser.next();
		//course
		course = parser.getText().replaceAll("^\\s+:\\s+-+\\s+", "").replaceAll("\n", "");
		
		parser.nextTag();
		parser.nextTag();
		parser.nextTag();
		parser.next();
		parser.next();
		parser.next();
		
		//author
		author = parser.getText().replaceAll("[ :]", "").replaceAll("\n", "");
		
	    parser.require(XmlPullParser.END_TAG, null, "content");
	}
	
	public String toString() {
		return title +" - " + id + " - " + link + " - " + course + " - " +  author + " - " + published;
	}

	public static String XmlPullTag(XmlPullParser xpp, String tag)
			throws XmlPullParserException, IOException {
		xpp.require(XmlPullParser.START_TAG, null, tag);
		String itemText = xpp.nextText();
		if (xpp.getEventType() != XmlPullParser.END_TAG) {
			xpp.nextTag();
		}
		xpp.require(XmlPullParser.END_TAG, null, tag);
		return itemText;
	}
}
