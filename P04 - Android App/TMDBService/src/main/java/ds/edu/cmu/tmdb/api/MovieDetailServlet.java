package ds.edu.cmu.tmdb.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ds.edu.cmu.tmdb.log.LogEntry;
import ds.edu.cmu.tmdb.log.LoggingService;
import ds.edu.cmu.tmdb.model.MovieDetail;
import ds.edu.cmu.tmdb.service.TMDBService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling movie detail requests
 *
 * @author Your Name (your Andrew ID)
 */
@WebServlet("/api/movies/*")
public class MovieDetailServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MovieDetailServlet.class.getName());
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
        logEntry.setEndpoint("/api/movies");
        logEntry.setRequestMethod("GET");
        logEntry.setQueryParams(request.getQueryString());
        logEntry.setUserAgent(request.getHeader("User-Agent"));
        logEntry.setClientIp(request.getRemoteAddr());

        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // Parse movie ID from path
        String pathInfo = request.getPathInfo();

        // Validate path
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Missing movie ID");
            out.print(gson.toJson(error));

            // Log error
            logEntry.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            logEntry.setResponseTime(System.currentTimeMillis() - startTime);
            logEntry.setResponseSize(error.toString().length());
            loggingService.logRequest(logEntry);

            return;
        }

        // Extract movie ID from path
        String movieIdStr = pathInfo.substring(1);
        int movieId;

        // Validate movie ID
        try {
            movieId = Integer.parseInt(movieIdStr);

            logEntry.setQueryParams(movieIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Invalid movie ID format");
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

            // Call TMDB service to get movie details
            MovieDetail movieDetail = tmdbService.getMovieDetails(movieId);

            // Calculate TMDB response time
            long tmdbResponseTime = System.currentTimeMillis() - tmdbStartTime;

            // Set success status
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(movieDetail));

            // Complete log entry
            logEntry.setStatusCode(HttpServletResponse.SC_OK);
            logEntry.setTmdbEndpoint("/movie/" + movieId);
            logEntry.setTmdbResponseCode(200); // Assuming success
            logEntry.setTmdbResponseTime(tmdbResponseTime);
            logEntry.setTmdbResponseJSON(movieDetail.toString());
            logEntry.setResponseSize(gson.toJson(movieDetail).length());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving movie details", e);

            // Handle error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Failed to retrieve movie details: " + e.getMessage());
            out.print(gson.toJson(error));

            // Log error
            logEntry.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logEntry.setTmdbResponseCode(0); // No response from TMDB
            logEntry.setTmdbResponseJSON(e.getMessage());
            logEntry.setResponseSize(error.toString().length());
        } finally {
            // Calculate total response time and log request
            logEntry.setResponseTime(System.currentTimeMillis() - startTime);
            loggingService.logRequest(logEntry);
        }
    }
}
