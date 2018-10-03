package com.example.fshmo.businesscard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import com.example.fshmo.businesscard.decorators.GridSpaceItemDecoration;

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

//        decoration = new DividerItemDecoration(this, recyclerView.getLayoutMode());
//        decoration = new VerticalSpaceItemDecoration(16);
        decoration = new GridSpaceItemDecoration(4,4);
        recyclerView.addItemDecoration(decoration);

//        decoration = (DividerItemDecoration) new DividerItemDecoration(recyclerView.getContext(), recyclerView.getLayoutMode());
//        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_item_decoration);
//        ((DividerItemDecoration) decoration).setDrawable(drawable);
//        recyclerView.addItemDecoration(decoration);
    }

    public static float dpToPixels(@NonNull final Context context, final float sizeInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources().getDisplayMetrics());
    }
}
