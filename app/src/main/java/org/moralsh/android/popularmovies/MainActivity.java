package org.moralsh.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;

import android.os.Bundle;
import android.os.AsyncTask;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.moralsh.android.popularmovies.utilities.NetworkUtils;
import org.moralsh.android.popularmovies.Movies;

public class MainActivity extends AppCompatActivity
                implements MovieAdapter.MovieClickListener {


    private MovieAdapter mAdapter;
    private TextView mErrorMessage;
    private RecyclerView postersList;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayoutManager layoutManager;
        Context context = MainActivity.this;
        postersList = (RecyclerView) findViewById(R.id.rv_posters);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_data);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        // A grid with 2 posters per row or 3 in landscape mode
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
             layoutManager = new GridLayoutManager(this,2);
        } else {
             layoutManager = new GridLayoutManager(this,3);
        }
        postersList.setLayoutManager(layoutManager);


        mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
        postersList.setHasFixedSize(true);
        postersList.setAdapter(mAdapter);
        if ( NetworkUtils.isOnline(context)) {
            makePopularMoviesQuery();
        } else {
            postersList.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This methods retrieve the data for popular and top rated Movies, using AsyncTask via the class declarated below
     */
    private void makePopularMoviesQuery() {
        URL popularMoviesQuery = NetworkUtils.buildPopularUrl();
        new TheMovieDBQueryTask().execute(popularMoviesQuery);
    }

    private void makeTopRatedMoviesQuery() {
        URL topRatedMoviesQuery = NetworkUtils.buildTopRatedUrl();
        new TheMovieDBQueryTask().execute(topRatedMoviesQuery);
    }


    public class TheMovieDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            postersList.setVisibility(View.INVISIBLE);

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
            postersList.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            String[] posterPathArray = new String[Movies.NUMBER_OF_POSTERS];
            String[] backgroundPathArray = new String[Movies.NUMBER_OF_POSTERS];
            String[] releaseDateArray = new String[Movies.NUMBER_OF_POSTERS];
            String[] overviewArray = new String[Movies.NUMBER_OF_POSTERS];
            Double[] ratingsArray = new Double[Movies.NUMBER_OF_POSTERS];
            int[] movieIdArray = new int[Movies.NUMBER_OF_POSTERS];
            String[] movieTitleArray = new String[Movies.NUMBER_OF_POSTERS];

            if (tmdbResults != null && !tmdbResults.equals("")) {
                try {
                    JSONObject popularMoviesJSON = new JSONObject(tmdbResults);
                    JSONArray resultsArray = popularMoviesJSON.getJSONArray("results");
                    for (int i = 0; i< resultsArray.length(); i++) {
                        JSONObject jsonobject = resultsArray.getJSONObject(i);
                        String title = jsonobject.getString("title");
                        String overview = jsonobject.getString("overview");
                        String releasDate = jsonobject.getString("release_date");
                        Double rating = jsonobject.getDouble("vote_average");

                        int id = jsonobject.getInt("id");
                        String posterPath = jsonobject.getString("poster_path");
                        String backdropPath = jsonobject.getString("backdrop_path");
                        movieIdArray[i] = id;
                        posterPathArray[i] = NetworkUtils.buildPosterImageUrl(posterPath).toString();
                        backgroundPathArray[i] = NetworkUtils.buildPosterImageUrl(backdropPath,"original").toString();
                        movieTitleArray[i] = title;
                        overviewArray[i] = overview;
                        ratingsArray[i] = rating;
                        releaseDateArray[i] = releasDate;
                    }
                    Movies.setMoviePosterURL(posterPathArray);
                    Movies.setMovieTitle(movieTitleArray);
                    Movies.setMovieBackground(backgroundPathArray);
                    Movies.setMovieOverview(overviewArray);
                    Movies.setMovieRating(ratingsArray);
                    Movies.setMovieReleaseDate(releaseDateArray);

                    mAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_popular) {
            makePopularMoviesQuery();
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        } else if (itemThatWasClickedId == R.id.action_top_rated) {
            makeTopRatedMoviesQuery();
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // handle the clicks on each poster
    @Override
    public void onListItemClick(int clickedItemIndex) {
        // TODO launch new activity from here to open movie details
        String textEntered = "" + clickedItemIndex;

        Context context = MainActivity.this;
        /* This is the class that we want to start (and open) when the button is clicked. */
        Class destinationActivity = MovieDetail.class;

         Intent startChildActivityIntent = new Intent(context, destinationActivity);

         startChildActivityIntent.putExtra(Intent.EXTRA_TEXT, textEntered);
         startActivity(startChildActivityIntent);

    }

}
