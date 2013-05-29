package com.example.helloaliens;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class NasaRssHandler extends RssHandler {
	private StringBuffer chars = new StringBuffer();
	private RssItem item = new RssItem();

	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		chars = new StringBuffer();
		if (localName.equalsIgnoreCase("enclosure")) {
			item.setImageUrl(atts.getValue("url"));
		}
	}

	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("title")) {
			item.setTitle(chars.toString());
		} else if (localName.equalsIgnoreCase("description")) {
			item.setDescription(chars.toString());
		} else if (localName.equalsIgnoreCase("pubDate")) {
			item.setDate(chars.toString());
		} else if (localName.equalsIgnoreCase("link")) {
			item.setUrl(chars.toString());
		}
	}
	
	@Override
	public RssItem getItem() {
		return item;
	}

	@Override
	public String getFeedUrl() {
		return "http://www.nasa.gov/rss/image_of_the_day.rss";
	}

}