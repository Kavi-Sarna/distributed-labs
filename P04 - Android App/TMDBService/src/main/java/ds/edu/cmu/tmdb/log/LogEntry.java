package ds.edu.cmu.tmdb.log;

import java.util.Date;

/**
 * Model for log entries to be stored in MongoDB
 *
 * @author Your Name (your Andrew ID)
 */
public class LogEntry {
    private String id;               // MongoDB document ID
    private Date timestamp;          // When the request was received
    private String endpoint;         // API endpoint called
    private String requestMethod;    // HTTP method (GET, POST, etc.)
    private String queryParams;      // Query parameters in the request
    private String userAgent;        // User agent (includes device model)
    private String clientIp;         // Client IP address
    private long responseTime;       // Response time in milliseconds
    private int statusCode;          // HTTP status code of response
    private String tmdbEndpoint;     // TMDB API endpoint called
    private int tmdbResponseCode;    // Response code from TMDB API
    private long tmdbResponseTime;   // Response time from TMDB API in milliseconds
    private int responseSize;        // Size of response in bytes

    public LogEntry() {
        this.timestamp = new Date();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTmdbEndpoint() {
        return tmdbEndpoint;
    }

    public void setTmdbEndpoint(String tmdbEndpoint) {
        this.tmdbEndpoint = tmdbEndpoint;
    }

    public int getTmdbResponseCode() {
        return tmdbResponseCode;
    }

    public void setTmdbResponseCode(int tmdbResponseCode) {
        this.tmdbResponseCode = tmdbResponseCode;
    }

    public long getTmdbResponseTime() {
        return tmdbResponseTime;
    }

    public void setTmdbResponseTime(long tmdbResponseTime) {
        this.tmdbResponseTime = tmdbResponseTime;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", endpoint='" + endpoint + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", queryParams='" + queryParams + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", responseTime=" + responseTime +
                ", statusCode=" + statusCode +
                ", tmdbEndpoint='" + tmdbEndpoint + '\'' +
                ", tmdbResponseCode=" + tmdbResponseCode +
                ", tmdbResponseTime=" + tmdbResponseTime +
                ", responseSize=" + responseSize +
                '}';
    }
}
