package com.example.helloaliens;

import org.xml.sax.helpers.DefaultHandler;

public abstract class RssHandler extends DefaultHandler {
	public abstract String getFeedUrl();
	public abstract RssItem getFirstItem();
}
