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
    /*    private Context mContext;
    private List<Movie.Videos> mVideos = new ArrayList<>();

    public MoviesVideoAdapter(Context context, List<Movie.Videos> videos) {
        mContext = context;
        mVideos = videos;
        Log.d("MovieVideoAdapter",videos.get(1).getName());
    }


    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.video_view, parent, false);
        }

        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.ic_video_list_view);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tv_video_list_view);

        Movie.Videos video = mVideos.get(position);
        tvTitle.setText(video.getName());
        Log.d("MovieVideoAdapter","position: " + position + " Name: " + video.getName());
        Log.d("MovieVideoAdapter","size: " + mVideos.size());
        // ivItem.setImageResource(item.getImage());

        return rowView;
    } */

}
