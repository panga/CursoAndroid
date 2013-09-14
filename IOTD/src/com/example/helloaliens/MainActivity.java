package com.example.helloaliens;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends RssActivity {

	private RssItem item;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		refreshFeed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void displayData(RssItem item) {
		TextView title = (TextView) findViewById(R.id.title);
		TextView date = (TextView) findViewById(R.id.date);
		ImageView image = (ImageView) findViewById(R.id.image);
		TextView description = (TextView) findViewById(R.id.description);

		if (item != null) {
			title.setText(item.getTitle());
			DateFormat pubDateFormatter = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			DateFormat localeFormatter = new SimpleDateFormat(getText(
					R.string.date_format).toString(), Locale.getDefault());
			try {
				Date pubDate = pubDateFormatter.parse(item.getDate());
				date.setText(localeFormatter.format(pubDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			image.setImageBitmap(item.getImage());
			description.setText(Html.fromHtml(item.getDescription()));
		} else {
			title.setText(null);
			date.setText(null);
			image.setImageBitmap(null);
			description.setText(null);
		}
		
		this.item = item;
	}
	
	public void refreshFeed() {
		displayData(null);
		RssService service = new RssService(this);
		service.execute(new NasaRssHandler());
	}
	
	public void shareFeed() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, getText(R.string.app_name) + ": "
				+ item.getTitle() + " " + item.getUrl());
		startActivity(Intent.createChooser(intent,
				getString(R.string.action_share)));
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh:
				refreshFeed();
				return true;
			case R.id.action_share:
				shareFeed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void onTitleClick(View view) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl())));
	}
}
