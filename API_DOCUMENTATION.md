# Movie Search API Documentation 🏴‍☠️

Ahoy matey! This be the comprehensive guide to our movie search treasure hunting API. Whether ye be a landlubber or a seasoned sea dog, this documentation will help ye navigate our search capabilities like a true pirate!

## Overview

The Movie Search API provides powerful search and filtering capabilities for our movie treasure chest. It supports both HTML web interface and JSON API responses, making it perfect for both human pirates and their digital parrot companions.

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication required - this treasure be free for all pirates! 🏴‍☠️

## Endpoints

### 1. HTML Search Interface

**Endpoint:** `GET /movies/search`

**Description:** Returns an HTML page with filtered movie results and pirate-themed search form.

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name to search for (partial, case-insensitive matching) |
| `id` | Long | No | Specific movie ID to find (exact match, must be positive) |
| `genre` | String | No | Genre to filter by (exact, case-insensitive matching) |

**Response:** HTML page with search results

**Examples:**

```bash
# Search by movie name
GET /movies/search?name=prison
# Returns movies containing "prison" in the name

# Search by genre
GET /movies/search?genre=Drama
# Returns all Drama movies

# Search by ID
GET /movies/search?id=1
# Returns movie with ID 1

# Combined search
GET /movies/search?name=the&genre=Drama
# Returns Drama movies containing "the" in the name

# No criteria (shows all movies with warning message)
GET /movies/search
```

**Special Behaviors:**
- Empty or whitespace-only parameters are ignored
- If no valid criteria provided, shows all movies with pirate warning message
- Search form preserves entered values after search
- Results include pirate-themed success/failure messages

---

### 2. JSON Search API

**Endpoint:** `GET /api/movies/search`

**Description:** Returns JSON response with filtered movie results - perfect for ye tech-savvy pirates!

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name to search for (partial, case-insensitive matching) |
| `id` | Long | No | Specific movie ID to find (exact match, must be positive) |
| `genre` | String | No | Genre to filter by (exact, case-insensitive matching) |

**Response Format:**

```json
{
  "success": boolean,
  "message": "string (pirate-themed message)",
  "movies": [
    {
      "id": number,
      "movieName": "string",
      "director": "string",
      "year": number,
      "genre": "string",
      "description": "string",
      "duration": number,
      "imdbRating": number
    }
  ],
  "totalResults": number
}
```

**HTTP Status Codes:**
- `200 OK`: Successful search (even if no results found)
- `400 Bad Request`: No search criteria provided
- `500 Internal Server Error`: Server error during search

**Examples:**

#### Successful Search
```bash
curl "http://localhost:8080/api/movies/search?name=prison"
```

**Response:**
```json
{
  "success": true,
  "message": "Yo ho ho! Found 1 piece of treasure with name containing 'prison'!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "totalResults": 1
}
```

#### No Results Found
```bash
curl "http://localhost:8080/api/movies/search?name=nonexistent"
```

**Response:**
```json
{
  "success": true,
  "message": "Shiver me timbers! No treasure found with name containing 'nonexistent'. Try a different search, ye savvy pirate!",
  "movies": [],
  "totalResults": 0
}
```

#### Bad Request (No Criteria)
```bash
curl "http://localhost:8080/api/movies/search"
```

**Response:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search criterion, matey!",
  "movies": []
}
```

## Search Logic

### Name Search
- **Type:** Partial matching
- **Case Sensitivity:** Case-insensitive
- **Behavior:** Searches within movie names using `contains()` logic
- **Examples:**
  - `name=prison` matches "The Prison Escape"
  - `name=HERO` matches "The Masked Hero"
  - `name=the` matches multiple movies containing "the"

### Genre Search
- **Type:** Exact matching
- **Case Sensitivity:** Case-insensitive
- **Behavior:** Exact match against movie genre
- **Examples:**
  - `genre=Drama` matches movies with genre "Drama"
  - `genre=drama` also matches movies with genre "Drama"
  - `genre=Crime` does NOT match "Crime/Drama" (must be exact)

### ID Search
- **Type:** Exact matching
- **Validation:** Must be positive integer
- **Priority:** When ID is provided, it takes precedence over other criteria
- **Examples:**
  - `id=1` returns only movie with ID 1
  - `id=1&name=something&genre=other` still returns only movie with ID 1

### Combined Search
- **Logic:** AND operation (all criteria must match)
- **Example:** `name=the&genre=Drama` returns movies that:
  - Contain "the" in the name AND
  - Have genre exactly matching "Drama"

## Available Genres

The following genres are available in our treasure chest:

- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

*Note: Use exact genre names for filtering. You can get the current list via the `/movies` page or by calling the search API and examining the available movies.*

## Error Handling

### Client Errors (4xx)

**400 Bad Request**
- **Cause:** No search criteria provided
- **Message:** "Arrr! Ye need to provide at least one search criterion, matey!"
- **Solution:** Provide at least one valid search parameter

### Server Errors (5xx)

**500 Internal Server Error**
- **Cause:** Unexpected server error during search
- **Message:** "Blimey! Something went wrong during the treasure hunt: [error details]"
- **Solution:** Check server logs and try again

### Input Validation

The API handles various edge cases gracefully:

- **Null parameters:** Treated as not provided
- **Empty strings:** Treated as not provided
- **Whitespace-only strings:** Trimmed and treated as empty
- **Invalid ID values:** Negative or zero IDs are ignored
- **Non-existent IDs:** Return empty results (not an error)

## Rate Limiting

Currently no rate limiting is implemented. All pirates are welcome to search as much as they desire! 🏴‍☠️

## Caching

Search results are not cached. Each request performs a fresh search through the movie collection.

## Performance

- **Search Time:** Typically < 10ms for the current dataset (12 movies)
- **Memory Usage:** Minimal - searches are performed in-memory
- **Scalability:** Linear with dataset size (O(n) complexity)

## Logging

All search operations are logged with pirate-themed messages:

```
INFO  - Ahoy! Starting treasure hunt for movies with criteria - name: 'prison', id: null, genre: 'null'
INFO  - Yo ho ho! Found 1 pieces of treasure matching yer search!
```

## SDK Examples

### JavaScript (Fetch API)

```javascript
// Search by name
async function searchMovies(name) {
    const response = await fetch(`/api/movies/search?name=${encodeURIComponent(name)}`);
    const data = await response.json();
    
    if (data.success) {
        console.log(`Found ${data.totalResults} movies:`, data.movies);
    } else {
        console.error('Search failed:', data.message);
    }
}

// Combined search
async function advancedSearch(name, genre) {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (genre) params.append('genre', genre);
    
    const response = await fetch(`/api/movies/search?${params}`);
    return await response.json();
}
```

### Python (requests)

```python
import requests

def search_movies(name=None, movie_id=None, genre=None):
    params = {}
    if name:
        params['name'] = name
    if movie_id:
        params['id'] = movie_id
    if genre:
        params['genre'] = genre
    
    response = requests.get('http://localhost:8080/api/movies/search', params=params)
    return response.json()

# Usage
result = search_movies(name='prison')
if result['success']:
    print(f"Found {result['totalResults']} movies")
    for movie in result['movies']:
        print(f"- {movie['movieName']} ({movie['year']})")
```

### Java (Spring RestTemplate)

```java
@Service
public class MovieSearchClient {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/api/movies/search";
    
    public SearchResponse searchMovies(String name, Long id, String genre) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        
        if (name != null) builder.queryParam("name", name);
        if (id != null) builder.queryParam("id", id);
        if (genre != null) builder.queryParam("genre", genre);
        
        return restTemplate.getForObject(builder.toUriString(), SearchResponse.class);
    }
}
```

## Testing

### Manual Testing

Use curl or any HTTP client to test the endpoints:

```bash
# Test successful search
curl -v "http://localhost:8080/api/movies/search?name=prison"

# Test no results
curl -v "http://localhost:8080/api/movies/search?name=nonexistent"

# Test bad request
curl -v "http://localhost:8080/api/movies/search"

# Test combined search
curl -v "http://localhost:8080/api/movies/search?name=the&genre=Drama"
```

### Automated Testing

The project includes comprehensive unit tests:

```bash
# Run all tests
mvn test

# Run specific search tests
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest
```

## Changelog

### Version 1.0.0 (Current)
- ✅ Initial implementation of movie search API
- ✅ HTML and JSON response formats
- ✅ Name, ID, and genre search criteria
- ✅ Pirate-themed messages and logging
- ✅ Comprehensive error handling
- ✅ Full test coverage

### Future Enhancements
- 🚧 Fuzzy search capabilities
- 🚧 Advanced filtering (year range, rating range)
- 🚧 Sorting options
- 🚧 Pagination for large result sets
- 🚧 Search result caching
- 🚧 More pirate language variations

---

*Arrr! That be all ye need to know about our treasure hunting API, matey! May yer searches be swift and yer results be bountiful! ⚓*

## Support

If ye be having trouble with the API, check the application logs for pirate-themed debug messages, or consult the main README.md for troubleshooting tips.

*Fair winds and following seas! 🏴‍☠️*