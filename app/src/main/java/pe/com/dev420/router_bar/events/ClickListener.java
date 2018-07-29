package pe.com.dev420.router_bar.events;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);

    void onGoGps(int position);

    void onRemove(int position);
}