package controllers;


import java.util.ArrayList;
import java.util.List;

import models.Member;
import models.Movie;

public class MovieController {
	private List<Movie> movieStore;

    public MovieController(List<Movie> movieStore) {
        this.movieStore = movieStore;
    }
    // tìm kiếm phim
    public List<Movie> searchMovie(String keyword) {
        List<Movie> results = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) 
        	return new ArrayList<>(movieStore);
        String kw = keyword.toLowerCase();
        for (Movie m : movieStore) {
            if (m.getNameMovie().toLowerCase().contains(kw)
             || m.getDirector().toLowerCase().contains(kw)
             || m.getActor().toLowerCase().contains(kw)) {
                results.add(m);
            }
        }
        return results;
    }
    // kiểm tra loại phim
    public boolean playMovie(int movieId, Member user) {
        Movie movie = movieStore.stream()
            .filter(m -> m.getId() == movieId)
            .findFirst().orElse(null);
        if (movie == null) { 
        	System.out.println("Movie not found: " + movieId); 
        	return false; 
        	}
        if (movie.isVip() && !"VIP".equalsIgnoreCase(user.getAccountStatus())) {
            System.out.println("VIP required for: " + movie.getNameMovie());
            return false;
        }
        movie.watchMovie();
        return true;
    }
}
