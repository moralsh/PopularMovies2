/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moralsh.android.popularmovies.utilities;

import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.content.Context;

import org.moralsh.android.popularmovies.BuildConfig;
import org.moralsh.android.popularmovies.MainActivity;
import org.moralsh.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {


    // Some useful constants

    final static String THEMOVIEDB_BASE_URL =
            "http://api.themoviedb.org/3";
    final static String  POPULAR_MOVIES = "/movie/popular";
    final static String  TOP_RATED_MOVIES = "/movie/top_rated";
    final static String  MOVIE_DETAIL = "/movie/";

    final static String PARAM_QUERY = "api_key";

    final static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    final static String POSTER_SIZE = "w185";

    private static String TheMovieDBApiKey = BuildConfig.THEMOVIEDB_API_KEY;
    /**
     * Builds the URL used to query Popular movies.
     *
     * @return The URL to use to query TMDB server.
     */
    public static URL buildPopularUrl() {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL + POPULAR_MOVIES).buildUpon()
                .appendQueryParameter(PARAM_QUERY, TheMovieDBApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query Top Rated movies.
     *
     * @return The URL to use to query TMDB server.
     */
    public static URL buildTopRatedUrl() {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL + TOP_RATED_MOVIES).buildUpon()
                .appendQueryParameter(PARAM_QUERY, TheMovieDBApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query detail on a particular movie. Not used yet
     *
     * @param movieId The movie ID on which to return details
     * @return The URL to use to query TMDB server.
     */
    public static URL buildMovieDetailUrl(int movieId) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL + MOVIE_DETAIL + movieId).buildUpon()
                .appendQueryParameter(PARAM_QUERY, TheMovieDBApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL of the poster (or background) Image
     *
     * @param posterImage The image name we append to get the full poster image URL
     * @return The URL of the image.
     */
    public static URL buildPosterImageUrl(String posterImage) {
        Uri builtUri = Uri.parse(POSTER_BASE_URL + POSTER_SIZE + posterImage).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL of the poster (or background) Image. Overload to be able to get a different size
     *
     * @param posterImage The image name we append to get the full poster image URL
     * @param posterSize The size of the poster (w185, w500...)
     * @return The URL of the image.
     */
    public static URL buildPosterImageUrl(String posterImage, String posterSize) {
        Uri builtUri = Uri.parse(POSTER_BASE_URL + posterSize + posterImage).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}