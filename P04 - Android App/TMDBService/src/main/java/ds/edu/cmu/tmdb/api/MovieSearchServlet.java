package ds.edu.cmu.tmdb.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ds.edu.cmu.tmdb.log.LogEntry;
import ds.edu.cmu.tmdb.log.LoggingService;
import ds.edu.cmu.tmdb.model.Movie;
import ds.edu.cmu.tmdb.service.TMDBService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling movie search requests
 *
 * @author Your Name (your Andrew ID)
 */
@WebServlet("/api/search/movies")
public class MovieSearchServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MovieSearchServlet.class.getName());
    private static final Gson gson = new Gson();
    private final TMDBService tmdbService = new TMDBService();
    private final LoggingService loggingService = LoggingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Start timer for response time measurement
        long startTime = System.currentTimeMillis();

        // Initialize log entry
        LogEntry logEntry = new LogEntry();
        logEntry.setEndpoint("/api/movies/search");
        logEntry.setRequestMethod("GET");
        logEntry.setQueryParams(request.getQueryString());
        logEntry.setUserAgent(request.getHeader("User-Agent"));
        logEntry.setClientIp(request.getRemoteAddr());

        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get query parameter
        String query = request.getParameter("query");
        PrintWriter out = response.getWriter();

        // Validate input
        if (query == null || query.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Missing query parameter");
            out.print(gson.toJson(error));

            // Log error
            logEntry.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            logEntry.setResponseTime(System.currentTimeMillis() - startTime);
            logEntry.setResponseSize(error.toString().length());
            loggingService.logRequest(logEntry);

            return;
        }

        try {
            // Start timer for TMDB API request
            long tmdbStartTime = System.currentTimeMillis();

            // Call TMDB service to search for movies
            List<Movie> movies = tmdbService.searchMovies(query);

            // Calculate TMDB response time
            long tmdbResponseTime = System.currentTimeMillis() - tmdbStartTime;

            // Create response JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("query", query);
            jsonResponse.add("results", gson.toJsonTree(movies));

            // Set success status
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(jsonResponse));

            // Complete log entry
            logEntry.setStatusCode(HttpServletResponse.SC_OK);
            logEntry.setTmdbEndpoint("/search/movie");
            logEntry.setTmdbResponseCode(200); // Assuming success
            logEntry.setTmdbResponseTime(tmdbResponseTime);
            logEntry.setResponseSize(jsonResponse.toString().length());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching for movies", e);

            // Handle error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Failed to search for movies: " + e.getMessage());
            out.print(gson.toJson(error));

            // Log error
            logEntry.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logEntry.setTmdbResponseCode(0); // No response from TMDB
            logEntry.setResponseSize(error.toString().length());
        } finally {
            // Calculate total response time and log request
            logEntry.setResponseTime(System.currentTimeMillis() - startTime);
            loggingService.logRequest(logEntry);
        }
    }
}