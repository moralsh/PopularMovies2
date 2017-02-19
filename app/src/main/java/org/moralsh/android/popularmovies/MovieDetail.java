package org.moralsh.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moralsh.android.popularmovies.utilities.NetworkUtils;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class created to serve a Child Activity to display movie details, once you click in the movie poster on the main Activity
 *
 * Uses the Movies Class to display the data via the array index, will probably switch this to the movie Id in the future, but it works.
 * Created by morals on 29/01/17.
 */

// ToDo Save state when rotating

public class MovieDetail extends AppCompatActivity
        implements MoviesVideoAdapter.MovieVideoClickListener {

    private static final String IS_FAVORITE = "favorite";
    private static final String SHOWING_VIDEOS = "videos";
    private static final String SHOWING_REVIEWS = "reviews";

    final public static List<Movie.Reviews> reviewList = new ArrayList<>();
    final public static List<Movie.Videos> videoList = new ArrayList<>();

    private MoviesVideoAdapter mVideoAdapter;
    // Widgets declaration
    private boolean isFavorite;
    private boolean showingReviews;
    private boolean showingVideos;
    private ImageView mDisplayBackground;
    private ImageView mDisplayPosterDetail;
    private TextView mDisplayTitle;
    private TextView mDisplayData;
    private TextView mDisplayReview, mDisplayReviewTitle, mDisplayVideoTitle;
    private RecyclerView mDisplayVideos;
    private ImageView mShowReviews, mShowVideos;
    private ImageView mFavorite;


    public void makeFavorite(View view) {
        if (!isFavorite) {
            mFavorite.setImageResource(R.mipmap.ic_star);
            isFavorite = true;
        } else {  //unfavorite it
            mFavorite.setImageResource(R.mipmap.ic_star_empty);
            isFavorite = false;
        }
    }

    public void showHideReviews(View view){
        if (!showingReviews) {
            mDisplayReview.setVisibility(View.VISIBLE);
            mShowReviews.setImageResource(R.mipmap.ic_down);
            mDisplayReviewTitle.setText(getString(R.string.hide_reviews));
            showingReviews = true;
        } else {  //hide
            mDisplayReview.setVisibility(View.GONE);
//            mDisplayReview.setVisibility(View.INVISIBLE);
            mShowReviews.setImageResource(R.mipmap.ic_up);
            mDisplayReviewTitle.setText(getString(R.string.show_reviews));
            showingReviews = false;
        }
    }

    public void showHideVideos(View view) {
        if (!showingVideos) {
            mDisplayVideos.setVisibility(View.VISIBLE);
            mShowVideos.setImageResource(R.mipmap.ic_down);
            mDisplayVideoTitle.setText(getString(R.string.hide_videos));
            showingVideos = true;
        } else {  //hide
            mDisplayVideos.setVisibility(View.GONE);
            mShowVideos.setImageResource(R.mipmap.ic_up);
            mDisplayVideoTitle.setText(getString(R.string.show_videos));
            showingVideos = false;
        }
    }

    private void makeReviewsQuery(int movieId) {
        URL reviewsQuery = NetworkUtils.buildMovieReviewsUrl(movieId);
        new ReviewsTask().execute(reviewsQuery);
    }

    private void makeVideosQuery(int movieId) {
        URL videosQuery = NetworkUtils.buildMovieVideosUrl(movieId);
        new VideosTask().execute(videosQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        int index;

        mDisplayBackground = (ImageView) findViewById(R.id.iv_background_Image);
        mDisplayPosterDetail = (ImageView) findViewById(R.id.iv_poster_Image_detail);
        mDisplayTitle = (TextView) findViewById(R.id.tv_title_display);
        mDisplayData = (TextView) findViewById(R.id.tv_data_display);
        mDisplayReview = (TextView) findViewById(R.id.tv_review_json_display);
        mDisplayVideos = (RecyclerView) findViewById(R.id.rv_video_json_display);
        mDisplayReviewTitle = (TextView) findViewById(R.id.tv_review_title_display);
        mDisplayVideoTitle = (TextView) findViewById(R.id.tv_video_title_display);
        mShowReviews = (ImageView) findViewById(R.id.ic_show_reviews);
        mShowVideos = (ImageView) findViewById(R.id.ic_show_videos);
        mFavorite = (ImageView) findViewById(R.id.ic_fav_icon);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDisplayVideos.setLayoutManager(layoutManager);
        mVideoAdapter = new MoviesVideoAdapter(this,20);
        mDisplayVideos.setAdapter(mVideoAdapter);

        if (savedInstanceState != null) {
            isFavorite = savedInstanceState.getBoolean(IS_FAVORITE);
            showingReviews = savedInstanceState.getBoolean(SHOWING_REVIEWS);
            showingVideos = savedInstanceState.getBoolean(SHOWING_VIDEOS);

            // restore the UI accordingly
            if (isFavorite) {
                mFavorite.setImageResource(R.mipmap.ic_star);
            }
            if (showingReviews) {
                mDisplayReview.setVisibility(View.VISIBLE);
                mShowReviews.setImageResource(R.mipmap.ic_down);
                mDisplayReviewTitle.setText(getString(R.string.hide_reviews));
            }
            if (showingVideos) {
                mDisplayVideos.setVisibility(View.VISIBLE);
                mDisplayVideos.scrollToPosition(videoList.size());
                mShowVideos.setImageResource(R.mipmap.ic_down);
                mDisplayVideoTitle.setText(getString(R.string.hide_videos));
            }
        } else {
            isFavorite = false;
            showingVideos = false;
            showingReviews = false;
        }

        Context context = mDisplayBackground.getContext();
        Context context_poster = mDisplayPosterDetail.getContext();

        // Get the intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) { // Do we have data?

            // Get the index
            index = Integer.parseInt(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT));

            // Get the movie ID
            int movieID = NetworkUtils.MovieList.get(index).getMovieId();

            // Get the rest of the data
            if (NetworkUtils.MovieList.get(index).getMovieTitle() != null) {
                Picasso.with(context).load(NetworkUtils.MovieList.get(index).getMovieBackground()).into(mDisplayBackground);
                Picasso.with(context_poster).load(NetworkUtils.MovieList.get(index).getMoviePosterURL()).into(mDisplayPosterDetail);
                mDisplayTitle.setText(NetworkUtils.MovieList.get(index).getMovieTitle());
                mDisplayData.append("Overview: " + NetworkUtils.MovieList.get(index).getMovieOverview() + "\n\n\n");
                mDisplayData.append("Release Date: " + NetworkUtils.MovieList.get(index).getMovieReleaseDate() + "\n\n\n");
                mDisplayData.append("Average Rating: " + NetworkUtils.MovieList.get(index).getMovieRating() + "\n\n\n");
            }
            makeReviewsQuery(movieID);
            makeVideosQuery(movieID);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_FAVORITE,isFavorite);
        outState.putBoolean(SHOWING_REVIEWS,showingReviews);
        outState.putBoolean(SHOWING_VIDEOS,showingVideos);
    }

    // handle the clicks on each poster

    public void onListItemClick(int clickedItemIndex) {
        // TODO launch new activity from here to watch Videos
        String textEntered = "" + clickedItemIndex;

        Context context = MovieDetail.this;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+videoList.get(clickedItemIndex).getKey())));


    }
    public class ReviewsTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(URL... params) {
            URL apiURL = params[0];
            String tmdbResults = null;
            try {
                tmdbResults = NetworkUtils.getResponseFromHttpUrl(apiURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbResults;
        }

        // we populate here the arrays in Movies
        @Override
        protected void onPostExecute(String tmdbResults) {

            if (tmdbResults != null && !tmdbResults.equals("")) {
                try {
                    JSONObject popularMoviesJSON = new JSONObject(tmdbResults);
                    JSONArray resultsArray = popularMoviesJSON.getJSONArray("results");
                    Movie.Reviews reviewToAdd;
                    for (int i = 0; i< resultsArray.length(); i++) {
                        JSONObject jsonobject = resultsArray.getJSONObject(i);
                        Movie movieParent = new Movie();
                        reviewToAdd = movieParent.new Reviews();
                        reviewToAdd.jsonParse(jsonobject);
                        reviewList.add(reviewToAdd);
                        mDisplayReview.append("Author: " + reviewToAdd.getAuthor() + "\n\n");
                        mDisplayReview.append(reviewToAdd.getContent() + "\n\n\n");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class VideosTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(URL... params) {
            URL apiURL = params[0];
            String tmdbResults = null;
            try {
                tmdbResults = NetworkUtils.getResponseFromHttpUrl(apiURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbResults;
        }

        // we populate here the arrays in Movies
        @Override
        protected void onPostExecute(String tmdbResults) {
            Context context = mDisplayVideos.getContext();
            if (tmdbResults != null && !tmdbResults.equals("")) {
                try {
                    videoList.clear();
                    JSONObject popularMoviesJSON = new JSONObject(tmdbResults);
                    JSONArray resultsArray = popularMoviesJSON.getJSONArray("results");
                    Movie.Videos videosToAdd;
                    for (int i = 0; i< resultsArray.length(); i++) {
                        JSONObject jsonobject = resultsArray.getJSONObject(i);
                        Movie movieParent = new Movie();
                        videosToAdd = movieParent.new Videos();
                        videosToAdd.jsonParse(jsonobject);
                        videoList.add(videosToAdd);
                        Log.d("Videos", jsonobject.toString());
                    }
                    mVideoAdapter.setNumberItems(videoList.size());
                    mVideoAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
