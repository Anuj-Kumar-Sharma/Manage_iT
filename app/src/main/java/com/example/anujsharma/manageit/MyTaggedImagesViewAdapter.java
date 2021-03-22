package com.example.anujsharma.manageit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyTaggedImagesViewAdapter extends RecyclerView.Adapter<MyTaggedImagesViewAdapter.MyImagesViewHolder> {

    LayoutInflater inflater;
    MyImagesViewHolder myImagesViewHolder;
    static Cursor mCursor;
    Context context;

    public MyTaggedImagesViewAdapter(Context context) {
        super();
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public MyImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.single_image_layout_row, parent, false);
        myImagesViewHolder=new MyImagesViewHolder(view);
        return myImagesViewHolder;
    }

    @Override
    public void onBindViewHolder(MyImagesViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.tagName.setText(mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.TABLE_NAME)));
    }

    @Override
    public int getItemCount() {
        return mCursor==null? 0:mCursor.getCount();
    }

    class MyImagesViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;
        RecyclerView recyclerViewImages;
        public MyImagesViewHolder(View itemView) {
            super(itemView);
            tagName= (TextView) itemView.findViewById(R.id.tagNameView);
            recyclerViewImages= (RecyclerView) itemView.findViewById(R.id.recyclerViewImages);
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
