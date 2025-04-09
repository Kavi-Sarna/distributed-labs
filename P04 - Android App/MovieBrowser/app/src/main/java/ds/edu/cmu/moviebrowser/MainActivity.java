/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Application code built with GPT.
 * Prompt:
 * "Implement a native Android application
 * a. Has at least three different kinds of Views in your Layout
 *      (TextView, EditText, ImageView, or anything that extends
 *      android.view.View). In order to figure out if something is a View,
 *      find its API. If it extends android.view.View then it is a View.
 * b. Requires input from the user
 * c. Makes an HTTP request (using an appropriate HTTP method)
 *      to your web service. Note that this request must be done using a
 *      background thread (see Lab 8's use of BackgroundTask)
 * d. Receives and parses an XML or JSON formatted reply from your web service
 * e. Displays new information to the user
 * f. Is repeatable (I.e. the user can repeatedly reuse the application without restarting it.)
 *
 * Using the above metrics, build an app for showing movie details to the users.
 * The app will have a main landing page where users can search for a movie,
 * this will connect to the tmdb service and get a list of movies,
 * and show them on the screen.
 * When user click on any of those, it makes another request to the tmdb service
 * to get the details of that movie and show it to the user."
 */
public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private ListView moviesListView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;

    // API Endpoints
    public static final String SEARCH_ENDPOINT = "/api/search/movies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        moviesListView = findViewById(R.id.moviesListView);

        // Initialize movie list and adapter
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
        moviesListView.setAdapter(movieAdapter);

        // Set click listener for search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMovies(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for list items
        moviesListView.setOnItemClickListener((parent, view, position, id) -> {
            Movie selectedMovie = movieList.get(position);
            openMovieDetails(selectedMovie.getId());
        });
    }

    private void searchMovies(String query) {
        // Construct the search URL with the query and API key
        String searchUrl = SEARCH_ENDPOINT + "?query=" + query;

        // Execute HTTP request in a background thread
        new BackgroundTask<String>(
                // doInBackground
                () -> HttpHandler.makeServiceCall(searchUrl),
                // onPostExecute
                response -> {
                    if (response != null) {
                        parseSearchResponse(response);
                    } else {
                        Toast.makeText(MainActivity.this, "Error retrieving data", Toast.LENGTH_LONG).show();
                    }
                }
        ).execute();
    }

    private void parseSearchResponse(String jsonResponse) {
        try {
            // Clear previous results
            movieList.clear();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            // Add movies to list
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);

                int id = movieObject.getInt("id");
                String title = movieObject.getString("title");
                String posterPath = movieObject.optString("posterPath", null);
                double rating = movieObject.optDouble("rating", 0.0);
                String releaseDate = movieObject.optString("releaseDate", "Unknown");

                Movie movie = new Movie(id, title, posterPath, rating, releaseDate);
                movieList.add(movie);
            }

            // Update the adapter
            movieAdapter.notifyDataSetChanged();

            if (movieList.isEmpty()) {
                Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_LONG).show();
        }
    }

    private void openMovieDetails(int movieId) {
        // Navigate to MovieDetailsActivity with the movie ID
        android.content.Intent intent = new android.content.Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie_id", movieId);
        startActivity(intent);
    }
}
