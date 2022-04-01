package org.factoriaf5.filmcity.controllers;

import org.factoriaf5.filmcity.domain.Movie;
import org.factoriaf5.filmcity.repositories.MovieRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MoviesController {

    private final MovieRepository movieRepository;
    private String id;

    @Autowired
    MoviesController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies")
    public List<Movie> allMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/movies/{id}")
    public Movie findMovie(@PathVariable Long id) {
        return movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
    }

    @PostMapping("/movies")
    public Movie addMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @PutMapping("/movies")
    public Movie updateMovieById(@RequestBody @NotNull Movie movie) {
        movieRepository.findById(movie.getId()).orElseThrow(MovieNotFoundException::new);
        return movieRepository.save(movie);
    }
    @PutMapping("/movies/{id}/book")
    public Movie updateMovieRented(@PathVariable Long id, @RequestParam (value = "renter")String renter) {
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.setRenter(renter);
        movie.setBooked(true);
        return movieRepository.save(movie);
    }

    @PutMapping("/movies/{id}/return")
    public Movie clearMovieRented(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movie.setRenter(null);
        movie.setBooked(true);
        return movieRepository.save(movie);
    }

    @PutMapping("/movies/{id}/rating")
    public Movie updateRatingById(@PathVariable Long id,@RequestBody Movie movie){
        Movie movieToEdit = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        int newScore = movie.getScore();
        if (newScore >= 0 && newScore <= 10) {
            movieToEdit.setScore(newScore);
        } else{
            movieToEdit.setScore(0);
            throw new IllegalArgumentException("Please enter number between 0 and 10");
        }
        return movieRepository.save(movieToEdit);
    }


    @DeleteMapping("/movies/{id}")
    public Movie deleteMovieById(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
        movieRepository.deleteById(id);
        return movie;
    }

}