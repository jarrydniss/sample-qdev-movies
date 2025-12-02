package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Searches for movies based on the provided criteria with pirate-themed logging.
     * Ahoy! This method be the treasure map to find yer desired movies, matey!
     * 
     * @param name Movie name to search for (partial, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (exact match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Starting treasure hunt for movies with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> searchResults = new ArrayList<>();
        
        // If searching by ID, return that specific movie if found
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            if (movieById.isPresent()) {
                logger.info("Arrr! Found treasure by ID: {}", movieById.get().getMovieName());
                searchResults.add(movieById.get());
                return searchResults;
            } else {
                logger.warn("Blimey! No treasure found with ID: {}", id);
                return searchResults; // Return empty list
            }
        }
        
        // Filter movies based on name and/or genre
        for (Movie movie : movies) {
            boolean matchesName = true;
            boolean matchesGenre = true;
            
            // Check name criteria (partial, case-insensitive)
            if (name != null && !name.trim().isEmpty()) {
                matchesName = movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim());
            }
            
            // Check genre criteria (exact match, case-insensitive)
            if (genre != null && !genre.trim().isEmpty()) {
                matchesGenre = movie.getGenre().toLowerCase().equals(genre.toLowerCase().trim());
            }
            
            // Add movie if it matches all criteria
            if (matchesName && matchesGenre) {
                searchResults.add(movie);
            }
        }
        
        if (searchResults.isEmpty()) {
            logger.info("Shiver me timbers! No treasure found matching yer search criteria, matey!");
        } else {
            logger.info("Yo ho ho! Found {} pieces of treasure matching yer search!", searchResults.size());
        }
        
        return searchResults;
    }

    /**
     * Gets all unique genres available in the movie collection.
     * Useful for populating search form dropdowns, ye savvy pirate!
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
    }
}
