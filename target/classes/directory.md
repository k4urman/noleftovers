```
leftovers/
├── pom.xml                    # Maven configuration file for dependencies and build settings
├── README.md                  # Project documentation with setup and usage instructions
├── PLAN.txt                   # Development plan outlining tasks and approach
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/leftovers/app/
    │   │       ├── FoodItem.java  # Main app, entity, repository, and controller logic
    │   │       ├── User.java      # User entity and repository
    │   │       ├── Location.java  # Utility for generating application.properties
    │   │       └── MainApp.java   # JavaFX GUI frontend
    │   └── resources/
    │       └── application.properties  # Spring Boot configuration for H2 database and server
```

### Explanation
- **Root Directory (`leftovers/`)**:
  - `pom.xml`: Maven configuration file, defining dependencies (Spring Boot, H2, Lombok, JavaFX) and build plugins.
  - `README.md`: Documentation with project overview, setup instructions, usage, and troubleshooting.
  - `PLAN.txt`: Development plan outlining the approach, tasks, timeline, and considerations.
- **Source Directory (`src/main/`)**:
  - `java/com/leftovers/app/`:
    - `FoodItem.java`: Contains the main application class, food item entity, repository interface, and REST controller for food-related operations.
    - `User.java`: Defines the user entity and repository for user data management.
    - `Location.java`: Utility class for generating `application.properties`.
    - `MainApp.java`: JavaFX frontend with GUI tabs for posting and viewing food listings.
  - `resources/`:
    - `application.properties`: Configures the H2 in-memory database, JPA, H2 console, server port, and logging.
- **Notes**:
  - Unlike the web version, there is no `src/main/resources/static/index.html` since the frontend is now JavaFX-based.
  - The structure is minimal, with only essential files for the MVP, as described in the provided code and `README.md`.
  - The `pom.xml`, `README.md`, and `PLAN.txt` are in the root directory, as is standard for Maven projects.

### Verification
- Ensure all files are placed as shown:
  - Java files in `src/main/java/com/leftovers/app/`.
  - `application.properties` in `src/main/resources/`.
  - `pom.xml`, `README.md`, and `PLAN.txt` in the root (`leftovers/`).
- Run `mvn clean install` in the root directory to verify the project builds correctly.
- If any file is misplaced, Maven or Spring Boot may fail to detect it (e.g., `application.properties` must be in `resources/`).
