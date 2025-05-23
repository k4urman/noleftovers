# leftovers

Leftovers is a web application built with Java and Spring Boot to reduce food waste by enabling users to share and claim free food based on their location. Users can post food items, view nearby listings on a map, and claim available food, promoting sustainability and community engagement.

## Features
- **Post Food**: Share food items with a title, description, and location (auto-detected via geolocation).
- **Find Nearby Listings**: View food listings within a 10 km radius (adjustable) on an interactive map powered by Leaflet.
- **Claim Food**: Mark food as claimed, updating its availability.
- **Simple Backend**: Uses Spring Boot with an in-memory H2 database for easy setup.
- **Responsive Frontend**: Built with HTML, JavaScript, Tailwind CSS, and Leaflet for a user-friendly interface.

## Tech Stack
- **Backend**: Java 17, Spring Boot, Spring Data JPA, H2 Database, Lombok
- **Frontend**: HTML, JavaScript, Tailwind CSS, Leaflet
- **Database**: In-memory H2 database (configurable for production)

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/leftovers/app/
│   │       ├── FoodItem.java       # Main app, entity, repository, and controller logic
│   │       ├── User.java           # User entity and repository
│   │       └── Location.java       # Utility for generating frontend and config
│   └── resources/
│       ├── static/
│       │   └── index.html          # Frontend UI
│       └── application.properties  # Configuration file
```

## Prerequisites
- Java 17
- Maven 3.6+
- IDE (e.g., IntelliJ IDEA, Eclipse)
- Web browser with geolocation support

## Setup Instructions
1. **Clone the Repository** (or create a new project):
   ```bash
   git clone <repository-url>
   cd leftovers
   ```
   Alternatively, create a new Spring Boot project with dependencies: Spring Web, Spring Data JPA, H2 Database, Lombok.

2. **Add Source Files**:
   - Place `FoodItem.java`, `User.java`, and `Location.java` in `src/main/java/com/leftovers/app/`.
   - Create `application.properties` in `src/main/resources/` with the following:
     ```properties
     spring.datasource.url=jdbc:h2:mem:leftovers
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=
     spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     spring.h2.console.enabled=true
     spring.h2.console.path=/h2-console
     server.port=8080
     logging.level.org.springframework=INFO
     logging.level.com.leftovers.app=DEBUG
     ```
   - Generate `index.html` in `src/main/resources/static/` by running `Location.generateFrontend()` or manually copying the HTML content from the `Location` class.

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   Or run `FoodItem` (main class) from your IDE.

5. **Access the App**:
   - Open `http://localhost:8080` in a browser to use the app.
   - Access the H2 console at `http://localhost:8080/h2-console` for database inspection (use JDBC URL: `jdbc:h2:mem:leftovers`, username: `sa`, no password).

## Usage
1. **Share Food**:
   - Enter a food title and description in the form.
   - Click "Post Food" to share (location is auto-detected or defaults to map center).
2. **View Nearby Food**:
   - The map shows your location and nearby food listings.
   - Listings are displayed below the map with details and a "Claim" button.
3. **Claim Food**:
   - Click "Claim" on a listing to mark it as unavailable.
   - The map and listings update automatically.

## Notes
- **Authentication**: This version omits authentication for simplicity. For production, add Spring Security.
- **Database**: Uses an in-memory H2 database. For persistence, configure a database like PostgreSQL in `application.properties`.
- **Enhancements**: Consider adding user profiles, image uploads, or notifications for a production-ready app.
- **Geolocation**: The app uses browser geolocation. Ensure your browser allows location access or it defaults to a preset location (London).

## Troubleshooting
- **H2 Console Access**: Ensure `spring.h2.console.enabled=true` and use the correct JDBC URL.
- **Geolocation Issues**: If the browser blocks geolocation, the app defaults to coordinates (51.505, -0.09).
- **Dependencies**: Run `mvn dependency:resolve` if you encounter build issues.

## Contributing
Feel free to fork the repository, submit issues, or create pull requests to enhance features like authentication, mobile support, or additional filters for food listings.

## License
This project is licensed under the Creative Commons Legal Code License.

### Placement
- Save this content as `README.md` in the root directory of your project:
  ```
  leftovers/
  ├── README.md
  ├── src/
  │   ├── main/
  │   │   ├── java/
  │   │   └── resources/
  ├── pom.xml
  └── ...
  ```

### Explanation
- **Overview**: Describes the app’s purpose and features to reduce food waste through location-based sharing.
- **Tech Stack**: Lists technologies used, aligning with the provided code.
- **Setup Instructions**: Guides users through setting up the project, including creating `application.properties` and `index.html`.
- **Usage**: Explains how to interact with the app (post, view, claim food).
- **Notes**: Highlights simplifications (e.g., no authentication) and suggestions for production.
- **Troubleshooting**: Addresses common issues like H2 console access or geolocation.
- **Structure**: Uses standard Markdown formatting for clarity and compatibility with platforms like GitHub.

This README provides all necessary information to set up, run, and understand the Leftovers app. If you need additional sections (e.g., API documentation) or specific details, let me know!
