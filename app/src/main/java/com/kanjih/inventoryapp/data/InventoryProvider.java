package com.kanjih.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry;
import com.kanjih.inventoryapp.data.OrderContract.OrderEntry;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by kneto on 4/9/17.
 */

public class InventoryProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int SUPPLIER = 100;
    private static final int SUPPLIER_ID = 101;
    private static final int PRODUCT = 102;
    private static final int PRODUCT_ID = 103;
    private static final int PRODUCT_ORDER = 104;
    private static final int PRODUCT_ORDER_ID = 105;
    private static final int ORDER = 106;
    private static final int ORDER_ID = 107;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private InventoryDBHelper mDBHelper;

    /**
     * The MIME type of a  subdirectory of a single
     * person.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";

    /**
     * The MIME type of  providing a directory of
     * people.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";


    static {
        //Supplier
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY, SupplierContract.PATH_SUPPLIER, SUPPLIER);
        sUriMatcher.addURI(SupplierContract.CONTENT_AUTHORITY, SupplierContract.PATH_SUPPLIER + "/#", SUPPLIER_ID);
        //Product
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        //Order
        sUriMatcher.addURI(OrderContract.CONTENT_AUTHORITY, OrderContract.PATH_ORDER, ORDER);
        sUriMatcher.addURI(OrderContract.CONTENT_AUTHORITY, OrderContract.PATH_ORDER + "/#", ORDER_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new InventoryDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUPPLIER:
                return SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_LIST_TYPE;
            case ORDER:
                return OrderEntry.CONTENT_LIST_TYPE;
            case ORDER_ID:
                return OrderEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match){
            case SUPPLIER:
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case  SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case  PRODUCT:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case  PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case  ORDER:
                cursor = database.query(OrderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case  ORDER_ID:
                selection = OrderEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(OrderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalStateException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id = -1;

        switch (match){
            case SUPPLIER:
                return insertSupplier(uri, values, database);
            case PRODUCT:
                return insertProduct(uri, values, database);
            case  ORDER:
                return insertOrder(uri, values, database);
            default:
                throw new IllegalStateException("Cannot query unknown URI" + uri);
        }
    }

    @Nullable
    private Uri insertOrder(Uri uri, ContentValues values, SQLiteDatabase database) {
        long id;
        id =  database.insert(OrderEntry.TABLE_NAME,null, values);

        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Nullable
    private Uri insertProduct(Uri uri, ContentValues values, SQLiteDatabase database) {
        long id;
        id =  database.insert(ProductEntry.TABLE_NAME,null, values);

        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Nullable
    private Uri insertSupplier(Uri uri, ContentValues values, SQLiteDatabase database) {
        long id;
        id =  database.insert(SupplierEntry.TABLE_NAME,null, values);

        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Nullable
    private Uri getUri(Uri uri, ContentValues values, SQLiteDatabase database) {
        long id;
        id =  database.insert(SupplierEntry.TABLE_NAME,null, values);

        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;


        switch (match){
            case SUPPLIER:
                return deleteSupplier(uri, selection, selectionArgs, database);
            case  SUPPLIER_ID:
                // Delete a single row given by the ID in the URI
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteSupplier(uri, selection, selectionArgs, database);
            case  PRODUCT:
                return deleteProduct(uri, selection, selectionArgs, database);
            case  PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs, database);
            case  ORDER:
                return deleteOrder(uri, selection, selectionArgs, database);
            case  ORDER_ID:
                selection = OrderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteOrder(uri, selection, selectionArgs, database);
            default:
                throw new IllegalStateException("Cannot query unknown URI" + uri);
        }
    }

    private int deleteOrder(Uri uri, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsDeleted;
        rowsDeleted =  database.delete(OrderEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    private int deleteProduct(Uri uri, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsDeleted;
        rowsDeleted =  database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int deleteSupplier(Uri uri, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsDeleted;
        rowsDeleted =  database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();  Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match){
            case SUPPLIER:
                return updateSupplier(uri, values, selection, selectionArgs, database);
            case  SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateSupplier(uri, values, selection, selectionArgs, database);
            case  PRODUCT:
                return updateProduct(uri, values, selection, selectionArgs, database);
            case  PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs, database);
            case  ORDER:
                return updateOrder(uri, values, selection, selectionArgs, database);
            case  ORDER_ID:
                selection = OrderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateOrder(uri, values, selection, selectionArgs, database);
            default:
                throw new IllegalStateException("Cannot query unknown URI" + uri);
        }
    }

    private int updateOrder(Uri uri, ContentValues values, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsUpdated;
        rowsUpdated = database.update(OrderEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsUpdated;
        rowsUpdated = database.update(ProductEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateSupplier(Uri uri, ContentValues values, String selection, String[] selectionArgs, SQLiteDatabase database) {
        int rowsUpdated;
        rowsUpdated = database.update(SupplierEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
