package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> results = new ArrayList<>();
                List<Movie> allMovies = getAllMovies();
                
                // If searching by ID, return that specific movie if found
                if (id != null && id > 0) {
                    Optional<Movie> movieById = getMovieById(id);
                    if (movieById.isPresent()) {
                        results.add(movieById.get());
                    }
                    return results;
                }
                
                // Filter by name and/or genre
                for (Movie movie : allMovies) {
                    boolean matchesName = true;
                    boolean matchesGenre = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matchesName = movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim());
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matchesGenre = movie.getGenre().toLowerCase().equals(genre.toLowerCase().trim());
                    }
                    
                    if (matchesName && matchesGenre) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify model attributes
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertFalse((Boolean) model.getAttribute("searchPerformed"));
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("Test", null, null, "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue((Boolean) model.getAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("searchMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Drama", "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Drama", movies.get(0).getGenre());
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("No treasure found"));
    }

    @Test
    public void testSearchMoviesNoCriteria() {
        String result = moviesController.searchMovies(null, null, null, "html", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // Should return all movies
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("need to provide some search criteria"));
    }

    @Test
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", null, null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("totalResults"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesApiNoCriteria() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, null);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertTrue(((String) body.get("message")).contains("need to provide at least one search criterion"));
    }

    @Test
    public void testSearchMoviesApiNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("NonExistent", null, null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("totalResults"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(2, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        assertEquals("Action Movie", movies.get(1).getMovieName());
    }
}
