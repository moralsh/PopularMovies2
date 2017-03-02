package org.moralsh.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;

import android.os.Bundle;
import android.os.AsyncTask;

import android.util.DisplayMetrics;
import android.util.Log;
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
import org.moralsh.android.popularmovies.data.FavoritesContract;
import org.moralsh.android.popularmovies.utilities.NetworkUtils;
import org.moralsh.android.popularmovies.Movies;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.moralsh.android.popularmovies.data.FavoritesContract.FavoritesEntry.CONTENT_URI;

public class MainActivity extends AppCompatActivity
                implements MovieAdapter.MovieClickListener {
    private static final String QUERY_TYPE = "query_type";


    private MovieAdapter mAdapter;

    @BindView(R.id.rv_posters) RecyclerView postersList;
    @BindView(R.id.tv_error_message) TextView mErrorMessage;
    @BindView(R.id.pb_loading_data) ProgressBar mLoadingIndicator;

    private String movieQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int columns = calculateNoOfColumns(this);
        GridLayoutManager layoutManager;
        Context context = MainActivity.this;

        postersList.setVisibility(View.INVISIBLE);

        // A grid dynamically adjusted depending on the available width
        layoutManager = new GridLayoutManager(this,columns);
        postersList.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            movieQuery = savedInstanceState.getString(QUERY_TYPE);
        } else {
            movieQuery = "popular"; // we just initialize with one of them
        }


        mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
        postersList.setAdapter(mAdapter);

        if ( NetworkUtils.isOnline(context)) {
            Log.d("MovieQuery","MovieQuery: " + movieQuery);
            switch (movieQuery) {
                case "popular":
                    makePopularMoviesQuery(1);
                    break;
                case "toprated":
                    makeTopRatedMoviesQuery(1);
                    break;
                case "favorites":
                    getFavoriteMovies();
                    break;
            }
        } else {
            postersList.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("SaveInstanceState", "Salvamos " + movieQuery);
        outState.putString(QUERY_TYPE,movieQuery);
    }

    // This is here to ask for more pages when scrolled to bottom, ToDo later
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
    /**
     * This methods retrieve the data for popular and top rated Movies, using AsyncTask via the class declarated below
     */
    private void makePopularMoviesQuery(int page) {
        URL popularMoviesQuery = NetworkUtils.buildPopularUrl(page);
        MyTaskParams params = new MyTaskParams(page, popularMoviesQuery);
        new TheMovieDBQueryTask().execute(params);
    }

    private void makeTopRatedMoviesQuery(int page) {
        URL topRatedMoviesQuery = NetworkUtils.buildTopRatedUrl(page);
        MyTaskParams params = new MyTaskParams(page, topRatedMoviesQuery);
        new TheMovieDBQueryTask().execute(params);
    }

    private void getFavoriteMovies() {
        Cursor mData;
        Uri uri = CONTENT_URI;

        NetworkUtils.MovieList.clear();
        mData = getContentResolver().query(uri, null, null, null, null);

        if (mData.moveToFirst()) {
            do {
                Movie movieToAdd = new Movie();

                int movieIdIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID);
                int overviewIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW);
                int releaseDateIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE);
                int ratingIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RATING);
                int titleIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE);
                int posterIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER);
                int backgroundIndex = mData.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_BACKGROUND);

                movieToAdd.setMovieId(mData.getInt(movieIdIndex));
                movieToAdd.setMovieOverview(mData.getString(overviewIndex));
                movieToAdd.setMovieTitle(mData.getString(titleIndex));
                movieToAdd.setMovieReleaseDate(mData.getString(releaseDateIndex));
                movieToAdd.setMovieRating(mData.getDouble(ratingIndex));
                movieToAdd.setMoviePosterURL(mData.getString(posterIndex));
                movieToAdd.setMovieBackground(mData.getString(backgroundIndex));

                NetworkUtils.MovieList.add(movieToAdd);

            } while (mData.moveToNext());

            mAdapter.setNumberItems(NetworkUtils.MovieList.size());
            mAdapter.notifyDataSetChanged();
            postersList.setVisibility(View.VISIBLE);
        }
    }

    private static class MyTaskParams {
        int pageIndex;
        URL url;

        MyTaskParams(int pageIndex, URL url) {
            this.pageIndex = pageIndex;
            this.url = url;
        }
    }
    public class TheMovieDBQueryTask extends AsyncTask<MyTaskParams, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            postersList.setVisibility(View.INVISIBLE);

        }

        @Override
        protected String doInBackground(MyTaskParams... params) {
            URL apiURL = params[0].url;
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
                    NetworkUtils.MovieList.clear();
                    JSONObject popularMoviesJSON = new JSONObject(tmdbResults);
                    JSONArray resultsArray = popularMoviesJSON.getJSONArray("results");
                    Movie movieToAdd;
                    for (int i = 0; i< resultsArray.length(); i++) {
                        JSONObject jsonobject = resultsArray.getJSONObject(i);
                        movieToAdd = new Movie(jsonobject);
                        NetworkUtils.MovieList.add(movieToAdd);
                    }

                    mAdapter.setNumberItems(NetworkUtils.MovieList.size());
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            postersList.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
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
            postersList.setVisibility(View.INVISIBLE);
            makePopularMoviesQuery(1);
            movieQuery = "popular";
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        } else if (itemThatWasClickedId == R.id.action_top_rated) {
            postersList.setVisibility(View.INVISIBLE);
            makeTopRatedMoviesQuery(1);
            movieQuery = "toprated";
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorites) {
            postersList.setVisibility(View.INVISIBLE);
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            movieQuery = "favorites";
            getFavoriteMovies();

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
