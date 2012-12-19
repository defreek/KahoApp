package be.kahosl.whatsrecent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import be.kahosl.R;
import be.kahosl.agenda.Agenda;
import be.kahosl.whatsrecent.data.WhatsRecentDatabase;
import be.kahosl.whatsrecent.data.WhatsRecentProvider;

public class WhatsRecentCursorAdapter extends CursorAdapter {
	private Context context;
	LayoutInflater mInflater;

	private final HashMap<Integer, Boolean> checkedItems = new HashMap<Integer, Boolean>();

	public WhatsRecentCursorAdapter(Context context, Cursor c) {
		// that constructor should be used with loaders.
		super(context, c, 0);

		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView title_view = (TextView) view.findViewById(R.id.title);
		title_view.setText(cursor.getString(cursor
				.getColumnIndex(WhatsRecentDatabase.COL_TITLE)));

		TextView author_view = (TextView) view.findViewById(R.id.author);
		author_view.setText(cursor.getString(cursor
				.getColumnIndex(WhatsRecentDatabase.COL_AUTHOR)));

		ImageView image_view = (ImageView) view.findViewById(R.id.imageView);

		String type = cursor.getString(cursor
				.getColumnIndex(WhatsRecentDatabase.COL_TYPE));

		if (type.equals("announcement")) {
			image_view.setImageResource(R.drawable.announcement);
		} else if (type.equals("content")) {
			image_view.setImageResource(R.drawable.content);
		} else if (type.equals("assignment")) {
			image_view.setImageResource(R.drawable.assignment);
		}
		
		DateTimeFormatter parser = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		DateTime dt = parser.parseDateTime(cursor.getString(cursor
				.getColumnIndex(WhatsRecentDatabase.COL_DATE)));
		TextView date_view = (TextView) view.findViewById(R.id.date);
		date_view.setText(formatDatum(dt));

		final int id = cursor.getInt(cursor
				.getColumnIndex(WhatsRecentDatabase.ID));
		final CheckBox cBox = (CheckBox) view.findViewById(R.id.checkBox);
		// CheckBox
		cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				checkedItems.put(id, isChecked ? true : false);
			}
		});
		cBox.setChecked(checkedItems.get(id));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.list_item, parent, false);
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		newCursor.moveToFirst();
		while (newCursor.isAfterLast() == false) {
			int id = newCursor.getInt(newCursor
					.getColumnIndex(WhatsRecentDatabase.ID));
			if (!checkedItems.containsKey(id))
				checkedItems.put(id, false);
			newCursor.moveToNext();
		}
		return super.swapCursor(newCursor);
	}

	public void hideCheckedItems() {
		ContentValues editedValues = new ContentValues();
		editedValues.put(WhatsRecentDatabase.COL_VISIBLE, 0);

		int aantal = 0;

		Iterator it = checkedItems.entrySet().iterator();
		while (it.hasNext()) {
			Entry pairs = (Entry) it.next();

			if ((Boolean) pairs.getValue()) {
				context.getContentResolver().update(
						Uri.withAppendedPath(WhatsRecentProvider.CONTENT_URI,
								(Integer) pairs.getKey() + ""), editedValues,
						null, null);
				aantal++;
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
 
		if (aantal > 0)
			Toast.makeText(context, aantal + " verborgen", Toast.LENGTH_SHORT)
					.show();
	}

	public void showAllItems() {
		ContentValues editedValues = new ContentValues();
		editedValues.put(WhatsRecentDatabase.COL_VISIBLE, 1);

		context.getContentResolver().update(WhatsRecentProvider.CONTENT_URI,
				editedValues, null, null);
	}
	
	public void addCheckedToCalendar(Activity activity) {
		Iterator it = checkedItems.entrySet().iterator();
		while (it.hasNext()) {
			Entry pairs = (Entry) it.next();

			if ((Boolean) pairs.getValue()) {
				String title = getTitle((Integer)pairs.getKey());
				Agenda.insertEvent(activity, title, "", "", getDateFromString(title), null, false);
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}
	
	private Calendar getDateFromString(String string) {
		Pattern p = Pattern.compile("[0-9]+/[0-9]+|/[0-9]+");
		Matcher m = p.matcher(string);

		if (m.find()) {
			String[] dt = m.group().split("/");
			
			Calendar c = Calendar.getInstance();
			
			int dag = Integer.parseInt(dt[0]);
			int maand = Integer.parseInt(dt[1]) - 1;
			int jaar = dt.length < 3 ? c.get(Calendar.YEAR) : Integer.parseInt(dt[2]);
			
			c.set(jaar, maand, dag);

			return c;
		} else {
			return null;
		}
	}
	
	private String getTitle(int id) {
		Cursor c = this.getCursor();
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			if (c.getInt(c.getColumnIndex(WhatsRecentDatabase.ID)) == id) {
				return c.getString(c.getColumnIndex(WhatsRecentDatabase.COL_TITLE));
			}
			c.moveToNext();
		}
		return "";
	}

	private String formatDatum(DateTime dt) {
		DateTime now = new DateTime();
		Period period = new Period(dt, now);

		PeriodFormatter formatter;

		if (period.getMonths() != 0) {
			formatter = new PeriodFormatterBuilder().appendMonths()
					.appendSuffix(" maand geleden").printZeroNever()
					.toFormatter();
		} else if (period.getWeeks() != 0) {
			formatter = new PeriodFormatterBuilder().appendWeeks()
					.appendSuffix(period.getWeeks() > 1 ? " weken geleden" : " week geleden").printZeroNever()
					.toFormatter();
		} else if (period.getDays() != 0) {
			formatter = new PeriodFormatterBuilder().appendDays()
					.appendSuffix(period.getDays() > 1 ? " dagen geleden" : " dag geleden").printZeroNever()
					.toFormatter();
		} else if (period.getHours() != 0) {
			formatter = new PeriodFormatterBuilder().appendHours()
					.appendSuffix(" uur geleden").printZeroNever()
					.toFormatter();
		} else if (period.getMinutes() != 0) {
			formatter = new PeriodFormatterBuilder().appendMinutes()
					.appendSuffix(period.getMinutes() > 1 ? " minuten geleden" : " minuut geleden").printZeroNever()
					.toFormatter();
		} else {
			formatter = new PeriodFormatterBuilder().appendSeconds()
					.appendSuffix(" seconden geleden").printZeroNever()
					.toFormatter();
		}

		return formatter.print(period);
	}
}

