package org.moralsh.android.popularmovies;

/**
 * Created by morals on 31/01/17.
 */

public class Movies {

    // The number of posters the service retrieves, will change it for the length in the future
    final static int NUMBER_OF_POSTERS = 20;

    // Arrays to store the data
    private static String[] moviePosterURL = new String[NUMBER_OF_POSTERS];
    private static String[] movieTitle = new String[NUMBER_OF_POSTERS];
    private static String[] movieOverview = new String[NUMBER_OF_POSTERS];
    private static String[] movieBackground = new String[NUMBER_OF_POSTERS];
    private static String[] movieReleaseDate = new String[NUMBER_OF_POSTERS];
    private static Double[] movieRating = new Double[NUMBER_OF_POSTERS];
    private static int[] movieId = new int[NUMBER_OF_POSTERS];

    // Methods to acccess/modify the arrays, self explanatory

    public static void setMoviePosterURL(String[] url) {
        moviePosterURL = url;
    }
    public String[] getMoviePosterURL() {
        return moviePosterURL;
    }

    public static void setMovieTitle(String[] title) {
        movieTitle = title;
    }
    public String[] getMovieTitle() {
        return movieTitle;
    }

    public static void setMovieOverview(String[] overview) {
        movieOverview = overview;
    }
    public String[] getMovieOverview() {
        return movieOverview;
    }

    public static void setMovieBackground(String[] background) {
        movieBackground = background;
    }
    public String[] getMovieBackground() {
        return movieBackground;
    }

    public static void setMovieReleaseDate(String[] releaseDate) {
        movieReleaseDate = releaseDate;
    }
    public String[] getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public static void setMovieRating(Double[] rating) {
        movieRating = rating;
    }
    public Double[] getMovieRating() {
        return movieRating;
    }

    public static String getMovieTitle(int index) {
        return movieTitle[index];
    }

    public static String getMoviePosterURL(int index) {
        return moviePosterURL[index];
    }

    public static String getMovieBackground(int index) {
        return movieBackground[index];
    }

    public static String getMovieOverview(int index) {
        return movieOverview[index];
    }

    public static String getMovieReleaseDate(int index) {
        return movieReleaseDate[index];
    }

    public static Double getMovieRating(int index) {
        return movieRating[index];
    }

    public void setMovieId(int[] id) {
        movieId = id;
    }
}
