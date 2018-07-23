package com.benites.jeral.router_bar.events;

import android.view.View;

/**
 * Created by emedinaa on 14/08/17.
 */

public interface ClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}