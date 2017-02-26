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
        Log.d(TAG, "#" + position);
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

        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            mVideoLink = (TextView) itemView.findViewById(R.id.tv_video_list_view);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            mVideoLink.setText(MovieDetail.videoList.get(listIndex).getName());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }


}
