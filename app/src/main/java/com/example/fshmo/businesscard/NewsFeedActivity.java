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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new MyAdapter(DataUtils.generateNews());
        recyclerView.setAdapter(adapter);


        decoration = (DividerItemDecoration) new DividerItemDecoration(recyclerView.getContext(), recyclerView.getLayoutMode());
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_item_decoration);
        ((DividerItemDecoration) decoration).setDrawable(drawable);
        recyclerView.addItemDecoration(decoration);

//        recyclerView.addOnItemTouchListener(
//                new RecItem
//        );
    }

    public static float dpToPixels(@NonNull final Context context, final float sizeInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources().getDisplayMetrics());
    }
}
