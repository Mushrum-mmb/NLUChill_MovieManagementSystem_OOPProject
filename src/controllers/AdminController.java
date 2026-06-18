
package controllers;

import java.util.List;

import models.Admin;
import models.Member;
import models.Movie;

public class AdminController {

    private Admin admin;
    private List<Movie> movies;
    private List<Member> users;

    public AdminController(
            Admin admin,
            List<Movie> movies,
            List<Member> users) {

        this.admin = admin;
        this.movies = movies;
        this.users = users;
    }

    // ===== MOVIE =====

    public List<Movie> getAllMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {

        movies.add(movie);

        admin.addMovie(movie);

        movie.notify(movie.getNameMovie());
    }

    public void deleteMovie(int row) {

        if(row < 0 || row >= movies.size())
            return;

        Movie movie = movies.get(row);

        admin.deleteMovie(movie);
    }

    public void updateMovie(
            int row,
            String name,
            String director,
            String actor,
            String country,
            boolean vip) {

        if(row < 0 || row >= movies.size())
            return;

        Movie movie = movies.get(row);

        movie.setNameMovie(name);
        movie.setDirector(director);
        movie.setActor(actor);
        movie.setCountry(country);
        movie.setVip(vip);
    }

    // ===== USER =====

    public List<Member> getAllUsers() {
        return users;
    }

    public void lockUser(int row) {

        if(row < 0 || row >= users.size())
            return;

        admin.lockAccount(users.get(row));
    }

    public void unlockUser(int row) {

        if(row < 0 || row >= users.size())
            return;

        admin.UnlockAccount(users.get(row));
    }

    public void warnUser(
            int row,
            String reason) {

        if(row < 0 || row >= users.size())
            return;

        admin.WarnUser(users.get(row), reason);
    }
}