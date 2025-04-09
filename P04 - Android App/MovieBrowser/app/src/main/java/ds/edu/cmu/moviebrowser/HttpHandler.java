/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    private static final String BASE_URL = "https://expert-space-parakeet-wwqjg5vwwr635qwr-8080.app.github.dev";


    // Make an HTTP GET request to the specified URL and return the response as a string
    public static String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            System.out.println("Request URL - " + BASE_URL + reqUrl);
            URL url = new URL(BASE_URL + reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            // Create connection
            conn.connect();

            int responseCode = conn.getResponseCode();
            System.out.println("Response code - " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = readStream(conn.getInputStream());
            } else {
                response = readStream(conn.getErrorStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    // Read an input stream and convert it to a string
    private static String readStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }

        reader.close();
        return sb.toString();
    }
}
