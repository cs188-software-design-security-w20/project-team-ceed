package com.ceed.tripster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class GalleryAdapter extends FirebaseRecyclerAdapter<GalleryPhoto, GalleryAdapter.ViewHolder> {
    private TextView _noPhotoTextView;

    public GalleryAdapter(FirebaseRecyclerOptions<GalleryPhoto> options, TextView noPhotoTextView) {
        super(options);
        _noPhotoTextView = noPhotoTextView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gallery_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull GalleryPhoto model) {
        _noPhotoTextView.setVisibility(View.GONE);
        holder.setPhoto(model);

        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GALLERYADAPTER", "clicked gallery photo");
//                if (mListener != null) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onItemClick();
//                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View contentView;

        private final ImageView _imageView;
        private GalleryPhoto _photo;

        public ViewHolder(View view) {
            super(view);
            contentView = view;
            _imageView = view.findViewById(R.id.imageView);
        }

        public void setPhoto(GalleryPhoto photo) {
            Log.d("GALLERYADAPTER", "Loading image with url " + photo.imageUrl);

            _photo = photo;
            new DownloadImageTask(_imageView).execute(photo.imageUrl);
        }

        public GalleryPhoto getPhoto(GalleryPhoto photo) {
            return _photo;
        }

        // async imageView modification to avoid UI hang when downloading images
        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            WeakReference<ImageView> imageView;

            public DownloadImageTask(ImageView imageView) {
                this.imageView = new WeakReference<ImageView>(imageView);
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                ImageView unwrappedImageView = imageView.get();

                if (unwrappedImageView != null) {
                    unwrappedImageView.setImageBitmap(result);
                }
            }
        }


        @Override
        public String toString() {
            return super.toString() + " Photo";
        }
    }
}
