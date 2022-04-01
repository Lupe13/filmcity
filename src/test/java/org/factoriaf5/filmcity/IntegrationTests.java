package org.factoriaf5.filmcity;

import org.factoriaf5.filmcity.domain.Movie;
import org.factoriaf5.filmcity.repositories.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    void returnsTheExistingMovies() throws Exception {

        addSampleMovies();

        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0].title", equalTo("Jurassic Park")))
                .andExpect(jsonPath("$[0].coverImage", equalTo("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg")))
                .andExpect(jsonPath("$[0].director", equalTo("Steven Spielberg")))
                .andExpect(jsonPath("$[0].year", equalTo(1993)))
                .andExpect(jsonPath("$[0].synopsis", equalTo("A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA.")))
                .andExpect(jsonPath("$[1].title", equalTo("Ratatouille")))
                .andExpect(jsonPath("$[1].coverImage", equalTo("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/npHNjldbeTHdKKw28bJKs7lzqzj.jpg")))
                .andExpect(jsonPath("$[1].director", equalTo("Brad Bird")))
                .andExpect(jsonPath("$[1].year", equalTo(2007)))
                .andExpect(jsonPath("$[1].synopsis", equalTo("Remy, a resident of Paris, appreciates good food and has quite a sophisticated palate. He would love to become a chef so he can create and enjoy culinary masterpieces to his heart's delight. The only problem is, Remy is a rat.")))
                .andDo(print());
    }

    private void addSampleMovies() {
        List<Movie> movies = List.of(
                new Movie("Jurassic Park",
                        "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg",
                        "Steven Spielberg",
                        1993,
                        "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."),
                new Movie("Ratatouille",
                        "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/npHNjldbeTHdKKw28bJKs7lzqzj.jpg",
                        "Brad Bird",
                        2007,
                        "Remy, a resident of Paris, appreciates good food and has quite a sophisticated palate. He would love to become a chef so he can create and enjoy culinary masterpieces to his heart's delight. The only problem is, Remy is a rat.")
        );

        movieRepository.saveAll(movies);
    }

    @Test
    void allowsToCreateANewMovie() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"title\": \"Harry Potter\", " +
                        "\"coverImage\": \"https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg\", " +
                        "\"director\": \"Carlitos\", " +
                        "\"year\": \"2001\", " +
                        "\"synopsis\": \"El día en que cumple once años, Harry Potter se entera de que es hijo de dos destacados hechiceros, de los que ha heredado poderes mágicos. En la escuela Hogwarts de Magia y Hechicería, donde se educa con otros niños que también tienen poderes especiales, aprenderá todo lo necesario para ser mago. \" " +
                        "}")
        ).andExpect(status().isOk());

        List<Movie> movies = movieRepository.findAll();
        assertThat(movies, contains(allOf(
                hasProperty("title", is("Harry Potter")),
                hasProperty("coverImage", is("https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg")),
                hasProperty("director", is("Carlitos")),
                hasProperty("year", is(2001)),
                hasProperty("synopsis", is("El día en que cumple once años, Harry Potter se entera de que es hijo de dos destacados hechiceros, de los que ha heredado poderes mágicos. En la escuela Hogwarts de Magia y Hechicería, donde se educa con otros niños que también tienen poderes especiales, aprenderá todo lo necesario para ser mago. "))
        )));
    }
    @Test
    void allowsToFindAMovieById() throws Exception {

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
    void allowsToModifyAMovie() throws Exception {
        Movie movie = movieRepository.save(new Movie("Jurassic Park", "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/oU7Oq2kFAAlGqbU4VoAE36g4hoI.jpg", "Steven Spielberg", 1993, "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA."));

        mockMvc.perform(put("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "\"id\": \"" + movie.getId() + "\", " +
                        "\"title\": \"Hero of Central Park\", " +
                        "\"coverImage\": \"https://imgix.ranker.com/node_img/9/166109/original/chestnut-hero-of-central-park-films-photo-1?auto=format&q=60&fit=fill&fm=pjpg&dpr=2&crop=faces&bg=fff&h=333&w=333\" }")
        ).andExpect(status().isOk());

        List<Movie> movies = movieRepository.findAll();

        assertThat(movies, hasSize(1));
        assertThat(movies.get(0).getTitle(), equalTo("Hero of Central Park"));
        assertThat(movies.get(0).getCoverImage(), equalTo("https://imgix.ranker.com/node_img/9/166109/original/chestnut-hero-of-central-park-films-photo-1?auto=format&q=60&fit=fill&fm=pjpg&dpr=2&crop=faces&bg=fff&h=333&w=333"));
    }

    @Test
    void returnsAnErrorIfTryingToGetAMovieThatDoesNotExist() throws Exception {
        mockMvc.perform(get("/movies/30"))
                .andExpect(status().isNotFound());
    }
    @Test
    void allowsToDeleteAMovieById() throws Exception {
        Movie movie = movieRepository.save(new Movie("Los juegos del hambre", "https://www.themoviedb.org/t/p/w1280/AgoxJvkKRd5MZAwAHq5bSBjUsnQ.jpg", "Francis Lawrence", 2012, "Katniss Everdeen, una joven de dieciséis años, decide sustituir a su hermana en los juegos; pero para ella, que ya ha visto la muerte de cerca, la lucha por la supervivencia es su segunda naturaleza."));

        mockMvc.perform(delete("/movies/"+ movie.getId()))
                .andExpect(status().isOk());

        List<Movie> movies = movieRepository.findAll();
        assertThat(movies, not(contains(allOf(
                hasProperty("title", is("Los juegos del hambre")),
                hasProperty("coverImage", is("https://www.themoviedb.org/t/p/w1280/AgoxJvkKRd5MZAwAHq5bSBjUsnQ.jpg"))
        ))));
    }
    @Test
    void returnsAnErrorIfTryingToDeleteAMovieThatDoesNotExist() throws Exception {
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isNotFound());
    }}