/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

public class Movie {
    private int id;
    private String title;
    private String posterPath;
    private double rating;
    private String releaseDate;

    // Constructor
    public Movie(int id, String title, String posterPath, double rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getFullPosterPath() {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
