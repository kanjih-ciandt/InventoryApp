package com.kanjih.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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

import com.kanjih.inventoryapp.data.InventoryDBHelper;
import com.kanjih.inventoryapp.data.ProductContract;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;
import com.kanjih.inventoryapp.data.ProductOrderContract;
import com.kanjih.inventoryapp.data.ProductOrderContract.ProductOrderEntry;
import com.kanjih.inventoryapp.data.SupplierContract;
import com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.data;

public class DetailActivity extends AppCompatActivity {
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtde++;
                mQtde.setText(String.valueOf(qtde));
            }
        });

        Intent intent = getIntent();
        if(intent.getBooleanExtra("take_picture", false)){
            intent.putExtra("take_picture", false);
            dispatchTakePictureIntent();

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

                // Do nothing for now
                if(!isValidFields()){
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
                finish();
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
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");

            mImageView.setImageBitmap(mImageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

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

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry.COLUMN_SUP_NAME));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(SupplierContract.SupplierEntry._ID));
                mapSupplier.put(id + " - " + name,id);
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
                mCode.getText().toString().trim().isEmpty() );

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
        builder.setMessage(R.string.delete_dialog_msg_supplier);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteSupplier();
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

        Uri newRowId = getContentResolver().insert(ProductEntry.CONTENT_URI, valuesProduct);

        if(newRowId != null) {
            String prodCode =  String.valueOf(ContentUris.parseId(newRowId));

            ContentValues valuesProductOrder = new ContentValues();
            valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_SUPPLIER_ID, mapSupplier.get(supplierString));
            valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_PRICE,number.doubleValue());
            valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_QTDE, qtdeString);
            valuesProductOrder.put(ProductOrderEntry.COLUMN_PO_PROD_ID, prodCode);

            newRowId = getContentResolver().insert(ProductOrderEntry.CONTENT_URI, valuesProductOrder);

            isOk = (newRowId != null);

        } else {
            isOk = false;
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
    private void deleteSupplier() {

        int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_supplier_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_supplier_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }


}
