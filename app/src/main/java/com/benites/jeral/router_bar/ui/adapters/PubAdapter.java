package com.benites.jeral.router_bar.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.events.ClickListener;
import com.benites.jeral.router_bar.model.PubEntity;

import java.util.List;


public class PubAdapter extends RecyclerView.Adapter<PubAdapter.ViewHolder> {

    private final List<PubEntity> pubEntities;
    private final Context context;
    private final ClickListener clickListener;

    public PubAdapter(List<PubEntity> pubEntities, Context context, ClickListener clickListener) {
        this.pubEntities = pubEntities;
        this.context = context;
        this.clickListener = clickListener;
    }

    public PubAdapter(List<PubEntity> pubEntities, Context context) {
        this.pubEntities = pubEntities;
        this.context = context;
        this.clickListener = null;
    }

    @Override
    public PubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PubEntity pubEntity = pubEntities.get(position);
        byte[] decodedString = Base64.decode(pubEntity.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.anImageView.setImageBitmap(decodedByte);
        holder.pubNameTextView.setText(pubEntity.getName());
        holder.pubPhoneTextView.setText(pubEntity.getSocial().getPhone());
        holder.pubAddressTextView.setText(pubEntity.getAddress().getStreet());
        holder.phoneImageView.setOnClickListener(v ->
                v.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pubEntity.getSocial().getPhone(), null))));
        holder.anImageView.setOnClickListener(v -> {
            if (clickListener != null) {
                //clickListener.onBarClick(bar.getiCodBar());
            }
        });
        holder.gpsImageView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onGoGps(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return pubEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView anImageView;
        TextView pubNameTextView;
        ImageView phoneImageView;
        TextView pubPhoneTextView;
        ImageView gpsImageView;
        TextView pubAddressTextView;

        ViewHolder(View itemView) {
            super(itemView);
            anImageView = itemView.findViewById(R.id.pubImageView);
            pubNameTextView = itemView.findViewById(R.id.pubNameTextView);
            phoneImageView = itemView.findViewById(R.id.phoneImageView);
            pubPhoneTextView = itemView.findViewById(R.id.pubPhoneTextView);
            gpsImageView = itemView.findViewById(R.id.gpsImageView);
            pubAddressTextView = itemView.findViewById(R.id.pubAddressTextView);
        }
    }

}
