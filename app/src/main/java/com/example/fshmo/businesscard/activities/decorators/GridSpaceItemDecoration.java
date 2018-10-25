package com.example.fshmo.businesscard.activities.decorators;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;
    private final int horizontalSpaceWidth;

    public GridSpaceItemDecoration(int verticalSpaceHeight, int horizontalSpaceWidth) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.horizontalSpaceWidth = horizontalSpaceWidth;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
        outRect.top = verticalSpaceHeight;
        outRect.left = horizontalSpaceWidth;
        outRect.right = horizontalSpaceWidth;
    }
}
