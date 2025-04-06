package ds.edu.cmu.tmdb.model;

import java.util.List;

/**
 * Detailed movie model
 *
 * @author Your Name (your Andrew ID)
 */
public class MovieDetail {
    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private double rating;
    private String posterPath;
    private List<String> genres;
    private int runtime;

    public MovieDetail() {
        // Default constructor
    }

    public MovieDetail(int id, String title, String overview, String releaseDate,
                       double rating, String posterPath, List<String> genres, int runtime) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.posterPath = posterPath;
        this.genres = genres;
        this.runtime = runtime;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public String toString() {
        return "MovieDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", rating=" + rating +
                ", genres=" + genres +
                ", runtime=" + runtime +
                '}';
    }
}
