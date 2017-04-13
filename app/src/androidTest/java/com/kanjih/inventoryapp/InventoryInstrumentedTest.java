package com.kanjih.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.kanjih.inventoryapp.data.InventoryProvider;
import com.kanjih.inventoryapp.data.OrderContract.OrderEntry;
import com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry;
import com.kanjih.inventoryapp.data.ProductOrderContract.ProductOrderEntry;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;

import org.junit.Assert;
import org.junit.Test;

import static android.R.attr.id;
import static com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry.projection;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class InventoryInstrumentedTest extends ProviderTestCase2<InventoryProvider> {

    ContentValues valuesSupplier;
    ContentValues valuesProduct;
    ContentValues valuesProductOrder;
    ContentValues valuesOrder;


    /**
     * Constructor.
     *
     */
    public InventoryInstrumentedTest() {
        super(InventoryProvider.class, InventoryProvider.class.getName());
    }

    /**
     * Set up and setting values
     * @throws java.lang.Exception
     */
    protected void setUp() throws java.lang.Exception {
        super.setUp();

        //Product
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PROD_CODE, "COD01");
        values.put(ProductEntry.COLUMN_PROD_NAME, "Product 1");
        values.put(ProductEntry.COLUMN_PROD_PRICE, 145023);
        values.put(ProductEntry.COLUMN_PROD_QTDE, 1000);

        ContentProvider provider = getProvider();
        Uri uri = this.testCreate(provider, ProductEntry.CONTENT_URI, values);
        String prodCode =  String.valueOf(ContentUris.parseId(uri));

        //Supplier
        valuesSupplier = new ContentValues();
        valuesSupplier.put(SupplierEntry.COLUMN_SUP_MAIL, "hkanjih@gmail.com");
        valuesSupplier.put(SupplierEntry.COLUMN_SUP_NAME, "Kanji Hara Neto");
        valuesSupplier.put(SupplierEntry.COLUMN_SUP_MOBILE, "+55 (31) 99753-8596");
        valuesSupplier.put(SupplierEntry.COLUMN_SUP_PHONE, "+55 (31) 99753-8596");

        //Product
        valuesProduct = new ContentValues();
        valuesProduct.put(ProductEntry.COLUMN_PROD_CODE, "COD02");
        valuesProduct.put(ProductEntry.COLUMN_PROD_NAME, "Product 2");
        valuesProduct.put(ProductEntry.COLUMN_PROD_PRICE, 145023);
        valuesProduct.put(ProductEntry.COLUMN_PROD_QTDE, 1000);


        //Product Order
        valuesProductOrder = new ContentValues();
        valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_PRICE,145023);
        valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_QTDE, 1000);
        valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_PROD_ID,Integer.valueOf(prodCode));

        //Order
        valuesOrder = new ContentValues();
        valuesOrder.put(OrderEntry.COLUMN_ORDER_CLIENT_NAME, "Isabela Nogueira Coelho");
        valuesOrder.put(OrderEntry.COLUMN_ORDER_PRICE, 145023);
        valuesOrder.put(OrderEntry.COLUMN_ORDER_PRODUCT_ID, Integer.valueOf(prodCode));
        valuesOrder.put(OrderEntry.COLUMN_ORDER_QTDE, 123);
        valuesOrder.put(OrderEntry.COLUMN_ORDER_TAG, "XPTO");
        valuesOrder.put(OrderEntry.COLUMN_ORDER_VENDOR_ID, "kanjih@ciandt.com");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();


    }




    @Test
    public void testCRUDSupplier() throws Exception {
        ContentProvider provider = getProvider();
        this.testCreate(provider, SupplierEntry.CONTENT_URI, valuesSupplier);
        this.testReadSupplier(provider);
        this.testUpdateSupplier(provider);
        this.testDeleteSupplier(provider);

    }

    @Test
    public void testCRUDProduct() throws Exception {
        ContentProvider provider = getProvider();

        Uri uri =  this.testCreate(provider, ProductEntry.CONTENT_URI, valuesProduct);
        String prodCode =  String.valueOf(ContentUris.parseId(uri));
        this.testRead(provider,ProductEntry.CONTENT_URI, ProductEntry.projection);
        valuesProduct.put(ProductEntry.COLUMN_PROD_PRICE, 85023);
        Uri currentUriPetUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,Integer.valueOf(prodCode));
        this.testUpdate(provider,currentUriPetUri,valuesProduct);
        this.testDelete(provider, currentUriPetUri);


    }

    @Test
    public void testCRUDProductOrder() throws Exception {
        ContentProvider provider = getProvider();

        Uri uri =   this.testCreate(provider, SupplierEntry.CONTENT_URI, valuesSupplier);

        String prodCode =  String.valueOf(ContentUris.parseId(uri));
        valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_SUPPLIER_ID, Integer.valueOf(prodCode));
        uri = this.testCreate(provider, ProductOrderEntry.CONTENT_URI, valuesProductOrder);

        this.testRead(provider,ProductOrderEntry.CONTENT_URI, ProductOrderEntry.projection);

        prodCode =  String.valueOf(ContentUris.parseId(uri));
        Uri currentUriPetUri = ContentUris.withAppendedId(ProductOrderEntry.CONTENT_URI,Integer.valueOf(prodCode));
        valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_PRICE,345023.23d);
        this.testUpdate(provider,currentUriPetUri,valuesProductOrder);
        this.testDelete(provider, currentUriPetUri);


    }

    @Test
    public void testCRUDOrder() throws Exception {
        ContentProvider provider = getProvider();
        Uri uri =    this.testCreate(provider, OrderEntry.CONTENT_URI, valuesOrder);
        this.testRead(provider,OrderEntry.CONTENT_URI, OrderEntry.projection);

        String prodCode =  String.valueOf(ContentUris.parseId(uri));
        Uri currentUriPetUri = ContentUris.withAppendedId(OrderEntry.CONTENT_URI,Integer.valueOf(prodCode));
        valuesOrder.put(OrderEntry.COLUMN_ORDER_CLIENT_NAME, "Livia Nogueira Coelho Hara");
        this.testUpdate(provider,currentUriPetUri,valuesOrder);
        this.testDelete(provider, currentUriPetUri);

    }

    //test include product

    //test receive product from supplier


    //test create a order

    //test track product from supplier

    //test include product in the card

    //test finished saller


    private Uri testCreate(ContentProvider provider, Uri uriTable, ContentValues value) {
        Uri uri = provider.insert(uriTable, value);
        Assert.assertNotNull( uri );
        return uri;
    }

    private void testReadSupplier(ContentProvider provider) {
        // READ
        Cursor cursor = provider.query(SupplierEntry.CONTENT_URI,SupplierEntry.projection,null,null,null);
        //check that you have date
        Assert.assertNotNull( cursor);

        if(cursor.moveToFirst()) {

            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(SupplierEntry._ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_SUP_MAIL));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_SUP_MOBILE));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(SupplierEntry.COLUMN_SUP_PHONE));

            Assert.assertEquals(email, "hkanjih@gmail.com");
            Assert.assertEquals(mobile, "+55 (31) 99753-8596");
            Assert.assertEquals(phone, "+55 (31) 99753-8596");
        } else {
            Assert.fail();
        }
    }

    private Cursor testRead(ContentProvider provider, Uri uriTable, String[] projection) {
        Cursor cursor = provider.query(uriTable,projection,null,null,null);
        Assert.assertNotNull( cursor);
        if(!cursor.moveToFirst()){
            Assert.fail();
        }

        return cursor;

    }

    private void testDeleteSupplier(ContentProvider provider) {
        //DELETE
        int rowsDeleted = provider.delete(SupplierEntry.CONTENT_URI, null, null);
        assertTrue(rowsDeleted > 0);
    }

    private void testUpdateSupplier(ContentProvider provider) {
        //UPDATE
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUP_MAIL, "kanjinho@gmail.com");
        values.put(SupplierEntry.COLUMN_SUP_NAME, "Kanji Hara Neto");
        values.put(SupplierEntry.COLUMN_SUP_MOBILE, "+55 (31) 99753-8596");
        values.put(SupplierEntry.COLUMN_SUP_PHONE, "+55 (31) 99753-8596");

        int isOk = provider.update(SupplierEntry.CONTENT_URI, values, null, null);

        assertTrue(isOk != -1);
    }

    private int testUpdate(ContentProvider provider, Uri uriTable, ContentValues value) {
        int numberUpdate = provider.update(uriTable, value, null, null);
        Assert.assertTrue( numberUpdate  > 0 );
        return numberUpdate;
    }

    private int testDelete(ContentProvider provider, Uri uriTable) {
        int numberUpdate = provider.delete(uriTable, null, null);
        Assert.assertTrue( numberUpdate  > 0 );
        return numberUpdate;
    }

}
