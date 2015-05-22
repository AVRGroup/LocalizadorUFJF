package com.example.localizador;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BuscaAdapter extends CursorAdapter {

	private List<String> items;

	private TextView text;

	public BuscaAdapter(Context context, Cursor cursor, List<String> items) {

		super(context, cursor, false);

		this.items = items;

	}

	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {
		try {
			text.setText(items.get(cursor.getPosition()));
		} catch (Exception e) {
			Log.i("Array out of bounds", "ERRO");
		}

	}

	@Override
	public View newView(final Context context, final Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_busca, parent, false);

		text = (TextView) view.findViewById(R.id.text);

		return view;

	}

	// OTIMIZAR DEPOIS:
	// -Using a ViewHolder, rather than calling findViewById() repeatedly.
	// -Saving the indices of your Cursor, rather than calling getColumnIndex()
	// repeatedly.
	// -Fetching the LayoutInflater once and keeping a local reference.

}
