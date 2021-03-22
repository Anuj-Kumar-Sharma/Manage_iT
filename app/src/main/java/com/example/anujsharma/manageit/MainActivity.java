package com.example.anujsharma.manageit;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,
        MyViewAdapter.MyViewHolder.ClickListener {

    public static final int CATEGORY_LOADER = 1;
    MyViewAdapter myViewAdapter;
    MyDataProvider myDataProvider;
    RecyclerView categoryRecyclerView;
    FloatingActionButton fab;
    ActionMode actionMode;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ColorGenerator colorGenerator=ColorGenerator.MATERIAL;
        int color=colorGenerator.getRandomColor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color-10);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCategory();
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(color));

        categoryRecyclerView = findViewById(R.id.recyclerView);
        myViewAdapter = new MyViewAdapter(this, this);
        myDataProvider = new MyDataProvider(this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.setAdapter(myViewAdapter);
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

    }

    private void createNewCategory() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final MaterialAutoCompleteTextView input = new MaterialAutoCompleteTextView(MainActivity.this);
        input.setHint("Category");
        input.setFloatingLabel(MaterialAutoCompleteTextView.FLOATING_LABEL_HIGHLIGHT);
        input.setFloatingLabelTextSize(50);
        input.setFloatingLabelPadding(50);
        input.setIconLeft(R.drawable.ic_add_circle_outline_black_24dp);
        input.setMaxCharacters(45);
        input.setShowClearButton(true);
        input.setPaddings(5, 10, 50, 50);
        input.setFloatingLabelText("Category");
        builder.setView(input);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().startsWith(" ")) {
                    Toast.makeText(MainActivity.this, "Category name cannot start with space.", Toast.LENGTH_SHORT).show();
                } else if (input.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Category name cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (input.getText().length() > 45) {
                    Toast.makeText(MainActivity.this, "Category name cannot exceed 45 characters.", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MyDatabaseHelper.CATEGORY_NAME, input.getText().toString());
                    contentValues.put(MyDatabaseHelper.TAG_COUNT, 0);
                    contentValues.put(MyDatabaseHelper.IMAGE_COUNT, 0);
                    Uri uri = myDataProvider.insert(MyDataProvider.CATEGORY_CONTENT_URI, contentValues);
                    if (uri == null) {
                        Toast.makeText(MainActivity.this, "Category " + input + " already exists.", Toast.LENGTH_SHORT).show();
                        input.setText("");
                    } else {
                        restartLoader();
                    }
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void restartLoader() {
        getSupportLoaderManager().restartLoader(CATEGORY_LOADER, null, MainActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchMenu = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.about) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == CATEGORY_LOADER) {
            cursorLoader = new CursorLoader(this, MyDataProvider.CATEGORY_CONTENT_URI, MyDatabaseHelper.ALL_CATEGORY_TABLE_COLUMNS,
                    null, null, MyDatabaseHelper.CATEGORY_NAME + " COLLATE NOCASE ASC");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case CATEGORY_LOADER:
                myViewAdapter.changeCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id) {
            case CATEGORY_LOADER:
                myViewAdapter.changeCursor(null);
                break;

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public void onItemClicked(int position, String category, int imageCount) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent intent = new Intent(MainActivity.this, ShowImagesActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("category", category);
            intent.putExtra("imageCount", imageCount);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }


    private void toggleSelection(int position) {
        myViewAdapter.toggleSelection(position);
        int count = myViewAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteCategory:
                    myViewAdapter.removeItems(myViewAdapter.getSelectedItems());
                    getSupportLoaderManager().restartLoader(CATEGORY_LOADER, null, MainActivity.this);
                    mode.finish();
                    return true;

                case R.id.shareCategory:
                    //TODO: share items
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            myViewAdapter.clearSelection();
            actionMode = null;
        }
    }
}
