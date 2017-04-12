package com.kanjih.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kanjih.inventoryapp.data.SupplierContract.SupplierEntry;

public class SupplierActivity extends AppCompatActivity {

    /**
     * Content URI
     */
    private Uri mCurrentUri;

    private boolean mSupplierHasChanged = false;

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mMobileEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        mNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);
        mPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
        mMobileEditText = (EditText) findViewById(R.id.edit_supplier_mobile);

        mNameEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mMobileEditText.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSupplierHasChanged = true;
            return false;
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // Do nothing for now
                if(!isValidFields()){
                    Toast.makeText(this, "Fields shoudn't be null ", Toast.LENGTH_SHORT).show();
                    return true;

                } else {
                    saveSupplier();
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
                if (!mSupplierHasChanged) {
                    NavUtils.navigateUpFromSameTask(SupplierActivity.this);
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
                                NavUtils.navigateUpFromSameTask(SupplierActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidFields() {
        return ! (mNameEditText.getText().toString().trim().isEmpty()  ||
                mEmailEditText.getText().toString().trim().isEmpty() );

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
        if (!mSupplierHasChanged) {
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
    private void saveSupplier() {


        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String mobileString = mMobileEditText.getText().toString().trim();



        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUP_NAME, nameString);
        values.put(SupplierEntry.COLUMN_SUP_MAIL, emailString);
        values.put(SupplierEntry.COLUMN_SUP_PHONE, phoneString);
        values.put(SupplierEntry.COLUMN_SUP_MOBILE, mobileString);

        boolean isOk;
        if (mCurrentUri != null) {

            int id = getContentResolver().update(mCurrentUri, values, null, null);

            isOk = (id != -1);
        } else {
            Uri newRowId = getContentResolver().insert(SupplierEntry.CONTENT_URI, values);
            isOk = (newRowId != null);
        }

        // Insert a new row for pet in the database, returning the ID of that new row.


        // Show a toast message depending on whether or not the insertion was successful
        if (isOk) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, R.string.editor_save_supplier_successful, Toast.LENGTH_SHORT).show();

        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, R.string.editor_save_supplier_failed, Toast.LENGTH_SHORT).show();
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
