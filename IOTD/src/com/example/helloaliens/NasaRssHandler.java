package com.example.helloaliens;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class NasaRssHandler extends RssHandler {
	private List<RssItem> list = new ArrayList<RssItem>();
	private RssItem item;
	private StringBuffer chars;

	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		if (localName.equalsIgnoreCase("item")) {
			item = new RssItem();
			list.add(item);
		} else {
			if (item != null) {
				if (localName.equalsIgnoreCase("enclosure")) {
					item.setImageUrl(atts.getValue("url"));
				}
			}
		}
		
		chars = new StringBuffer();
	}

	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (item != null) {
			if (localName.equalsIgnoreCase("item")) {
				item = null;
			} else if (localName.equalsIgnoreCase("title")) {
				item.setTitle(chars.toString());
			} else if (localName.equalsIgnoreCase("description")) {
				item.setDescription(chars.toString());
			} else if (localName.equalsIgnoreCase("pubDate")) {
				item.setDate(chars.toString());
			} else if (localName.equalsIgnoreCase("link")) {
				item.setUrl(chars.toString());
			}
		}
	}
	
	@Override
	public RssItem getFirstItem() {
		return list.get(0);
	}

	@Override
	public String getFeedUrl() {
		//return "http://www.nasa.gov/rss/image_of_the_day.rss";
		return "https://raw.github.com/panga/CursoAndroid/master/IOTD/assets/image_of_the_day.xml";
	}

}