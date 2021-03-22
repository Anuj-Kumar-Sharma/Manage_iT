package com.example.anujsharma.manageit;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.Nullable;

public class MyDataProvider extends ContentProvider {

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int CATEGORY = 1;
    public static final int TAGS = 2;
    private static final String AUTHORITY = "com.example.anujsharma.magnageit";
    private static final String CATEGORY_BASE_PATH = "myImages";
    public static final Uri CATEGORY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CATEGORY_BASE_PATH);
    private static final String TAGS_BASE_PATH = "myCategory";
    public static final Uri TAGS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TAGS_BASE_PATH);

    static {
        uriMatcher.addURI(AUTHORITY, CATEGORY_BASE_PATH, CATEGORY);
        uriMatcher.addURI(AUTHORITY, TAGS_BASE_PATH, TAGS);
    }

    SQLiteDatabase sqLiteDatabase;
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    public MyDataProvider(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        this.context = context;
        sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
    }
    public MyDataProvider() {
    }

    @Override
    public boolean onCreate() {
        myDatabaseHelper = new MyDatabaseHelper(getContext());
        sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        if (match == CATEGORY)
            return sqLiteDatabase.query(MyDatabaseHelper.CATEGORY_TABLE_NAME, MyDatabaseHelper.ALL_CATEGORY_TABLE_COLUMNS, selection,
                    selectionArgs, null, null,  MyDatabaseHelper.CATEGORY_NAME+" COLLATE NOCASE ASC");

        else if (match == TAGS) {
            return sqLiteDatabase.query(MyDatabaseHelper.TABLE_NAME, MyDatabaseHelper.ALL_COLUMNS, selection,
                    selectionArgs, null, null,  MyDatabaseHelper.TAG_NAME+" COLLATE NOCASE ASC");
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        if (match == CATEGORY) {
            long id = sqLiteDatabase.insert(MyDatabaseHelper.CATEGORY_TABLE_NAME, null, values);
            context.getContentResolver().notifyChange(CATEGORY_CONTENT_URI, null);
            return Uri.parse(CATEGORY_BASE_PATH + "/" + id);
        } else if (match == TAGS) {
            long id = sqLiteDatabase.insert(MyDatabaseHelper.TABLE_NAME, null, values);
            return Uri.parse(TAGS_BASE_PATH + "/" + id);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == CATEGORY) {
            int delete=sqLiteDatabase.delete(MyDatabaseHelper.CATEGORY_TABLE_NAME, selection, selectionArgs);
            context.getContentResolver().notifyChange(CATEGORY_CONTENT_URI, null);
            return delete;
        } else if (match == TAGS) {
            return sqLiteDatabase.delete(MyDatabaseHelper.TABLE_NAME, selection, selectionArgs);
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == CATEGORY) {
            int update=sqLiteDatabase.update(MyDatabaseHelper.CATEGORY_TABLE_NAME, values, selection, selectionArgs);
            context.getContentResolver().notifyChange(CATEGORY_CONTENT_URI, null);
            return update;
        } else if (match == TAGS) {
            int update=sqLiteDatabase.update(MyDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
            context.getContentResolver().notifyChange(TAGS_CONTENT_URI, null);
            return update;
        }
        return -1;
    }
}
