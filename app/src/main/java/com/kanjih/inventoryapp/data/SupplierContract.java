package com.kanjih.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kneto on 4/7/17.
 */

public class SupplierContract {

    public static final String CONTENT_AUTHORITY = "com.kanjih.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUPPLIER = "supplier";

    private SupplierContract(){}

    public static final class SupplierEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIER);

        public static final String TABLE_NAME = "supplier";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SUP_NAME = "name";
        public static final String COLUMN_SUP_MAIL = "mail";
        public static final String COLUMN_SUP_PHONE = "phone";
        public static final String COLUMN_SUP_MOBILE = "mobile";

        public static final  String[] projection = {
                _ID,
                COLUMN_SUP_MAIL,
                COLUMN_SUP_MOBILE,
                COLUMN_SUP_NAME,
                COLUMN_SUP_PHONE
        };

        /**
         * The MIME type of the  for a list of supplier.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;

        /**
         * The MIME type of the for a single supplier.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIER;


    }

}
