package org.factoriaf5.filmcity;

import org.factoriaf5.filmcity.domain.Movie;
import org.factoriaf5.filmcity.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MovieRepository movieRepository;


    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }
    @Test
    void returnsTheExistingCoders() throws Exception {

        addTestMovies();

        mockMvc.perform(get("/movies"));
        Movie movie = movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg", "Steven Spielberg", 1993, "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."));

        mockMvc.perform(get("/movies/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Jurassic Park")))
                .andExpect(jsonPath("$.coverImage", equalTo("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg")))
                .andExpect(jsonPath("$.director", equalTo("Steven Spielberg")))
                .andExpect(jsonPath("$.year", equalTo(1993)))
                .andExpect(jsonPath("$.synopsis", equalTo("A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.")));
    }

    @Test
    void allowsToCreateANewMovie() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"  +
                        "\"title\": \"Jurassic Park\", " +
                        "\"coverImage\": \"https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg\", " +
                        "\"director\": \"Steven Spielberg\", " +
                        "\"year\": \"1993\", " +
                        "\"synopsis\": \"A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.\" " +
                        "}")

        ).andExpect(status().isOk());

        List<Movie> movies = movieRepository.findAll();
        assertThat(movies, contains(allOf(
                hasProperty("title", is("Jurassic Park")),
                hasProperty("coverImage", is("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg")),
                hasProperty("director", is("Steven Spielberg")),
                hasProperty("year", is(1993)),
                hasProperty("synopsis", is("A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."))
        )));
    }

    @Test
    void allowsToFindACoderById() throws Exception {

        Movie Movies = movieRepository.save(new Movie("Mohamed", "Aberkani", "hhane@example.org", 2010, "Java"));

        mockMvc.perform(get("/movies/" + Movies.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title", is ("")))
                .andExpect(jsonPath("coverImage", is("")))
                .andExpect(jsonPath("director", is("")))
                .andExpect(jsonPath("", is("")))
                .andExpect(jsonPath("", is("")));
    }

    @Test
    void returnsAnErrorIfTryingToGetACoderThatDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void allowsToDeleteAMovieById() throws Exception {

        Movie movie = null;
        mockMvc.perform(get("/movies/"+ movie.getId()))
                .andExpect(status().isOk());

        List<Movie> Movies = movieRepository.findAll();
        assertThat(Movies, not(contains(allOf(
                hasProperty("firstName", is("Mohamed")),
                hasProperty("lastName", is("Aberkani"))
        ))));
    }

    @Test
    void returnsAnErrorIfTryingToDeleteAMovieThatDoesNotExist() throws Exception {
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void allowsToModifyAMovie() throws Exception {
        Movie movie = movieRepository.save(new Movie("Mohamed", "Aberkani", "hhane@example.org", 2001, "Java"));

        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +

                        "\"title\": \"Jurrasic Park\", " +
                        "\"coverImage\": \"\", " +
                        "\"director\": \"Steven Spielberg\", " +
                        "\"year\": \"1993\" }")
        ).andExpect(status().isOk());

        List<Movie> Movies = movieRepository.findAll();

        assertThat(Movies, hasSize(1));
        assertThat(Movies.get(0).getFirstName(), equalTo("Andrea"));

    }

    @Test
    void returnsAnErrorWhenTryingToModifyAMovieThatDoesNotExist() throws Exception {
        addTestMovies();

        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"title\": \"Jurrasic Park\", " +
                        "\"coverImage\": \"\", " +
                        "\"director\": \"Steven Spielberg\", " +
                        "\"year\": \"1993\" }")
        ).andExpect(status().isNotFound());
    }

    private void addTestMovies() {
        List<Movie> movies = List.of(

        );

        movies.forEach(movieRepository::save);
    }
}
