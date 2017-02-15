package org.moralsh.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.moralsh.android.popularmovies.Movies;
import org.moralsh.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Class created to serve a Child Activity to display movie details, once you click in the movie poster on the main Activity
 *
 * Uses the Movies Class to display the data via the array index, will probably switch this to the movie Id in the future, but it works.
 * Created by morals on 29/01/17.
 */

public class MovieDetail extends AppCompatActivity {

    // Widgets declaration
    private ImageView mDisplayBackground;
    private ImageView mDisplayPosterDetail;
    private TextView mDisplayTitle;
    private TextView mDisplayData;
    private TextView mDisplayReview, mDisplayVideos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        int index;

        mDisplayBackground = (ImageView) findViewById(R.id.iv_background_Image);
        mDisplayPosterDetail = (ImageView) findViewById(R.id.iv_poster_Image_detail);
        mDisplayTitle = (TextView) findViewById(R.id.tv_title_display);
        mDisplayData = (TextView) findViewById(R.id.tv_data_display);
        mDisplayReview = (TextView) findViewById(R.id.tv_review_json_display);
        mDisplayVideos = (TextView) findViewById(R.id.tv_video_json_display);

        Context context = mDisplayBackground.getContext();
        Context context_poster = mDisplayPosterDetail.getContext();

        // Get the intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) { // Do we have data?

            // Get the index
            index = Integer.parseInt(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT));

            // Get the movie ID
            int movieID = Movies.getMovieId(index);
            URL reviewsJsonURL = NetworkUtils.buildMovieReviewsUrl(movieID);
            String reviewsJson = "";


            // Get the rest of the data
            if (NetworkUtils.MovieList.get(index).getMovieTitle() != null) {
                Picasso.with(context).load(NetworkUtils.MovieList.get(index).getMovieBackground()).into(mDisplayBackground);
                Picasso.with(context_poster).load(NetworkUtils.MovieList.get(index).getMoviePosterURL()).into(mDisplayPosterDetail);
                mDisplayTitle.setText(NetworkUtils.MovieList.get(index).getMovieTitle());
                mDisplayData.append("Overview: " + NetworkUtils.MovieList.get(index).getMovieOverview() + "\n\n\n");
                mDisplayData.append("Release Date: " + NetworkUtils.MovieList.get(index).getMovieReleaseDate() + "\n\n\n");
                mDisplayData.append("Average Rating: " + NetworkUtils.MovieList.get(index).getMovieRating() + "\n\n\n");
                mDisplayReview.setText(reviewsJson.toString());
            }
        }

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
                    Movie movieToAdd;
                    for (int i = 0; i< resultsArray.length(); i++) {
                        JSONObject jsonobject = resultsArray.getJSONObject(i);
                        movieToAdd = new Movie(jsonobject);
                        NetworkUtils.MovieList.add(movieToAdd);
                    }
                    // ToDo put the new adapter here
                    //mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
