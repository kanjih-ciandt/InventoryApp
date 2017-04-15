package com.kanjih.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kanjih.inventoryapp.data.OrderContract;
import com.kanjih.inventoryapp.data.ProductContract;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by kneto on 4/9/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productTextView = (TextView) view.findViewById(R.id.list_product);
        TextView quantityTextView = (TextView) view.findViewById(R.id.list_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_price);
        ImageButton sellItem = (ImageButton) view.findViewById(R.id.btn_list_item_add);


        String productString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_NAME));
        String quantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_QTDE));
        String priceString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_PRICE));
        final int idString = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry._ID));

        NumberFormat nf = NumberFormat.getInstance(Locale.US);



        productTextView.setText(productString);
        quantityTextView.setText(quantityString + " " + context.getString(R.string.quantity));
        try {
            priceTextView.setText(context.getString(R.string.currency)  + " " +  nf.parse(priceString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sellItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "clicked item -> " + idString);
                saveItem(v, context, idString);

            }
        });
    }

    private void saveItem(View view, Context context, int id){
        //Update de number of itens
        Uri currentUriPetUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,Integer.valueOf(id));
        Cursor cursor = context.getContentResolver().query(currentUriPetUri, ProductContract.ProductEntry.projection, null, null, null);
        if(cursor.moveToFirst()) {
            Integer qtde = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_QTDE));
            Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_PRICE));
            if(qtde.intValue() > 0 ){

                qtde--;
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PROD_QTDE, qtde);
                context.getContentResolver().update(currentUriPetUri, values, null, null);

                //Product Order
                values = new ContentValues();
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_PRICE, price);
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_QTDE, 1);
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_PRODUCT_ID, Integer.valueOf(id));
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_VENDOR_ID, "XPTO1");
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_CLIENT_NAME, "XPTO1");
                values.put(OrderContract.OrderEntry.COLUMN_ORDER_TAG, "XPTO1");


                context.getContentResolver().insert(OrderContract.OrderEntry.CONTENT_URI, values);

            } else {
                Snackbar.make(view, R.string.msg_sold_qtde, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }



    }
}
