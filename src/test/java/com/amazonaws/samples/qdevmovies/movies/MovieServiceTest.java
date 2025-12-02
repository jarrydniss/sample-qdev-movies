package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for MovieService search functionality
 * Arrr! Testing our treasure hunting capabilities, matey!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load movies from movies.json
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
        
        Optional<Movie> movieNegative = movieService.getMovieById(-1L);
        assertFalse(movieNegative.isPresent());
        
        Optional<Movie> movieZero = movieService.getMovieById(0L);
        assertFalse(movieZero.isPresent());
    }

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Prison Escape"
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Prison Escape" regardless of case
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("Hero", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Masked Hero"
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("hero")));
    }

    @Test
    public void testSearchMoviesByNameNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // All results should be Drama genre
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find Drama movies regardless of case
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreNoResults() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        // Search for movies with "The" in name and "Drama" genre
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertNotNull(results);
        
        // All results should match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesEmptyStringParameters() {
        // Empty strings should be treated as null
        List<Movie> results = movieService.searchMovies("", null, "");
        assertNotNull(results);
        // Should return all movies since no valid criteria provided
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    public void testSearchMoviesWhitespaceParameters() {
        // Whitespace-only strings should be treated as empty
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        assertNotNull(results);
        // Should return all movies since no valid criteria provided
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    public void testSearchMoviesNullParameters() {
        // All null parameters should return all movies
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    public void testSearchMoviesIdTakesPrecedence() {
        // When ID is provided, it should take precedence over other criteria
        List<Movie> results = movieService.searchMovies("SomeOtherName", 1L, "SomeOtherGenre");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Should contain unique genres from the movie collection
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action/Crime"));
        assertTrue(genres.contains("Adventure/Fantasy"));
        
        // Should be sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
        
        // Should not contain duplicates
        assertEquals(genres.size(), genres.stream().distinct().count());
    }

    @Test
    public void testSearchMoviesWithTrimming() {
        // Test that leading/trailing whitespace is properly trimmed
        List<Movie> results1 = movieService.searchMovies("  Prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("Prison", null, null);
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(results1.size(), results2.size());
        
        // Results should be identical
        for (int i = 0; i < results1.size(); i++) {
            assertEquals(results1.get(i).getId(), results2.get(i).getId());
        }
    }

    @Test
    public void testSearchMoviesGenreExactMatch() {
        // Genre should be exact match, not partial
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should not find "Crime/Drama" movies
        
        // But should find exact matches
        List<Movie> exactResults = movieService.searchMovies(null, null, "Crime/Drama");
        assertNotNull(exactResults);
        assertFalse(exactResults.isEmpty());
    }

    @Test
    public void testSearchMoviesPerformance() {
        // Basic performance test - search should complete quickly
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            movieService.searchMovies("The", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 searches in reasonable time (less than 1 second)
        assertTrue(duration < 1000, "Search performance is too slow: " + duration + "ms");
    }
}