package ds.edu.cmu.tmdb.api;

import ds.edu.cmu.tmdb.log.LogEntry;
import ds.edu.cmu.tmdb.log.LoggingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet  extends HttpServlet {

    private final LoggingService loggingService = LoggingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch analytics data
        List<LogEntry> logs = loggingService.getAllLogs();
        request.setAttribute("logs", logs);
        request.setAttribute("totalRequests", logs.size());

        Map<String, Integer> topQueries = loggingService.getTopSearchQueries(1);
        topQueries.forEach((k, v) -> {
                    request.setAttribute("topQueryKey", k);
                    request.setAttribute("topQueryValue", v);
                });

        double averageTmdbResponseTime = loggingService.getAverageTmdbResponseTime();
        request.setAttribute("averageTmdbResponseTime", averageTmdbResponseTime);

//        Map<String, Integer> deviceDistribution = loggingService.getDeviceDistribution(1);

//        Map<String, Integer> dailyRequestCounts = loggingService.getDailyRequestCounts();

//        request.setAttribute("logs", logs);

        // Forward to the JSP page
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
