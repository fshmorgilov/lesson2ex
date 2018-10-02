package com.example.fshmo.businesscard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

public class NewsFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(DataUtils.generateNews());
        recyclerView.setAdapter(adapter);


        decoration = (DividerItemDecoration) new DividerItemDecoration(recyclerView.getContext(), recyclerView.getLayoutMode());
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_item_decoration);
        ((DividerItemDecoration) decoration).setDrawable(drawable);
        recyclerView.addItemDecoration(decoration);

    }

    public static float dpToPixels(@NonNull final Context context, final float sizeInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources().getDisplayMetrics());
    }
}
