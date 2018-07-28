package com.benites.jeral.router_bar.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        /*Bitmap bitmap = ImageBase64.decodeBase64(pubEntity.getImage(),context);
        holder.anImageView.setImageBitmap(bitmap);
        bitmap = null;
        System.gc();*/
        //holder.anImageView.setImageBitmap(getResizedBitmap(decodeArray(pubEntity.getImage()),100,100));
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


    private Bitmap decodeArray(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
        options.inPurgeable = true; // inPurgeable is used to free up memory while required
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);//Decode image, "thumbnail" is the object of image file
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

// "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}
