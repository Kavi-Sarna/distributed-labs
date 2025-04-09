/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        super(context, R.layout.movie_list_item, movieList);
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        }

        Movie currentMovie = movieList.get(position);

        // Set up the movie title
        TextView titleTextView = listItem.findViewById(R.id.movieTitleTextView);
        titleTextView.setText(currentMovie.getTitle());

        // Set up the release date
        TextView releaseDateTextView = listItem.findViewById(R.id.releaseDateTextView);
        releaseDateTextView.setText("Released: " + currentMovie.getReleaseDate());

        // Set up the rating
        TextView ratingTextView = listItem.findViewById(R.id.ratingTextView);
        ratingTextView.setText("Rating: " + currentMovie.getRating() + "/10");

        // Set up the poster image
        ImageView posterImageView = listItem.findViewById(R.id.posterImageView);
        String posterUrl = currentMovie.getFullPosterPath();

        if (posterUrl != null) {
            Picasso.get().load(posterUrl)
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_placeholder)
                    .into(posterImageView);
        } else {
            posterImageView.setImageResource(R.drawable.movie_placeholder);
        }

        return listItem;
    }
}
