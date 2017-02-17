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
        private String id;
        private String author;
        private String content;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public  void jsonParse(JSONObject json) {
            try {
                setId(json.getString("id"));
                setAuthor(json.getString("author"));
                setContent(json.getString("content"));
                setUrl(json.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class Videos {
        private String id;
        private String iso_639_1;
        private String iso_3166_1;
        private String key;
        private String name;
        private String site;
        private int size;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public  void jsonParse(JSONObject json) {
            try {
                setId(json.getString("id"));
                setIso_639_1(json.getString("iso_639_1"));
                setIso_3166_1(json.getString("iso_4166"));
                setKey(json.getString("key"));
                setName(json.getString("name"));
                setSite(json.getString("site"));
                setSize(json.getInt("size"));
                setType(json.getString("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
