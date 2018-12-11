package com.example.aamer.inventoryapp;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.aamer.inventoryapp.data.InventoryContract.InventoryEntry;


public class ProductDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, Button.OnClickListener {

    public static Uri productUri;
    private TextView textViewName;
    private TextView textViewPrice;
    private TextView textViewQuantity;
    private TextView textViewSupplierName;
    private TextView textViewSupplierNumber;

    private int PRODUCT_LOADER_DETAILS = 1;

    private int quantity;
    private String name;
    private int price;
    private String supplierName;
    private int supplierNumber;
    private String supplierNumberString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_product_detail);
        super.onCreate(savedInstanceState);

        // If there was an intent used to open this Activity take the Uri from it
        Intent intent = getIntent();
        productUri = intent.getData();

        textViewName = findViewById(R.id.text_name);
        textViewPrice = findViewById(R.id.text_price);
        textViewQuantity = findViewById(R.id.text_quantity);
        textViewSupplierName = findViewById(R.id.text_supplier_name);
        textViewSupplierNumber = findViewById(R.id.text_supplier_number);

        //Find the views and set a listener on them
        Button addButton = findViewById(R.id.add_quantity_button_detail);
        addButton.setOnClickListener(this);
        Button reduceButton = findViewById(R.id.reduce_quantity_button_detail);
        reduceButton.setOnClickListener(this);
        Button dialSupplier = findViewById(R.id.call_supplier);
        dialSupplier.setOnClickListener(this);

        //Initiate the loader
        getLoaderManager().initLoader(PRODUCT_LOADER_DETAILS, null, this);

    }

    private void editProduct() {
        Intent intent = new Intent(ProductDetailActivity.this, DataReceptionMainActivity.class);
        Uri currentProductURIEdit = productUri;

        intent.setData(currentProductURIEdit);

        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the .
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the .
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
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (productUri != null) {
            // Call the ContentResolver to delete the at the given content URI.
            // Pass in null for the selection and selection args because the
            // content URI already identifies the  that we want.
            int rowsDeleted = getContentResolver().delete(productUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_item:
                editProduct();
                break;
            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the details shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(
                this,
                productUri,
                projection,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of  attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            name = cursor.getString(nameColumnIndex);
            String priceString = cursor.getString(priceColumnIndex);
            price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            String quantityString = cursor.getString(quantityColumnIndex);
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            supplierName = cursor.getString(supplierNameColumnIndex);
            supplierNumberString = cursor.getString(supplierNumberColumnIndex);
            supplierNumber = 0;
            if (!TextUtils.isEmpty(supplierNumberString)) {
                supplierNumber = Integer.parseInt(supplierNumberString);
            }

            // Update the views on the screen with the values from the database
            textViewName.setText(name);
            textViewPrice.setText(String.valueOf(price));
            textViewQuantity.setText(String.valueOf(quantity));
            textViewSupplierName.setText(supplierName);
            textViewSupplierNumber.setText(String.valueOf(supplierNumber));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        textViewName.setText("");
        textViewPrice.setText("");
        textViewQuantity.setText("");
        textViewSupplierName.setText("");
        textViewSupplierNumber.setText("");
    }

    @Override
    public void onClick(View view) {
        ContentValues values = new ContentValues();
        switch (view.getId()) {
            case R.id.add_quantity_button_detail:
                quantity++;
                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                getContentResolver().update(productUri, values, null, null);
                break;
            case R.id.reduce_quantity_button_detail:
                if (quantity > 0) {
                    quantity--;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    getContentResolver().update(productUri, values, null, null);
                } else {
                    Toast.makeText(this, getString(R.string.sold_out),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.call_supplier:
                //Intent to dial the supplier
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierNumberString));
                startActivity(intent);
                break;
        }
    }
}
