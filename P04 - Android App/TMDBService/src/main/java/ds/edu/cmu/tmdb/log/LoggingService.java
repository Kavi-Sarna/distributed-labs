package ds.edu.cmu.tmdb.log;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for logging data to MongoDB
 *
 * @author Your Name (your Andrew ID)
 */
public class LoggingService {
    private static final Logger LOGGER = Logger.getLogger(LoggingService.class.getName());

    // MongoDB Configuration
    private static final String CONNECTION_STRING = "mongodb://ksarna:kavicmutest@cluster0-shard-00-00.pthyg.mongodb.net:27017,cluster0-shard-00-01.pthyg.mongodb.net:27017,cluster0-shard-00-02.pthyg.mongodb.net:27017/?replicaSet=atlas-54mxil-shard-0&ssl=true&authSource=admin&retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "tmdb_app";
    private static final String COLLECTION_NAME = "request_logs";

    private static LoggingService instance;
    private  MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Private constructor for singleton pattern
     */
    private LoggingService() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);

            // Connect to database (if it doesn't exist, MongoDB will create it)
//            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);

        // try {
            // Initialize MongoDB client
//            ConnectionString connString = new ConnectionString(CONNECTION_STRING);
//            MongoClientSettings settings = MongoClientSettings.builder()
//                    .applyConnectionString(connString)
//                    .build();
//            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(DATABASE_NAME);
            collection = database.getCollection(COLLECTION_NAME);

            LOGGER.info("MongoDB connection initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize MongoDB connection", e);
        }
    }

    /**
     * Get singleton instance
     *
     * @return LoggingService instance
     */
    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    /**
     * Log a request to MongoDB
     *
     * @param logEntry The log entry to save
     */
    public void logRequest(LogEntry logEntry) {
        try {
            Document doc = new Document()
                    .append("timestamp", logEntry.getTimestamp())
                    .append("endpoint", logEntry.getEndpoint())
                    .append("requestMethod", logEntry.getRequestMethod())
                    .append("queryParams", logEntry.getQueryParams())
                    .append("userAgent", logEntry.getUserAgent())
                    .append("clientIp", logEntry.getClientIp())
                    .append("responseTime", logEntry.getResponseTime())
                    .append("statusCode", logEntry.getStatusCode())
                    .append("tmdbEndpoint", logEntry.getTmdbEndpoint())
                    .append("tmdbResponseCode", logEntry.getTmdbResponseCode())
                    .append("tmdbResponseTime", logEntry.getTmdbResponseTime())
                    .append("responseSize", logEntry.getResponseSize());

            collection.insertOne(doc);
            LOGGER.info("Logged request: " + logEntry.getEndpoint());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to log request", e);
        }
    }

    /**
     * Get all log entries
     *
     * @return List of log entries
     */
    public List<LogEntry> getAllLogs() {
        List<LogEntry> logs = new ArrayList<>();

        try {
            collection.find()
                    .sort(Sorts.descending("timestamp"))
                    .forEach(doc -> logs.add(documentToLogEntry(doc)));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve logs", e);
        }

        return logs;
    }

    /**
     * Get top search queries by occurrence
     *
     * @param limit Maximum number of results
     * @return Map of query to count
     */
    public Map<String, Integer> getTopSearchQueries(int limit) {
        Map<String, Integer> queryCount = new HashMap<>();

        try {
            collection.find(Filters.eq("endpoint", "/api/movies/search"))
                    .forEach(doc -> {
                        String query = doc.getString("queryParams");
                        if (query != null && query.contains("query=")) {
                            query = query.substring(query.indexOf("query=") + 6);
                            if (query.contains("&")) {
                                query = query.substring(0, query.indexOf("&"));
                            }
                            queryCount.put(query, queryCount.getOrDefault(query, 0) + 1);
                        }
                    });

            // Sort by count and limit results
            return sortByValueAndLimit(queryCount, limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get top search queries", e);
            return queryCount;
        }
    }

    /**
     * Get average response time for TMDB API calls
     *
     * @return Average response time in milliseconds
     */
    public double getAverageTmdbResponseTime() {
        try {
            long totalTime = 0;
            long count = 0;

            for (Document doc : collection.find()) {
                if (doc.containsKey("tmdbResponseTime")) {
                    totalTime += doc.getLong("tmdbResponseTime");
                    count++;
                }
            }

            return count > 0 ? (double) totalTime / count : 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to calculate average response time", e);
            return 0;
        }
    }

    /**
     * Get device distribution
     *
     * @param limit Maximum number of results
     * @return Map of device model to count
     */
    public Map<String, Integer> getDeviceDistribution(int limit) {
        Map<String, Integer> deviceCount = new HashMap<>();

        try {
            collection.find().forEach(doc -> {
                String userAgent = doc.getString("userAgent");
                if (userAgent != null) {
                    // Extract device model from user agent
                    String device = parseDeviceFromUserAgent(userAgent);
                    deviceCount.put(device, deviceCount.getOrDefault(device, 0) + 1);
                }
            });

            // Sort by count and limit results
            return sortByValueAndLimit(deviceCount, limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get device distribution", e);
            return deviceCount;
        }
    }

    /**
     * Get daily request counts for the past week
     *
     * @return Map of date to request count
     */
    public Map<String, Integer> getDailyRequestCounts() {
        Map<String, Integer> dailyCounts = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Get current date and calculate 7 days ago
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -6); // Start from 6 days ago (7 days total including today)

            // Initialize map with dates for the past week
            for (int i = 0; i < 7; i++) {
                dailyCounts.put(dateFormat.format(cal.getTime()), 0);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Query logs
            collection.find().forEach(doc -> {
                Date timestamp = doc.getDate("timestamp");
                String dateStr = dateFormat.format(timestamp);

                if (dailyCounts.containsKey(dateStr)) {
                    dailyCounts.put(dateStr, dailyCounts.get(dateStr) + 1);
                }
            });

            return dailyCounts;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get daily request counts", e);
            return dailyCounts;
        }
    }

    /**
     * Sort a map by value (descending) and limit the number of entries
     *
     * @param map The map to sort
     * @param limit Maximum number of entries to return
     * @return Sorted and limited map
     */
    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueAndLimit(Map<K, V> map, int limit) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<K, V> entry : list) {
            if (count >= limit) break;
            result.put(entry.getKey(), entry.getValue());
            count++;
        }

        return result;
    }

    /**
     * Convert MongoDB document to LogEntry object
     *
     * @param doc MongoDB document
     * @return LogEntry object
     */
    private LogEntry documentToLogEntry(Document doc) {
        LogEntry entry = new LogEntry();

        entry.setId(doc.getObjectId("_id").toString());
        entry.setTimestamp(doc.getDate("timestamp"));
        entry.setEndpoint(doc.getString("endpoint"));
        entry.setRequestMethod(doc.getString("requestMethod"));
        entry.setQueryParams(doc.getString("queryParams"));
        entry.setUserAgent(doc.getString("userAgent"));
        entry.setClientIp(doc.getString("clientIp"));
        entry.setResponseTime(doc.getLong("responseTime"));
        entry.setStatusCode(doc.getInteger("statusCode"));
        entry.setTmdbEndpoint(doc.getString("tmdbEndpoint"));
        entry.setTmdbResponseCode(doc.getInteger("tmdbResponseCode"));
        entry.setTmdbResponseTime(doc.getLong("tmdbResponseTime"));
        entry.setResponseSize(doc.getInteger("responseSize"));

        return entry;
    }

    /**
     * Extract device model from user agent string
     * Basic parsing, could be improved with a dedicated user agent parser library
     *
     * @param userAgent User agent string
     * @return Device model or "Unknown"
     */
    private String parseDeviceFromUserAgent(String userAgent) {
        // Extract device model from Android user agent
        // Example: "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 ..."
        try {
            if (userAgent.contains("Android")) {
                int startIndex = userAgent.indexOf("Android");
                startIndex = userAgent.indexOf(";", startIndex) + 1;
                int endIndex = userAgent.indexOf(")", startIndex);

                if (startIndex > 0 && endIndex > startIndex) {
                    return userAgent.substring(startIndex, endIndex).trim();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse device from user agent", e);
        }

        return "Unknown Device";
    }

    /**
     * Close MongoDB connection when application shuts down
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            LOGGER.info("MongoDB connection closed");
        }
    }
}
