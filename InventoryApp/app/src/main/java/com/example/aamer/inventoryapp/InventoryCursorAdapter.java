package com.example.aamer.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aamer.inventoryapp.data.InventoryContract.InventoryEntry;
public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context context1 = context;

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);


        // Find the columns of  attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);


        // Read the  attributes from the Cursor for the current
        int id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);

        saleButton.setTag(id);

        saleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) view.getTag();

                ContentValues values = new ContentValues();
                String currentQuantityString = quantityTextView.getText().toString().trim();
                int currentQuantity = Integer.parseInt(currentQuantityString);
                Uri currentProductURI = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                if (currentQuantity > 0) {
                    currentQuantity--;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity);
                    context1.getContentResolver().update(currentProductURI, values, null, null);
                } else {
                    Toast.makeText(view.getContext(), "Sold out!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update the TextViews with the attributes for the current
        nameTextView.setText(productName);
        priceTextView.setText(Integer.toString(productPrice));
        quantityTextView.setText(Integer.toString(productQuantity));
    }
}
