package com.example.anujsharma.manageit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class MyAllImagesViewAdapter extends RecyclerView.Adapter<MyAllImagesViewAdapter.MyAllImagesViewHolder> {

    LayoutInflater layoutInflater;
    Context context;
    MyAllImagesViewHolder myAllImagesViewHolder;
    static Cursor mCursor;

    public MyAllImagesViewAdapter(Context context) {
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public MyAllImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.all_images_single_grid, parent, false);
        myAllImagesViewHolder=new MyAllImagesViewHolder(view);
        return myAllImagesViewHolder;
    }

    @Override
    public void onBindViewHolder(MyAllImagesViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String imageFileName=mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.IMAGE_FILE));
        File imageFile=new File(imageFileName);
        Glide.with(context)
                .load(Uri.fromFile(imageFile))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.allImagesSingleImage);
        Log.d("tag", mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.IMAGE_CREATED)));
    }

    @Override
    public int getItemCount() {
        return mCursor==null? 0:mCursor.getCount();
    }

    class MyAllImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView allImagesSingleImage;
        public MyAllImagesViewHolder(View itemView) {
            super(itemView);
            allImagesSingleImage= (ImageView) itemView.findViewById(R.id.allImagesSingleImage);
            allImagesSingleImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            String imageFileName= mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.IMAGE_FILE));
            Intent intent =new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(imageFileName)),"image/*");
            context.startActivity(intent);
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        this.mCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }
}
