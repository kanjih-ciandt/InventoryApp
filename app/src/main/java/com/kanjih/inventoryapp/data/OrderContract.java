package com.kanjih.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.kanjih.inventoryapp.data.ProductContract.ProductEntry.COLUMN_PROD_CODE;
import static com.kanjih.inventoryapp.data.ProductContract.ProductEntry.COLUMN_PROD_NAME;
import static com.kanjih.inventoryapp.data.ProductContract.ProductEntry.COLUMN_PROD_PRICE;
import static com.kanjih.inventoryapp.data.ProductContract.ProductEntry.COLUMN_PROD_QTDE;


/**
 * Created by kneto on 4/7/17.
 */

public class OrderContract {

    public static final String CONTENT_AUTHORITY = "com.kanjih.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ORDER = "salesorder";

    private OrderContract(){}

    public static final class OrderEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ORDER);

        public static final String TABLE_NAME = "salesorder";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ORDER_VENDOR_ID = "vendor_id";
        public static final String COLUMN_ORDER_PRODUCT_ID = "product_id";
        public static final String COLUMN_ORDER_CLIENT_NAME = "client_name";
        public static final String COLUMN_ORDER_TAG = "order_tag";
        public static final String COLUMN_ORDER_PRICE = "price";
        public static final String COLUMN_ORDER_QTDE = "qtde";
        public static final String COLUMN_ORDER_TIMESTAMP = "timestamp";

        public static final  String[] projection = {
                _ID,
                COLUMN_ORDER_VENDOR_ID,
                COLUMN_ORDER_PRODUCT_ID,
                COLUMN_ORDER_CLIENT_NAME,
                COLUMN_ORDER_TAG,
                COLUMN_ORDER_PRICE,
                COLUMN_ORDER_QTDE,
                COLUMN_ORDER_TIMESTAMP
        };


        /**
         * The MIME type of the  for a list of supplier.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;

        /**
         * The MIME type of the for a single supplier.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;

    }
}
