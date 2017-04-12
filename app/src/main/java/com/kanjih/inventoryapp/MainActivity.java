package com.kanjih.inventoryapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.LinearLayout;
import android.widget.ListView;

import com.kanjih.inventoryapp.data.OrderContract;
import com.kanjih.inventoryapp.data.ProductContract;
import com.kanjih.inventoryapp.data.ProductContract.ProductEntry;
import com.kanjih.inventoryapp.data.ProductOrderContract;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener,  LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PRODUCT_LOADER = 0;

    boolean isInventoryMode = true;

    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("take_picture", true);
                startActivity(intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView productsListView = (ListView) findViewById(R.id.list_view_products);

        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        productsListView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert_supplier) {
            Intent intent = new Intent(MainActivity.this, SupplierActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_delete_all){
            getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
            getContentResolver().delete(ProductOrderContract.ProductOrderEntry.CONTENT_URI, null, null);
            getContentResolver().delete(OrderContract.OrderEntry.CONTENT_URI, null, null);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_mode) {
            this.changeMode(item);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void changeMode(MenuItem item){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout navLayout = (LinearLayout) findViewById(R.id.nav_bar_header);

        if(isInventoryMode){
            item.setIcon(R.drawable.ic_check_box_outline_blank_black_24dp);
            isInventoryMode = false;
            this.setTitle(R.string.app_name_mode_seller);
            toolbar.setBackgroundResource(R.color.primary_seller);
            navLayout.setBackgroundResource(R.drawable.side_nav_bar_seller_mode);
        } else {
            item.setIcon(R.drawable.ic_check_box_black_24dp);
            this.setTitle(R.string.app_name_mode_inventory);
            toolbar.setBackgroundResource(R.color.primary);
            navLayout.setBackgroundResource(R.drawable.side_nav_bar_inventory_mode);
            isInventoryMode = true;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI,ProductEntry.projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
