package com.kanjih.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry.COLUMN_SUP_MAIL;
import static com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry.COLUMN_SUP_MOBILE;
import static com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry.COLUMN_SUP_NAME;
import static com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry.COLUMN_SUP_PHONE;

/**
 * Created by kneto on 4/7/17.
 */

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.kanjih.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCT = "product";

    private ProductContract(){}

    public static final class ProductEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String TABLE_NAME = "product";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PROD_CODE = "code";
        public static final String COLUMN_PROD_NAME = "name";
        public static final String COLUMN_PROD_PRICE = "price";
        public static final String COLUMN_PROD_QTDE = "qtde";
        public static final String COLUMN_PROD_IMG_URL = "imgurl";

        public static final  String[] projection = {
                _ID,
                COLUMN_PROD_CODE,
                COLUMN_PROD_NAME,
                COLUMN_PROD_PRICE,
                COLUMN_PROD_QTDE
        };


        /**
         * The MIME type of the  for a list of supplier.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * The MIME type of the for a single supplier.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
    }

}
