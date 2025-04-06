package ds.edu.cmu.tmdb.service;

import com.google.gson.*;
import ds.edu.cmu.tmdb.model.Movie;
import ds.edu.cmu.tmdb.model.MovieDetail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for interacting with the TMDB API using Gson for JSON parsing
 *
 * @author Your Name (your Andrew ID)
 */
public class TMDBService {
    private static final Logger LOGGER = Logger.getLogger(TMDBService.class.getName());
    private static final Gson gson = new Gson();

    // TMDB API Configuration
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final String API_KEY = "00801da84f1d845e4713b0c9f29e76fa"; // Replace with your actual API key

    // API Endpoints
    private static final String SEARCH_ENDPOINT = "/search/movie";
    private static final String MOVIE_DETAILS_ENDPOINT = "/movie/";

    /**
     * Search for movies by query
     *
     * @param query The search query
     * @return List of movies matching the query
     * @throws IOException If an I/O error occurs
     */
    public List<Movie> searchMovies(String query) throws IOException {
        // Build URL with query parameters
        String urlString = API_BASE_URL + SEARCH_ENDPOINT
                + "?api_key=" + API_KEY
                + "&query=" + query
                + "&language=en-US";

        // Make API request
        String jsonResponse = makeApiRequest(urlString);

        // Parse JSON response using Gson
        List<Movie> movies = new ArrayList<>();

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray results = jsonObject.getAsJsonArray("results");

        for (JsonElement element : results) {
            JsonObject movieJson = element.getAsJsonObject();

            int id = movieJson.get("id").getAsInt();
            String title = movieJson.get("title").getAsString();

            // Handle optional fields with defaults
            String releaseDate = movieJson.has("release_date") && !movieJson.get("release_date").isJsonNull()
                    ? movieJson.get("release_date").getAsString() : "";

            String posterPath = movieJson.has("poster_path") && !movieJson.get("poster_path").isJsonNull()
                    ? movieJson.get("poster_path").getAsString() : "";

            double rating = movieJson.has("vote_average")
                    ? movieJson.get("vote_average").getAsDouble() : 0.0;

            Movie movie = new Movie(id, title, releaseDate, posterPath, rating);
            movies.add(movie);
        }

        return movies;
    }

    /**
     * Get detailed information about a movie
     *
     * @param movieId The ID of the movie
     * @return MovieDetail object containing detailed information
     * @throws IOException If an I/O error occurs
     */
    public MovieDetail getMovieDetails(int movieId) throws IOException {
        // Build URL with query parameters
        String urlString = API_BASE_URL + MOVIE_DETAILS_ENDPOINT + movieId
                + "?api_key=" + API_KEY
                + "&language=en-US";

        // Make API request
        String jsonResponse = makeApiRequest(urlString);

        // Parse JSON response using Gson
        JsonObject movieJson = JsonParser.parseString(jsonResponse).getAsJsonObject();

        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setId(movieJson.get("id").getAsInt());
        movieDetail.setTitle(movieJson.get("title").getAsString());

        // Handle optional fields
        if (movieJson.has("overview") && !movieJson.get("overview").isJsonNull()) {
            movieDetail.setOverview(movieJson.get("overview").getAsString());
        }

        if (movieJson.has("release_date") && !movieJson.get("release_date").isJsonNull()) {
            movieDetail.setReleaseDate(movieJson.get("release_date").getAsString());
        }

        if (movieJson.has("vote_average")) {
            movieDetail.setRating(movieJson.get("vote_average").getAsDouble());
        }

        if (movieJson.has("poster_path") && !movieJson.get("poster_path").isJsonNull()) {
            movieDetail.setPosterPath(movieJson.get("poster_path").getAsString());
        }

        if (movieJson.has("runtime") && !movieJson.get("runtime").isJsonNull()) {
            movieDetail.setRuntime(movieJson.get("runtime").getAsInt());
        }

        // Extract genres
        List<String> genres = new ArrayList<>();
        if (movieJson.has("genres") && !movieJson.get("genres").isJsonNull()) {
            JsonArray genresArray = movieJson.getAsJsonArray("genres");
            for (JsonElement element : genresArray) {
                JsonObject genre = element.getAsJsonObject();
                genres.add(genre.get("name").getAsString());
            }
        }
        movieDetail.setGenres(genres);

        return movieDetail;
    }

    /**
     * Make an HTTP request to the TMDB API
     *
     * @param urlString The URL to request
     * @return The JSON response as a String
     * @throws IOException If an I/O error occurs
     */
    private String makeApiRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Get response code
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new IOException(responseMessage);

        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
