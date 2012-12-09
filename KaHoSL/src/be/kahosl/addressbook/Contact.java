package be.kahosl.addressbook;

public class Contact {
	private String name;
	private String mail;
	
	public Contact(String name, String mail) {
		this.name = name;
		this.mail = mail; 	 	
	}

	public String getName() {
		return name;
	}

	public String getMail() {
		return mail;
	}
}
