package com.example.anujsharma.manageit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowImagesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    MyTaggedImagesViewAdapter myTaggedImagesViewAdapter;
    MyAllImagesViewAdapter myAllImagesViewAdapter;
    MyDataProvider myDataProvider;
    RecyclerView tagRecyclerView;
    MainActivity mainActivity;

    ViewPager viewPager;
    TabLayout tabLayout;
    String categoryName;
    int position;
    public static final int TAG_LOADER = 1;
    private static final int CAMERA_REQUEST=6;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST=0;
    private static final int CAMAERA_PERMISSION_REQUEST=2;
    File myDir, imageFile;
    String imageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        categoryName = bundle.getString("category", "");
        int imageCount=bundle.getInt("imageCount");
        position = bundle.getInt("position", -1);
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setSubtitle(imageCount+"");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFab();
            }
        });
        ColorGenerator colorGenerator=ColorGenerator.MATERIAL;
        int color=colorGenerator.getRandomColor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color-10);
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(color+10));

        checkAllPermissions();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        myDir=new File(Environment.getExternalStorageDirectory(),"Tag_iT/Images/");
        if(!myDir.exists())  myDir.mkdirs();

        myDataProvider=new MyDataProvider(this);
        tabLayout= (TabLayout) findViewById(R.id.tabLayout);
        viewPager= (ViewPager) findViewById(R.id.viewPager);
        FragmentManager fragmentManager=getSupportFragmentManager();
        viewPager.setAdapter(new MyViewPagerAdapter(fragmentManager));
        tabLayout.setBackgroundColor(color);
        tabLayout.setTabTextColors(color-100, Color.WHITE);
        tabLayout.setupWithViewPager(viewPager, true);

        myTaggedImagesViewAdapter=new MyTaggedImagesViewAdapter(this);
        myAllImagesViewAdapter=new MyAllImagesViewAdapter(this);
        myDataProvider=new MyDataProvider(this);
        getSupportLoaderManager().initLoader(TAG_LOADER, null, this);

    }

    private String getImageName() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp=sdf.format(new Date());
        return "TAG_IT"+timeStamp+".jpg";
    }

    private void clickFab() {
        Intent pictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File picturesDirectoryName=myDir;
        imageName=getImageName();
        imageFile=new File(picturesDirectoryName, imageName);
        Uri imageUri= FileProvider.getUriForFile(ShowImagesActivity.this, getPackageName()+".provider", imageFile);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(pictureIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CAMERA_REQUEST&&resultCode==RESULT_OK) {

            ContentValues contentValues=new ContentValues();
            contentValues.put(MyDatabaseHelper.CATEGORY_NAME, categoryName);
            contentValues.put(MyDatabaseHelper.IMAGE_NAME, imageName);
            contentValues.put(MyDatabaseHelper.IMAGE_FILE, imageFile.getPath());
            myDataProvider.insert(MyDataProvider.TAGS_CONTENT_URI, contentValues);

            ContentValues countContentValues=new ContentValues();
            countContentValues.put(MyDatabaseHelper.CATEGORY_NAME, categoryName);
            Cursor tempCursor= myDataProvider.query(MyDataProvider.CATEGORY_CONTENT_URI, null, MyDatabaseHelper.CATEGORY_NAME+" = ?",
                    new String[] {categoryName}, null);
            tempCursor.moveToFirst();
            countContentValues.put(MyDatabaseHelper.IMAGE_COUNT, tempCursor.getInt(tempCursor.getColumnIndex(MyDatabaseHelper.IMAGE_COUNT))+1);
            myDataProvider.update(MyDataProvider.CATEGORY_CONTENT_URI, countContentValues, MyDatabaseHelper.CATEGORY_NAME+" = ?",
                    new String[] {categoryName});

            getSupportLoaderManager().restartLoader(TAG_LOADER, null, ShowImagesActivity.this);

            Intent intent =new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(imageFile),"image/*");
            startActivity(intent);
        }
        else {
            Toast.makeText(this, imageName+" could not be saved ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader=null;
        if (id == TAG_LOADER) {
            cursorLoader = new CursorLoader(this, MyDataProvider.TAGS_CONTENT_URI, MyDatabaseHelper.ALL_COLUMNS,
                    MyDatabaseHelper.CATEGORY_NAME+" =?", new String[] {categoryName}, "datetime( "+MyDatabaseHelper.IMAGE_CREATED +" ) DESC ");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case TAG_LOADER:
                myTaggedImagesViewAdapter.changeCursor(data);
                myAllImagesViewAdapter.changeCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id) {
            case TAG_LOADER:
                myTaggedImagesViewAdapter.changeCursor(null);
                myAllImagesViewAdapter.changeCursor(null);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images_menu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_REQUEST:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "now can access external storage", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "can't access external storage", Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkAllPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {

            }
            else {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to read external storage.", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
            }

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {

            }
            else {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to access camera", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {android.Manifest.permission.CAMERA},
                        CAMAERA_PERMISSION_REQUEST);
            }
        }
    }
}
