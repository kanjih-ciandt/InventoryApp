package com.kanjih.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_CLIENT_NAME;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_PRICE;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_PRODUCT_ID;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_QTDE;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_TAG;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_TIMESTAMP;
import static com.kanjih.inventoryapp.data.OrderContract.OrderEntry.COLUMN_ORDER_VENDOR_ID;

/**
 * Created by kneto on 4/7/17.
 */

public class ProductOrderContract {

    public static final String CONTENT_AUTHORITY = "com.kanjih.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCT_ORDER = "productorder";

    private ProductOrderContract(){}

    public static final class ProductOrderEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT_ORDER);

        public static final String TABLE_NAME = "productorder";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PO_PROD_ID = "product_id";
        public static final String COLUMN_PO_SUPPLIER_ID = "supplier_id";
        public static final String COLUMN_PO_QTDE = "qtde";
        public static final String COLUMN_PO_PRICE = "price";
        public static final String COLUMN_PO_TIMESTAMP = "timestamp";

        public static final  String[] projection = {
                _ID,
                COLUMN_PO_PROD_ID,
                COLUMN_PO_SUPPLIER_ID,
                COLUMN_PO_QTDE,
                COLUMN_PO_PRICE,
                COLUMN_PO_TIMESTAMP
        };

        /**
         * The MIME type of the  for a list of supplier.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_ORDER;

        /**
         * The MIME type of the for a single supplier.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_ORDER;
    }

}
