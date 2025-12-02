# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate-themed twist!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🆕 Movie Search & Filtering**: Hunt for treasure with our pirate-themed search functionality!
  - Search by movie name (partial matching)
  - Filter by genre
  - Find specific movies by ID
  - Combine multiple search criteria
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Ahoy matey! Enjoy the pirate-themed interface and messages

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🆕 Movie Search**: Use the search form on the main page or API endpoints below

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── MovieService.java         # Business logic for movie operations
│   │       ├── Movie.java                # Movie data model
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── MovieIconUtils.java   # Movie icon utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── application.yml               # Application configuration
│       ├── movies.json                   # Movie data
│       ├── mock-reviews.json             # Mock review data
│       ├── log4j2.xml                    # Logging configuration
│       └── templates/
│           ├── movies.html               # Main movie listing page with search
│           └── movie-details.html        # Movie details page
└── test/                                 # Unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a pirate-themed search form.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🆕 Search Movies (HTML Interface)
```
GET /movies/search
```
Returns an HTML page with filtered movie results based on search criteria.

**Query Parameters:**
- `name` (optional): Movie name to search for (partial, case-insensitive matching)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (exact, case-insensitive matching)

**Examples:**
```
# Search by name
http://localhost:8080/movies/search?name=prison

# Search by genre
http://localhost:8080/movies/search?genre=Drama

# Search by ID
http://localhost:8080/movies/search?id=1

# Combined search
http://localhost:8080/movies/search?name=the&genre=Drama
```

### 🆕 Search Movies (JSON API)
```
GET /api/movies/search
```
Returns JSON response with filtered movie results - perfect for ye tech-savvy pirates!

**Query Parameters:**
- `name` (optional): Movie name to search for (partial, case-insensitive matching)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (exact, case-insensitive matching)

**Response Format:**
```json
{
  "success": true,
  "message": "Yo ho ho! Found 2 pieces of treasure with name containing 'the'!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "totalResults": 1
}
```

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/api/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/api/movies/search?genre=Drama"

# Search by ID
curl "http://localhost:8080/api/movies/search?id=1"

# Combined search
curl "http://localhost:8080/api/movies/search?name=the&genre=Drama"
```

**Error Response:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search criterion, matey!",
  "movies": []
}
```

## Search Features

### 🔍 Search Capabilities
- **Name Search**: Partial, case-insensitive matching (e.g., "prison" finds "The Prison Escape")
- **Genre Filter**: Exact, case-insensitive matching (e.g., "drama" matches "Drama")
- **ID Search**: Exact match for specific movie lookup
- **Combined Search**: Use multiple criteria together (AND logic)
- **Input Validation**: Handles empty inputs, whitespace, and invalid parameters
- **Pirate Messages**: All search results include themed messages and logging

### 🏴‍☠️ Pirate Language Features
- Search form uses pirate terminology ("Hunt for Treasure", "Start Treasure Hunt!")
- Results display pirate-themed messages ("Yo ho ho! Found X pieces of treasure!")
- Empty results show encouraging pirate messages
- API responses include pirate flair while maintaining technical accuracy
- Logging includes pirate-themed messages for debugging

### 📱 User Interface
- Responsive search form with pirate styling
- Real-time form validation
- Search results preserve form state
- Clear "no results" messaging with helpful suggestions
- Seamless integration with existing movie grid layout

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest
```

### Test Coverage
- **MovieService**: Search functionality, edge cases, performance
- **MoviesController**: HTML and JSON endpoints, error handling
- **Integration Tests**: End-to-end search workflows
- **Edge Cases**: Invalid inputs, empty results, parameter validation

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that movies.json is properly loaded
2. Verify search parameters are correctly formatted
3. Check application logs for pirate-themed debug messages
4. Ensure proper URL encoding for special characters

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- ✅ Add new features like search or filtering (Already implemented!)
- Improve the responsive design
- Add more pirate language and personality
- Extend search functionality (fuzzy matching, advanced filters)

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Ahoy matey! May fair winds fill yer sails as ye explore our movie treasures! ⚓*
