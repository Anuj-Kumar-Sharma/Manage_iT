package com.example.anujsharma.manageit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    Context context;

    static final String DATABASE_NAME="myDatabase";
    static final String TABLE_NAME="myImages";
    static final int DATABASE_VERSION=5;
    static final String ID="_id";
    static final String IMAGE_NAME="imageName";
    static final String IMAGE_FILE="imageFile";
    static final String IMAGE_CREATED="imageCreated";
    static final String TAG_NAME="tagName";
    static final String CATEGORY_NAME="categoryName";

    static final String CATEGORY_TABLE_NAME="myCategory";
    static final String TAG_COUNT="tagCount";
    static final String IMAGE_COUNT="imageCount";

    public static final String[] ALL_COLUMNS={ID, TAG_NAME, CATEGORY_NAME, IMAGE_NAME, IMAGE_FILE, IMAGE_CREATED};
    public static final String[] ALL_CATEGORY_TABLE_COLUMNS={CATEGORY_NAME, TAG_COUNT, IMAGE_COUNT};

    static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ( "+
            ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            TAG_NAME+" VARCHAR, "+
            CATEGORY_NAME+" VARCHAR, "+
            IMAGE_NAME+" VARCHAR, "+
            IMAGE_FILE+" VARCHAR, "+
            IMAGE_CREATED+" TEXT default CURRENT_TIMESTAMP "+
            " ) ";

    static final String CREATE_CATEGORY_TABLE="CREATE TABLE "+CATEGORY_TABLE_NAME+" ( "+
            CATEGORY_NAME+" VARCHAR PRIMARY KEY, "+
            TAG_COUNT+" INTEGER, "+
            IMAGE_COUNT+" INTEGER "+
            " ) ";

    static final String DROP_CATEGORY_TABLE="DROP TABLE IF EXISTS "+ CATEGORY_TABLE_NAME;
    static final String DROP_TABLE="DROP TABLE IF EXISTS "+ TABLE_NAME;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context, "table created", Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "table upgraded", Toast.LENGTH_SHORT).show();
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_CATEGORY_TABLE);
        onCreate(db);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
