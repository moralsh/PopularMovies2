package org.moralsh.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;

import android.os.Bundle;
import android.os.AsyncTask;

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

import static org.moralsh.android.popularmovies.data.FavoritesContract.FavoritesEntry.CONTENT_URI;

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
//        postersList.setHasFixedSize(true);
        postersList.setAdapter(mAdapter);
        if ( NetworkUtils.isOnline(context)) {
            makePopularMoviesQuery(1);
        } else {
            postersList.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
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
            postersList.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);


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
            makePopularMoviesQuery(1);
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        } else if (itemThatWasClickedId == R.id.action_top_rated) {
            makeTopRatedMoviesQuery(1);
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorites) {
            mAdapter = new MovieAdapter(Movies.NUMBER_OF_POSTERS,this);
            postersList.setAdapter(mAdapter);
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
