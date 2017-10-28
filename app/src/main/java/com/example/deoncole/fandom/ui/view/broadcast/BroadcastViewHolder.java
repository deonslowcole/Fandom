package com.example.deoncole.fandom.ui.view.broadcast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.model.Broadcast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class BroadcastViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "BroadcastViewHolder";

    private TextView userNameDisplay;
    private TextView messageDisplay;
    private TextView timestampDisplay;
    private ImageView imageDisplay;

    BroadcastViewHolder(View itemView) {
        super(itemView);

        imageDisplay = (ImageView) itemView.findViewById(R.id.messageImgV);
        userNameDisplay = (TextView) itemView.findViewById(R.id.messUserNameTV);
        messageDisplay = (TextView) itemView.findViewById(R.id.messageTV);
        timestampDisplay = (TextView) itemView.findViewById(R.id.messTimestampTv);

    }

    void bind(BroadcastViewItem broadcastViewItem) {
        final Broadcast broadcast = broadcastViewItem.getBroadcast();
        if (broadcast.getBitmapUrl() != null) {
            Picasso.with(itemView.getContext()).load(broadcast.getBitmapUrl()).into(imageDisplay);
        } else {
            imageDisplay.setVisibility(View.GONE);
        }

        messageDisplay.setText(broadcast.getMessage());
        userNameDisplay.setText(broadcastViewItem.getArtist().getArtistName());
        timestampDisplay.setText(broadcast.getFormattedTimestamp());

        itemView.findViewById(R.id.item_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.post_menu_options);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        String bitmapUrl = broadcast.getBitmapUrl();
                        if (bitmapUrl != null) {
                            Picasso.with(itemView.getContext()).load(bitmapUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, messageDisplay.getText());
                                    shareIntent.setType("image/*");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(itemView.getContext(), bitmap));
                                    itemView.getContext().startActivity(Intent.createChooser
                                            (shareIntent,
                                                    itemView.getContext().getString(R.string.share)));
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Log.d(TAG, "onBitmapFailed() called with: errorDrawable = [" + errorDrawable + "]");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                        } else {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, messageDisplay.getText());
                            shareIntent.setType("text/plain");
                            itemView.getContext().startActivity(Intent.createChooser(shareIntent,
                                    itemView.getContext().getString(R.string.share)));
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private Uri getLocalBitmapUri(Context context, Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.e(TAG, "Error loading the image to share");
        }
        return bmpUri;
    }

}
