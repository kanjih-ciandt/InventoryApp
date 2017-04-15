package com.kanjih.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry;
import com.kanjih.inventoryapp.data.OrderContract.OrderEntry;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by kneto on 4/8/17.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";


    private String createSupplier = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
            + SupplierEntry._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SupplierEntry.COLUMN_SUP_NAME + " TEXT NOT NULL, "
            + SupplierEntry.COLUMN_SUP_MAIL + " TEXT , "
            + SupplierEntry.COLUMN_SUP_PHONE + " TEXT , "
            + SupplierEntry.COLUMN_SUP_MOBILE + " TEXT ); ";


    private String createProduct =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
            + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_PROD_CODE + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PROD_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PROD_PRICE + " REAL , "
            + ProductEntry.COLUMN_PROD_IMG_URL + " TEXT , "
            + ProductEntry.COLUMN_PROD_SUPPLIER_ID + " INTEGER , "
            + ProductEntry.COLUMN_PROD_QTDE + " INTEGER NOT NULL,"
            + " FOREIGN KEY(" + ProductEntry.COLUMN_PROD_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + " (id) "
            + "); ";


    private String createOrder = "CREATE TABLE " + OrderEntry.TABLE_NAME + " ("
            + OrderEntry._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
            + OrderEntry.COLUMN_ORDER_PRODUCT_ID + " INTEGER NOT NULL, "
            + OrderEntry.COLUMN_ORDER_VENDOR_ID + " TEXT NOT NULL, "
            + OrderEntry.COLUMN_ORDER_CLIENT_NAME + " TEXT NOT NULL, "
            + OrderEntry.COLUMN_ORDER_TAG + " TEXT NOT NULL, "
            + OrderEntry.COLUMN_ORDER_QTDE + " INTEGER NOT NULL, "
            + OrderEntry.COLUMN_ORDER_PRICE + " INTEGER NOT NULL,"
            + OrderEntry.COLUMN_ORDER_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY(" + OrderEntry.COLUMN_ORDER_PRODUCT_ID + ") REFERENCES " + ProductEntry.TABLE_NAME + " (id) "
            + "); ";
    /**
     *  Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSupplier);
        Log.i(LOG_TAG, "Creating table " + createSupplier);
        db.execSQL(createProduct);
        Log.i(LOG_TAG, "Creating table " + createProduct);
        db.execSQL(createOrder);
        Log.i(LOG_TAG, "Creating table " + createOrder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
