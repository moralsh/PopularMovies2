package org.moralsh.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morals on 18/02/17.
 */

public class MoviesVideoAdapter extends RecyclerView.Adapter<MoviesVideoAdapter.MovieVideoViewHolder> {

    private static final String TAG = MoviesVideoAdapter.class.getSimpleName();
    final private MovieVideoClickListener mOnClickListener;
    private int mNumberItems;

    public interface MovieVideoClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MoviesVideoAdapter(MovieVideoClickListener mOnClickListener, int numberOfItems) {
        this.mOnClickListener = mOnClickListener;
        this.mNumberItems = MovieDetail.videoList.size();
    }

    public void setNumberItems(int number) {
        mNumberItems = number;
    }

    @Override
    public MoviesVideoAdapter.MovieVideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieVideoViewHolder viewHolder = new MovieVideoViewHolder(view);
        return viewHolder;
    }




    @Override
    public void onBindViewHolder(MoviesVideoAdapter.MovieVideoViewHolder holder, int position) {
        if (position < MovieDetail.videoList.size()) {
            holder.bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    class MovieVideoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView mVideoLink;
        ImageView mVideoThumbnail;

        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            mVideoLink = (TextView) itemView.findViewById(R.id.tv_video_list_view);
            mVideoThumbnail = (ImageView) itemView.findViewById(R.id.ic_video_list_view);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            Context context = mVideoThumbnail.getContext();

            String img_url="http://img.youtube.com/vi/"+ MovieDetail.videoList.get(listIndex).getKey() + "/0.jpg";
            mVideoLink.setText(MovieDetail.videoList.get(listIndex).getName());
            Picasso.with(context).load(img_url).into(mVideoThumbnail);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }


}
