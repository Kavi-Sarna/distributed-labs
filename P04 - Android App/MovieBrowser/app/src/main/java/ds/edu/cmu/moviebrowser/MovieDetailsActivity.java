/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView overviewTextView;
    private TextView genresTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView runtimeTextView;
    private ImageView backdropImageView;

    public static final String MOVIE_DETAILS_ENDPOINT = "/api/movies/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Initialize UI components
        titleTextView = findViewById(R.id.detailTitleTextView);
        overviewTextView = findViewById(R.id.detailOverviewTextView);
        genresTextView = findViewById(R.id.detailGenresTextView);
        releaseDateTextView = findViewById(R.id.detailReleaseDateTextView);
        ratingTextView = findViewById(R.id.detailRatingTextView);
        runtimeTextView = findViewById(R.id.detailRuntimeTextView);
        backdropImageView = findViewById(R.id.backdropImageView);

        // Get movie ID from intent
        int movieId = getIntent().getIntExtra("movie_id", -1);

        if (movieId != -1) {
            fetchMovieDetails(movieId);
        } else {
            Toast.makeText(this, "Error: Invalid movie ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchMovieDetails(int movieId) {
        // Construct the details URL with the movie ID and API key
        String detailsUrl = MOVIE_DETAILS_ENDPOINT + movieId;

        // Execute HTTP request in a background thread
        new BackgroundTask<String>(
                // doInBackground
                () -> HttpHandler.makeServiceCall(detailsUrl),
                // onPostExecute
                response -> {
                    if (response != null) {
                        parseDetailsResponse(response);
                    } else {
                        Toast.makeText(MovieDetailsActivity.this, "Error retrieving movie details", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        ).execute();
    }

    private void parseDetailsResponse(String jsonResponse) {
        try {
            JSONObject movieObject = new JSONObject(jsonResponse);

            // Extract movie details
            String title = movieObject.getString("title");
            String overview = movieObject.optString("overview", "No overview available");
            String releaseDate = movieObject.optString("releaseDate", "Unknown");
            double rating = movieObject.optDouble("rating", 0.0);
            int runtime = movieObject.optInt("runtime", 0);
            String backdropPath = movieObject.optString("posterPath", null);

            // Extract genres
            JSONArray genresArray = movieObject.getJSONArray("genres");
            StringBuilder genresBuilder = new StringBuilder();

            for (int i = 0; i < genresArray.length(); i++) {
                genresBuilder.append(genresArray.getString(i));

                if (i < genresArray.length() - 1) {
                    genresBuilder.append(", ");
                }
            }

            // Update UI
            titleTextView.setText(title);
            overviewTextView.setText(overview);
            genresTextView.setText("Genres: " + genresBuilder.toString());
            releaseDateTextView.setText("Released: " + releaseDate);
            ratingTextView.setText("Rating: " + rating + "/10");
            runtimeTextView.setText("Runtime: " + runtime + " min");

            // Load backdrop image
            if (backdropPath != null && !backdropPath.isEmpty()) {
                String backdropUrl = "https://image.tmdb.org/t/p/w780" + backdropPath;
                Picasso.get().load(backdropUrl)
                        .placeholder(R.drawable.backdrop_placeholder)
                        .error(R.drawable.backdrop_placeholder)
                        .into(backdropImageView);
            } else {
                backdropImageView.setImageResource(R.drawable.backdrop_placeholder);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing movie details", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
