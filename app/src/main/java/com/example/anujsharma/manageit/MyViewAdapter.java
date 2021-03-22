package com.example.anujsharma.manageit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyViewAdapter extends SelectableAdapter<MyViewAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    MyViewHolder myViewHolder;
    static Cursor mCursor;
    MyDataProvider myDataProvider;
    private MyViewHolder.ClickListener clickListener;
    MainActivity mainActivity;

    public MyViewAdapter(MyViewHolder.ClickListener clickListener, Context context) {
        super();
        myDataProvider=new MyDataProvider(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.clickListener = clickListener;
        mainActivity=new MainActivity();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_row_layout, parent, false);
        myViewHolder = new MyViewHolder(view, clickListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        String categoryName = mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.CATEGORY_NAME));
        holder.categoryTextView.setText(categoryName);
        holder.tagCountView.setText(mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.TAG_COUNT)));
        holder.imageCountView.setText(mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.IMAGE_COUNT)));
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        final int color = colorGenerator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .buildRound(categoryName.charAt(0) + "", color);
        holder.iconImageView.setImageDrawable(drawable);
        if (isSelected(position)) {
            myViewHolder.iconImageView.setImageResource(R.mipmap.ic_check_image);
            myViewHolder.categoryTextView.setTextColor(Color.rgb(150, 150, 150));
            myViewHolder.iconImageView.setBackgroundColor(Color.rgb(250, 250, 250));
            myViewHolder.cardView.setBackgroundColor(Color.rgb(250, 250, 250));
            DrawableCompat.setTint(myViewHolder.imageCountImage.getDrawable(), Color.rgb(210, 210, 220));
            DrawableCompat.setTint(myViewHolder.tagCountImage.getDrawable(), Color.rgb(210, 210, 220));
        } else {
            DrawableCompat.setTint(holder.imageCountImage.getDrawable(), color);
            DrawableCompat.setTint(holder.tagCountImage.getDrawable(), color);
            holder.iconImageView.setBackgroundColor(Color.WHITE);
            holder.cardView.setBackgroundColor(Color.WHITE);
            holder.categoryTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView categoryTextView, tagCountView, imageCountView;
        ImageView iconImageView, imageCountImage, tagCountImage;
        CardView cardView;
        private ClickListener listener;

        public MyViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryNameView);
            this.listener = listener;
            tagCountView = itemView.findViewById(R.id.tagCountView);
            imageCountView = itemView.findViewById(R.id.imageCountView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            cardView = itemView.findViewById(R.id.singleRow);
            imageCountImage = itemView.findViewById(R.id.imagecountImage);
            tagCountImage = itemView.findViewById(R.id.tagCountImage);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                mCursor.moveToPosition(getAdapterPosition());
                String category=mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.CATEGORY_NAME));
                int imageCount=mCursor.getInt(mCursor.getColumnIndex(MyDatabaseHelper.IMAGE_COUNT));
                listener.onItemClicked(getAdapterPosition(), category, imageCount);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }

            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position, String category, int imageCount);

            boolean onItemLongClicked(int position);
        }

    }

    public void removeItem(int position) {
        mCursor.moveToPosition(position);
        myDataProvider.delete(MyDataProvider.CATEGORY_CONTENT_URI, MyDatabaseHelper.CATEGORY_NAME+" =?",
                new String[] {mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.CATEGORY_NAME))});

        notifyItemRemoved(position);
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mCursor.moveToPosition(positionStart);
            myDataProvider.delete(MyDataProvider.CATEGORY_CONTENT_URI, MyDatabaseHelper.CATEGORY_NAME+" =?",
                    new String[] {mCursor.getString(mCursor.getColumnIndex(MyDatabaseHelper.CATEGORY_NAME))});
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                if (count > 0) {
                    positions.subList(0, count).clear();
                }
            }
        }
    }


    public Cursor swapCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;
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