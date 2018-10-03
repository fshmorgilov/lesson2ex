package com.example.fshmo.businesscard;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.example.fshmo.businesscard.decorators.GridSpaceItemDecoration;

import java.util.Objects;

public class NewsFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;

    //    WindowManager wn = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//    private Display display = getWindowManager().getDefaultDisplay();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        int orientation = this.getResources().getConfiguration().orientation;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MyAdapter(DataUtils.generateNews());
        decoration = new GridSpaceItemDecoration(4, 4);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else
            layoutManager = new GridLayoutManager(this, 2);



        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setHasFixedSize(true);
    }

    public static float dpToPixels(@NonNull final Context context, final float sizeInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources().getDisplayMetrics());
    }
}
