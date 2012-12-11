package be.kahosl.addressbook;

import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLParser {
	
	private AddressBookFragment ui;
	
	public XMLParser(AddressBookFragment ui) {
		this.ui = ui;
	}
	
	public void searchContacts(final String query) {
		Runnable r = new Runnable() {
			
			public void run() {
				try {
					// pr_kahosl.ikdoeict.be -> underscores niet ondersteund
					
					HttpGet uri = new HttpGet("http://jarnogoossens.ikdoeict.be/kahosl.php?q=" + URLEncoder.encode(query, "UTF-8"));
			
					DefaultHttpClient client = new DefaultHttpClient();
					HttpResponse resp = client.execute(uri);
			
					StatusLine status = resp.getStatusLine();
					if (status.getStatusCode() != 200) {
					    Log.d("", "HTTP error, invalid server status code: " + resp.getStatusLine());  
					}

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(resp.getEntity().getContent());
					
					parseXML(doc);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(r).start();
	}
	
	private void parseXML(Document doc) {
		NodeList nodes = doc.getElementsByTagName("contact");
		Contact[] contacts = new Contact[nodes.getLength()];
		
		for (int i = 0; i < nodes.getLength(); i++)
			contacts[i] = new Contact(getValue(nodes.item(i), "name"), getValue(nodes.item(i), "mail"));
		
		ui.updateUI(contacts);
	}
	
	private String getValue(Node contact, String node) {
	    NodeList n = ((Element) contact).getElementsByTagName(node);
	    
	    return (n.item(0) != null) ? n.item(0).getTextContent() : "";
	}
	
}
