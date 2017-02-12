package org.moralsh.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.moralsh.android.popularmovies.Movies;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        int index;

        mDisplayBackground = (ImageView) findViewById(R.id.iv_background_Image);
        mDisplayPosterDetail = (ImageView) findViewById(R.id.iv_poster_Image_detail);
        mDisplayTitle = (TextView) findViewById(R.id.tv_title_display);
        mDisplayData = (TextView) findViewById(R.id.tv_data_display);

        Context context = mDisplayBackground.getContext();
        Context context_poster = mDisplayPosterDetail.getContext();

        // Get the intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) { // Do we have data?

            // Get the index
            index = Integer.parseInt(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT));

            // Get the rest of the data
            if (Movies.getMovieTitle(index) != null) {
                Picasso.with(context).load(Movies.getMovieBackground(index)).into(mDisplayBackground);
                Picasso.with(context_poster).load(Movies.getMoviePosterURL(index)).into(mDisplayPosterDetail);
                mDisplayTitle.setText(Movies.getMovieTitle(index));
                mDisplayData.append("Overview: " + Movies.getMovieOverview(index) + "\n\n\n");
                mDisplayData.append("Release Date: " + Movies.getMovieReleaseDate(index) + "\n\n\n");
                mDisplayData.append("Average Rating: " + Movies.getMovieRating(index) + "\n\n\n");
            }
        }

    }
}
