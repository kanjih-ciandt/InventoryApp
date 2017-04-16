package com.kanjih.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kanjih.inventoryapp.data.ProductContract;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;
import com.kanjih.inventoryapp.data.SupplierContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.name;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int PRODUCT_LOADER = 0;

    /**
     * Content URI
     */
    private Uri mCurrentUri;

    private ImageView mImageView;

    private EditText mCode;
    private EditText mName;


    private EditText mPrice;
    private EditText mQtde;
    private int qtde=0;
    private HashMap<String, String>  mapSupplier = new HashMap<String, String>();
    private HashMap<String, Integer>  mapId = new HashMap<String, Integer>();
    private boolean mProductChange;


    private Spinner mSupplierSpinner;



    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private Bitmap mImageBitmap;

    private String mCurrentPhotoPath;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mImageView = (ImageView) findViewById(R.id.image_product);
        mCode = (EditText) findViewById(R.id.edit_prod_code);
        mName = (EditText) findViewById(R.id.edit_prod_name);
        mSupplierSpinner = (Spinner) findViewById(R.id.edit_prod_supplier);
        mPrice = (EditText) findViewById(R.id.edit_prod_price);
        mQtde = (EditText) findViewById(R.id.edit_prod_qtde);

        mImageView.setOnTouchListener(mTouchListener);
        mCode.setOnTouchListener(mTouchListener);
        mName.setOnTouchListener(mTouchListener);
        mSupplierSpinner.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQtde.setOnTouchListener(mTouchListener);



        ImageButton btnAdd = (ImageButton) findViewById(R.id.btn_qtde_add);
        ImageButton btnRemove = (ImageButton) findViewById(R.id.btn_qtde_remove);

        setupSpinner();

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qtde <= 0) {

                    Snackbar.make(view, R.string.msg_edit_qtde, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    qtde--;
                    mQtde.setText(String.valueOf(qtde));
            }
            }
        });

        ImageView img = (ImageView) findViewById(R.id.image_product);



        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtde++;
                mQtde.setText(String.valueOf(qtde));
            }
        });

        final Intent intent = getIntent();
        if(intent.getBooleanExtra("take_picture", false)){
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("take_picture", false);
                    dispatchTakePictureIntent();
                }
            });

        }

        mCurrentUri = intent.getData();

        if (mCurrentUri != null) {
            // Kick off the loader
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        } else {
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                if(mSupplierSpinner.getSelectedItem() == null ){
                    showDialogSuggestIncludeSupplier();

                }
                else if(!isValidFields()){
                    Toast.makeText(this, "Fields shoudn't be null ", Toast.LENGTH_SHORT).show();
                    return true;

                } else {
                    save();
                    finish();

                }

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;


            case R.id.action_supplier_order:
                showSupplierOrderConfirmationDialog();
                return true;


            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductChange) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSupplierOrderConfirmationDialog() {

        String supplierString = mSupplierSpinner.getSelectedItem().toString();
        String supplierID = mapSupplier.get(supplierString);
        Uri uri = ContentUris.withAppendedId(SupplierContract.SupplierEntry.CONTENT_URI,Integer.valueOf(supplierID));

        String tempPhone = null;
        String tempEmail = null;
        String tempName = null;

        Cursor cursor =  getContentResolver().query(uri, SupplierContract.SupplierEntry.projection,null, null, null);
        if(cursor.moveToFirst()) {
            tempPhone = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_SUP_PHONE));
            tempEmail = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_SUP_MAIL));
            tempName = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_SUP_NAME));
        }

        final String phone = tempPhone;
        final String email = tempEmail;
        final String message = getString(R.string.email_body, tempName, mCode.getText().toString(), mName.getText().toString());

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = getString(R.string.action_supplier_order);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ask_by_phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });
        builder.setNegativeButton(R.string.ask_by_email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/html");
                intent.setData(Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Supply Order");
                intent.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogSuggestIncludeSupplier() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = getString(R.string.action_suggest_confirmation);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.generate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ContentValues valuesSupplier = new ContentValues();
                valuesSupplier.put(SupplierContract.SupplierEntry.COLUMN_SUP_MAIL, "hkanjih@gmail.com");
                valuesSupplier.put(SupplierContract.SupplierEntry.COLUMN_SUP_NAME, "Kanji Hara Neto");
                valuesSupplier.put(SupplierContract.SupplierEntry.COLUMN_SUP_MOBILE, "+55 (31) 99753-8596");
                valuesSupplier.put(SupplierContract.SupplierEntry.COLUMN_SUP_PHONE, "+55 (31) 99753-8596");
                getContentResolver().insert(SupplierContract.SupplierEntry.CONTENT_URI,valuesSupplier);
                setupSpinner();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(LOG_TAG,"error with photo", ex );
            }

            if (photoFile != null) {

                Uri uriSavedImage=Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//            mImageView.setImageBitmap(mImageBitmap);
//        }
//    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            int nh = (int) ( mImageBitmap.getHeight() * (512.0 / mImageBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(mImageBitmap, 512, nh, true);
            mImageView.setImageBitmap(scaled);
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/ " + imageFileName + ".jpg" );


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        List<String> labels = new ArrayList<String>();

        // READ
        Cursor cursor = getContentResolver().query(SupplierContract.SupplierEntry.CONTENT_URI, SupplierContract.SupplierEntry.projection,null,null,null);
        int i = 0;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String name = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_SUP_NAME));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry._ID));
                mapSupplier.put(id + " - " + name,id);
                mapId.put(id,i);
                i++;
                labels.add(id + " - " + name);
            } while (cursor.moveToNext());
        }


        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSupplierSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isValidFields() {
        return ! (mName.getText().toString().trim().isEmpty()  ||
                mCode.getText().toString().trim().isEmpty() ||
                mPrice.getText().toString().trim().isEmpty() ||
                mQtde.getText().toString().trim().isEmpty() ||
                null == mCurrentPhotoPath ||
                (null == mSupplierSpinner.getSelectedItem()));

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductChange) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);



        String msg = getString(R.string.action_delete_confirmation, mCode.getText());
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void save() {
        boolean isOk = false;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String codeString = mCode.getText().toString().trim();
        String nameString = mName.getText().toString().trim();
        String supplierString = mSupplierSpinner.getSelectedItem().toString();
        String priceString = mPrice.getText().toString().trim();

        NumberFormat format = NumberFormat.getInstance(Locale.US);
        Number number = null;
        try {
            number = format.parse(priceString);


        } catch (ParseException e) {
            e.printStackTrace();
        }


        String qtdeString = mQtde.getText().toString().trim();
        String fileLocationString = mCurrentPhotoPath;

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues valuesProduct = new ContentValues();
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_CODE, codeString);
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_NAME, nameString);
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_QTDE, qtdeString);
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_PRICE, number.doubleValue());
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_IMG_URL, fileLocationString);
        valuesProduct.put(ProductContract.ProductEntry.COLUMN_PROD_SUPPLIER_ID, mapSupplier.get(supplierString));

        if(mCurrentUri == null) {
            Uri newRowId = getContentResolver().insert(ProductEntry.CONTENT_URI, valuesProduct);
            isOk = (newRowId != null);
        } else {
            int numUpdate = getContentResolver().update(mCurrentUri, valuesProduct, null, null);
            isOk = (numUpdate > 0);
        }

        // Show a toast message depending on whether or not the insertion was successful
        if (isOk) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, R.string.editor_save_product_successful, Toast.LENGTH_SHORT).show();

        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, R.string.editor_save_product_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {

        int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                    Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentUri,ProductEntry.projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String productString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_NAME));
            String quantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_QTDE));
            String priceString = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PROD_PRICE));
            String imageString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PROD_IMG_URL));
            String codeString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PROD_CODE));
            String supplierString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PROD_SUPPLIER_ID));

            qtde = Integer.valueOf(quantityString);

            mCurrentPhotoPath = imageString;

            mImageBitmap = BitmapFactory.decodeFile(imageString);
            int nh = (int) ( mImageBitmap.getHeight() * (512.0 / mImageBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(mImageBitmap, 512, nh, true);
            mImageView.setImageBitmap(scaled);

            //mImageView.
            mCode.setText(codeString);
            mName.setText(productString);
            mPrice.setText(priceString);
            mQtde.setText(quantityString);

            int i  = mapId.get(supplierString);
            mSupplierSpinner.setSelection(i);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.action_supplier_order);
            menuItem.setVisible(false);
        }
        return true;
    }
}
