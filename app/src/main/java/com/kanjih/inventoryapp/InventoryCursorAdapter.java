package com.kanjih.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kanjih.inventoryapp.data.ProductContract;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static java.lang.Double.parseDouble;

/**
 * Created by kneto on 4/9/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productTextView = (TextView) view.findViewById(R.id.list_product);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_price);

        String productString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_NAME));
        String quantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_QTDE));
        String priceString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_PRICE));

        NumberFormat nf = NumberFormat.getInstance(Locale.US);



        productTextView.setText(productString);
        quantityTextView.setText(quantityString + " " + context.getString(R.string.quantity));
        try {
            priceTextView.setText(context.getString(R.string.currency)  + " " +  nf.parse(priceString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
