package br.com.pwms;

import java.util.List;

public class Manifest {

	private List<Item> items;
	private String title;
	private String xmlVersion;
	private String type;
	
	public Manifest() {
		super();
	}
	

	public Manifest(List<Item> items, String title, String version, String type) {
		super();
		this.items = items;
		this.title = title;
		this.xmlVersion = version;
		this.type = type;
	}



	public List<Item> getItems() {
		return items;
	}



	public void setItems(List<Item> item) {
		this.items = item;
	}



	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return xmlVersion;
	}

	public void setVersion(String version) {
		this.xmlVersion = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
