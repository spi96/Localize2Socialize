package com.example.spi.localize2socialize;

import android.view.View;

public interface RecyclerClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
