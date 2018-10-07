package com.example.fshmo.businesscard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<NewsItem> dataset;
    private OnItemClickListener onItemClickListener;
    private RequestManager glide;

    public interface OnItemClickListener {
        void onItemClick(NewsItem item);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTextView;
        private TextView headerTextView;
        private TextView textTextView;
        private TextView dateTextView;
        private ImageView photo;
        private View view;


        public MyViewHolder(View v) {
            super(v);
            this.view = v;
            categoryTextView = v.findViewById(R.id.category);
            headerTextView = v.findViewById(R.id.header);
            textTextView = v.findViewById(R.id.text);
            dateTextView = v.findViewById(R.id.date);
            photo = v.findViewById(R.id.photo);
        }

        public void bind(final NewsItem newsItem, RequestManager glide, OnItemClickListener onItemClickListener) {
            categoryTextView.setText(newsItem.getCategory().getName());
            headerTextView.setText(newsItem.getTitle());
            textTextView.setText(newsItem.getPreviewText());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, EEEE", Locale.ENGLISH);
            dateTextView.setText(sdf.format(newsItem.getPublishDate()));
            glide.load(newsItem.getImageUrl()).into(photo);
            view.setOnClickListener(v -> onItemClickListener.onItemClick(newsItem));
        }
    }

    public MyAdapter(List<NewsItem> newsItems, RequestManager glide, OnItemClickListener clickListener) {
        this.glide = glide;
        this.dataset = newsItems;
        this.onItemClickListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(dataset.get(position), glide, onItemClickListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}