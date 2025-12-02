package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("searchPerformed", false);
        model.addAttribute("searchMessage", "");
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! This be the treasure hunt endpoint for searching movies!
     * Supports both JSON API responses and HTML page rendering.
     * 
     * @param name Movie name to search for (partial match)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by
     * @param model Spring model for HTML rendering
     * @return JSON response for API calls or HTML page for browser requests
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "format", required = false, defaultValue = "html") String format,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Search request received - name: '{}', id: {}, genre: '{}', format: '{}'", 
                   name, id, genre, format);
        
        // Validate input parameters
        if ((name == null || name.trim().isEmpty()) && 
            (id == null || id <= 0) && 
            (genre == null || genre.trim().isEmpty())) {
            
            logger.warn("Blimey! No search criteria provided, showing all movies");
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchMessage", "Arrr! Ye need to provide some search criteria, matey! Showing all treasure instead.");
            return "movies";
        }
        
        // Perform the search
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Prepare search message with pirate flair
        String searchMessage = generatePirateSearchMessage(searchResults.size(), name, id, genre);
        
        // Add attributes for HTML rendering
        model.addAttribute("movies", searchResults);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchMessage", searchMessage);
        model.addAttribute("searchName", name);
        model.addAttribute("searchId", id);
        model.addAttribute("searchGenre", genre);
        
        return "movies";
    }

    /**
     * JSON API endpoint for movie search - Arrr! For ye tech-savvy pirates!
     * 
     * @param name Movie name to search for
     * @param id Specific movie ID to find
     * @param genre Genre to filter by
     * @return JSON response with search results
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! API search request received - name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate input parameters
        if ((name == null || name.trim().isEmpty()) && 
            (id == null || id <= 0) && 
            (genre == null || genre.trim().isEmpty())) {
            
            response.put("success", false);
            response.put("message", "Arrr! Ye need to provide at least one search criterion, matey!");
            response.put("movies", List.of());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("message", generatePirateSearchMessage(searchResults.size(), name, id, genre));
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Shiver me timbers! Error during search: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Blimey! Something went wrong during the treasure hunt: " + e.getMessage());
            response.put("movies", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Generates pirate-themed search result messages
     */
    private String generatePirateSearchMessage(int resultCount, String name, Long id, String genre) {
        StringBuilder criteria = new StringBuilder();
        
        if (name != null && !name.trim().isEmpty()) {
            criteria.append("name containing '").append(name).append("'");
        }
        if (id != null && id > 0) {
            if (criteria.length() > 0) criteria.append(" and ");
            criteria.append("ID ").append(id);
        }
        if (genre != null && !genre.trim().isEmpty()) {
            if (criteria.length() > 0) criteria.append(" and ");
            criteria.append("genre '").append(genre).append("'");
        }
        
        if (resultCount == 0) {
            return "Shiver me timbers! No treasure found with " + criteria + ". Try a different search, ye savvy pirate!";
        } else if (resultCount == 1) {
            return "Yo ho ho! Found 1 piece of treasure with " + criteria + "!";
        } else {
            return "Arrr! Found " + resultCount + " pieces of treasure with " + criteria + "! What a bounty, matey!";
        }
    }
}