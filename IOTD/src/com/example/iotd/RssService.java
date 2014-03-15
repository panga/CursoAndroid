package com.example.iotd;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class RssService extends AsyncTask<RssHandler, Void, RssItem> {

	private ProgressDialog progress;
	private RssActivity activity;

	public RssService(RssActivity activity) {
		this.activity = activity;
	}

	@Override
	protected RssItem doInBackground(RssHandler... handlers) {
		RssHandler handler = handlers[0];
		RssItem item = null;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(handler);

			URL url = new URL(handler.getFeedUrl());
			xr.parse(new InputSource(url.openConnection().getInputStream()));
			//xr.parse(new InputSource(activity.getAssets().open("image_of_the_day.xml")));

			item = handler.getFirstItem();
			if (item.getImageUrl() != null) {
				item.setImage(getBitmap(item.getImageUrl()));
				//item.setImage(BitmapFactory.decodeResource(activity.getResources(),R.drawable.image_of_the_day));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	@SuppressLint("InlinedApi")
	protected void onPreExecute() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			progress = new ProgressDialog(activity,
					ProgressDialog.THEME_HOLO_DARK);
		} else {
			progress = new ProgressDialog(activity);
		}
		progress.setIndeterminate(true);
		progress.setMessage(activity.getText(R.string.loading));
		progress.show();
	}

	private Bitmap getBitmap(String imageUrl) {
		Bitmap bitmap = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl)
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(final RssItem result) {
		activity.displayData(result);
		progress.dismiss();
	}

}
