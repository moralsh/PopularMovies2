package org.moralsh.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;
import org.moralsh.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by morals on 31/01/17.
 */

public class Movie {
    // Arrays to store the data
    private String moviePosterURL;
    private String movieTitle;
    private String movieOverview;
    private String movieBackground;
    private String movieReleaseDate;
    private Double movieRating;
    private int movieId;

    public String getMoviePosterURL() {
        return moviePosterURL;
    }

    public void setMoviePosterURL(String moviePosterURL) {
        this.moviePosterURL = moviePosterURL;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getMovieBackground() {
        return movieBackground;
    }

    public void setMovieBackground(String movieBackground) {
        this.movieBackground = movieBackground;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(Double movieRating) {
        this.movieRating = movieRating;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return getMovieTitle();
    }

    public Movie() {}
    public Movie(JSONObject json) {
        jsonParse(json);
    }

    public class Reviews {

    }

    public class Videos {

    }



    public  void jsonParse(JSONObject json) {
        try {
            setMovieTitle(json.getString("title"));
            setMovieOverview(json.getString("overview"));
            setMovieReleaseDate(json.getString("release_date"));
            setMovieRating(json.getDouble("vote_average"));
            setMovieId(json.getInt("id"));
            setMoviePosterURL(NetworkUtils.buildPosterImageUrl(json.getString("poster_path")).toString());
            setMovieBackground(NetworkUtils.buildPosterImageUrl(json.getString("backdrop_path")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
