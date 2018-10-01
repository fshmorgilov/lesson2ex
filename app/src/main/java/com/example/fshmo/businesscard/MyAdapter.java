package com.example.fshmo.businesscard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<NewsItem> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView categoryTextView;
        private TextView headerTextView;
        private TextView textTextView;
        private TextView dateTextView;
        private ImageView photo;


        public MyViewHolder(View v) {
            super(v);
            categoryTextView = v.findViewById(R.id.category);
            headerTextView = v.findViewById(R.id.header);
            textTextView = v.findViewById(R.id.text);
            dateTextView = v.findViewById(R.id.date);
            photo = v.findViewById(R.id.photo);
        }

        public void bind(NewsItem newsItem) {
            categoryTextView.setText(newsItem.getCategory().getName());
            headerTextView.setText(newsItem.getTitle());
            textTextView.setText(newsItem.getFullText());
            dateTextView.setText(newsItem.getPublishDate().toString());
            Glide.with(photo.getContext()) //FIXME Переделать контекст
                    .load(newsItem.getImageUrl()).into(photo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<NewsItem> myDataset) {
        dataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(dataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}