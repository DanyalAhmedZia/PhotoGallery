package com.example.ahmedmurtaza.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Murtaza on 12/28/2016.
 */

public class PhotoGallaryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
//            @Override
//            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
//                //Drawable drawable = new BitmapDrawable(getResources(),thumbnail);
//               // target.bindGalleryItem(drawable);
//            }
//        });
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
//        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmant_photo_gallery,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    // view holder for recycler view
    private class  PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        public PhotoHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_imageview);
        }

        void bindGalleryItem(GalleryItem galleryItem){
            Picasso.with(getActivity())
                    .load(galleryItem.getUrl())
                    .placeholder(R.mipmap.sloth)
                    .into(mImageView);

        }
    }
    // adapter for recycler view
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.gallery_item,viewGroup,false);
            return new PhotoHolder(view);
        }
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);

        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
    // fetches data from flickr
    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

}
