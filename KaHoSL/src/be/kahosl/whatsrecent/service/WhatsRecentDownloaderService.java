/*
 * Copyright (c) 2011, Lauren Darcey and Shane Conder
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following disclaimer.
 *   
 * * Redistributions in binary form must reproduce the above copyright notice, this list 
 *   of conditions and the following disclaimer in the documentation and/or other 
 *   materials provided with the distribution.
 *   
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific prior 
 *   written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * <ORGANIZATION> = Mamlambo
 */
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
import android.text.TextUtils;
import android.util.Log;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;

public class WhatsRecentDownloaderService extends Service {

	private static final String DEBUG_TAG = "WhatsRecentDownloaderService";
	private DownloaderTask whatsrecentDownloader;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		URL tutorialPath;
		try {
			tutorialPath = new URL(intent.getDataString());
			whatsrecentDownloader = new DownloaderTask();
			whatsrecentDownloader.execute(tutorialPath);
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
				//succeeded = xmlParse(downloadPath);
				succeeded = feedParse(downloadPath);
			}
			return succeeded;
		}

		private boolean xmlParse(URL downloadPath) {
			boolean succeeded = false;

			XmlPullParser whatsrecent;

			try {
				whatsrecent = XmlPullParserFactory.newInstance().newPullParser();
				whatsrecent.setInput(downloadPath.openStream(), null);
				int eventType = -1;
				// psuedo code--
				// for each found "item" tag, find "link" and "title" tags
				// before end tag "item"

				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						String tagName = whatsrecent.getName();
						if (tagName.equals("item")) {

							ContentValues whatsrecentData = new ContentValues();
							// inner loop looking for link and title
							while (eventType != XmlPullParser.END_DOCUMENT) {
								if (eventType == XmlPullParser.START_TAG) {
									if (whatsrecent.getName().equals("link")) {
										whatsrecent.next();
										Log.d(DEBUG_TAG,
												"Link: " + whatsrecent.getText());
										whatsrecentData.put(
												WhatsRecentDatabase.COL_URL,
												whatsrecent.getText());
									} else if (whatsrecent.getName().equals(
											"title")) {
										whatsrecent.next();
										whatsrecentData.put(
												WhatsRecentDatabase.COL_TITLE,
												whatsrecent.getText());
									}
								} else if (eventType == XmlPullParser.END_TAG) {
									if (whatsrecent.getName().equals("item")) {
										// save the data, and then continue with
										// the outer loop
										getContentResolver()
												.insert(WhatsRecentProvider.CONTENT_URI,
														whatsrecentData);
										break;
									}
								}
								eventType = whatsrecent.next();
							}
						}
					}
					eventType = whatsrecent.next();
				}
				// no exceptions during parsing
				succeeded = true;
			} catch (XmlPullParserException e) {
				Log.e(DEBUG_TAG, "Error during parsing", e);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "IO Error during parsing", e);
			}

			return succeeded;
		}

		private boolean feedParse(URL downloadPath) {
			boolean succeeded = false;
			
			XmlPullParser xpp;
			
			try {
				xpp = XmlPullParserFactory.newInstance().newPullParser();
				xpp.setInput(downloadPath.openStream(), null);
				
				int depth = xpp.getDepth();
				assert (depth == 1);
				int eventType;

				xpp.require(XmlPullParser.START_TAG, null, "feed");
				while (((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT)
						&& (xpp.getDepth() > depth)) {
					// loop invariant: At this point, the parser is not sitting
					// on
					// end-of-document, and is at a level deeper than where it
					// started.
					
					ContentValues whatsrecentData = new ContentValues();

					if (eventType == XmlPullParser.START_TAG) {
						String tag = xpp.getName();
						// Log.d("parseFeed", "Start tag: " + tag); // Uncomment
						// to debug
						if (FeedEntry.TAG_ENTRY.equalsIgnoreCase(tag)) {
							FeedEntry feedEntry = new FeedEntry(xpp);
							
							whatsrecentData.put(
									WhatsRecentDatabase.COL_URL,
									feedEntry.origLink);
							whatsrecentData.put(
									WhatsRecentDatabase.COL_TITLE,
									feedEntry.title);
							getContentResolver()
							.insert(WhatsRecentProvider.CONTENT_URI,
									whatsrecentData);
							
							// feedEntry.persist(this);
							// Log.d("FeedEntry", feedEntry.title); // Uncomment
							// to debug
							// xpp.require(XmlPullParser.END_TAG, null, tag);
						}
					}
				}
				assert (depth == 1);
				succeeded = true;
			} catch (XmlPullParserException e) {
				Log.e(DEBUG_TAG, "Error during parsing", e);
			} catch (IOException e) {
				Log.e(DEBUG_TAG, "IO Error during parsing", e);
			}

			return succeeded;
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
	String updated;
	// Timestamp lastRead;
	String title;
	String subtitle;
	String authorName;
	int contentType;
	String content;
	String preview;
	String origLink;
	String thumbnailUri;
	// Media media;

	static final String TAG_ENTRY = "entry";
	static final String TAG_ENTRY_ID = "id";
	static final String TAG_TITLE = "title";
	static final String TAG_SUBTITLE = "subtitle";
	static final String TAG_UPDATED = "updated";
	static final String TAG_PUBLISHED = "published";
	static final String TAG_AUTHOR = "author";
	static final String TAG_CONTENT = "content";
	static final String TAG_TYPE = "type";
	static final String TAG_ORIG_LINK = "origLink";
	static final String TAG_THUMBNAIL = "thumbnail";
	static final String ATTRIBUTE_URL = "url";

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
			while (((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT)
					&& (xpp.getDepth() > depth)) {

				if (eventType == XmlPullParser.START_TAG) {
					String tag = xpp.getName();
					if (TAG_ENTRY_ID.equalsIgnoreCase(tag)) {
						id = XmlPullTag(xpp, TAG_ENTRY_ID);
					} else if (TAG_TITLE.equalsIgnoreCase(tag)) {
						title = XmlPullTag(xpp, TAG_TITLE);
					} else if (TAG_SUBTITLE.equalsIgnoreCase(tag)) {
						subtitle = XmlPullTag(xpp, TAG_SUBTITLE);
					} else if (TAG_UPDATED.equalsIgnoreCase(tag)) {
						updated = XmlPullTag(xpp, TAG_UPDATED);
					} else if (TAG_PUBLISHED.equalsIgnoreCase(tag)) {
						published = XmlPullTag(xpp, TAG_PUBLISHED);
					} else if (TAG_CONTENT.equalsIgnoreCase(tag)) {
						content = XmlPullTag(xpp, TAG_CONTENT);
						// extractPreview();
					} else if (TAG_AUTHOR.equalsIgnoreCase(tag)) {
						// Skip author for now -- it is complicated
						int authorDepth = xpp.getDepth();
						assert (authorDepth == 3);
						xpp.require(XmlPullParser.START_TAG, null, TAG_AUTHOR);
						while (((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT)
								&& (xpp.getDepth() > authorDepth)) {
						}
						assert (xpp.getDepth() == 3);
						xpp.require(XmlPullParser.END_TAG, null, TAG_AUTHOR);

					} else if (TAG_ORIG_LINK.equalsIgnoreCase(tag)) {
						origLink = XmlPullTag(xpp, TAG_ORIG_LINK);
					} else if (TAG_THUMBNAIL.equalsIgnoreCase(tag)) {
						thumbnailUri = XmlPullAttribute(xpp, tag, null,
								ATTRIBUTE_URL);
					} else {
						@SuppressWarnings("unused")
						String throwAway = XmlPullTag(xpp, tag);
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

	public static String XmlPullAttribute(XmlPullParser xpp, String tag,
			String namespace, String name) throws XmlPullParserException,
			IOException {
		assert (!TextUtils.isEmpty(tag));
		assert (!TextUtils.isEmpty(name));
		xpp.require(XmlPullParser.START_TAG, null, tag);
		String itemText = xpp.getAttributeValue(namespace, name);
		if (xpp.getEventType() != XmlPullParser.END_TAG) {
			xpp.nextTag();
		}
		xpp.require(XmlPullParser.END_TAG, null, tag);
		return itemText;
	}
}
