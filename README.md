# leftovers

Leftovers is a desktop application built with Java, Spring Boot, and JavaFX to reduce food waste by enabling users to share and claim free food based on their location. Users can post food items, view nearby listings in a graphical interface, and claim available food, promoting sustainability and community engagement.

## Features
- **Post Food**: Share food items with a title, description, and manually entered location (latitude and longitude).
- **Find Nearby Listings**: View food listings within a 10 km radius (adjustable) in a list and a simple canvas-based map visualization.
- **Claim Food**: Mark food as claimed, updating its availability.
- **Simple Backend**: Uses Spring Boot with an in-memory H2 database for easy setup.
- **Desktop GUI**: Built with JavaFX for a user-friendly interface with tabs for posting and viewing food.

## Tech Stack
- **Backend**: Java 17, Spring Boot, Spring Data JPA, H2 Database, Lombok
- **Frontend**: JavaFX (controls and FXML)
- **Database**: In-memory H2 database (configurable for production)

## Project Structure
```
leftovers/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/leftovers/app/
│   │   │       ├── FoodItem.java       # Main app, entity, repository, and controller logic
│   │   │       ├── User.java           # User entity and repository
│   │   │       ├── Location.java       # Utility for generating config
│   │   │       └── MainApp.java        # JavaFX GUI frontend
│   │   └── resources/
│   │       └── application.properties  # Configuration file
├── README.md
```

## Prerequisites
- Java 17
- Maven 3.6+
- JavaFX SDK (e.g., OpenJFX 17+)
- IDE (e.g., IntelliJ IDEA, Eclipse)

## Setup Instructions
1. **Clone the Repository** (or create a new project):
   ```bash
   git clone <repository-url>
   cd leftovers
   ```
   Alternatively, create a new Maven project and add the provided files.

2. **Add Source Files**:
   - Place `FoodItem.java`, `User.java`, `Location.java`, and `MainApp.java` in `src/main/java/com/leftovers/app/`.
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
     Alternatively, run `Location.generateConfig()` to generate `application.properties`.

3. **Configure `pom.xml`**:
   - Place the following `pom.xml` in the project root:
     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
         <modelVersion>4.0.0</modelVersion>
         <groupId>com.leftovers</groupId>
         <artifactId>leftovers</artifactId>
         <version>1.0-SNAPSHOT</version>
         <name>Leftovers</name>
         <description>A Java app for sharing free food to reduce waste</description>
         <properties>
             <java.version>17</java.version>
             <spring-boot.version>3.2.5</spring-boot.version>
             <javafx.version>17.0.2</javafx.version>
         </properties>
         <dependencies>
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-web</artifactId>
                 <version>${spring-boot.version}</version>
             </dependency>
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-data-jpa</artifactId>
                 <version>${spring-boot.version}</version>
             </dependency>
             <dependency>
                 <groupId>com.h2database</groupId>
                 <artifactId>h2</artifactId>
                 <scope>runtime</scope>
             </dependency>
             <dependency>
                 <groupId>org.projectlombok</groupId>
                 <artifactId>lombok</artifactId>
                 <version>1.18.30</version>
                 <optional>true</optional>
             </dependency>
             <dependency>
                 <groupId>org.openjfx</groupId>
                 <artifactId>javafx-controls</artifactId>
                 <version>${javafx.version}</version>
             </dependency>
             <dependency>
                 <groupId>org.openjfx</groupId>
                 <artifactId>javafx-fxml</artifactId>
                 <version>${javafx.version}</version>
             </dependency>
             <dependency>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-test</artifactId>
                 <version>${spring-boot.version}</version>
                 <scope>test</scope>
             </dependency>
         </dependencies>
         <build>
             <plugins>
                 <plugin>
                     <groupId>org.springframework.boot</groupId>
                     <artifactId>spring-boot-maven-plugin</artifactId>
                     <version>${spring-boot.version}</version>
                     <executions>
                         <execution>
                             <goals>
                                 <goal>repackage</goal>
                             </goals>
                         </execution>
                     </executions>
                 </plugin>
                 <plugin>
                     <groupId>org.apache.maven.plugins</groupId>
                     <artifactId>maven-compiler-plugin</artifactId>
                     <version>3.11.0</version>
                     <configuration>
                         <source>${java.version}</source>
                         <target>${java.version}</target>
                     </configuration>
                 </plugin>
                 <plugin>
                     <groupId>org.openjfx</groupId>
                     <artifactId>javafx-maven-plugin</artifactId>
                     <version>0.0.8</version>
                     <configuration>
                         <mainClass>com.leftovers.app.FoodItem</mainClass>
                     </configuration>
                 </plugin>
             </plugins>
         </build>
     </project>
     ```

4. **Configure JavaFX**:
   - Download and install the JavaFX SDK (e.g., OpenJFX 17+).
   - Configure your IDE with JavaFX:
     - Add VM options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`
     - Example for IntelliJ: Edit run configuration and set the module path to your JavaFX SDK `lib` directory.

5. **Build the Project**:
   ```bash
   mvn clean install
   ```

6. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   Or run `FoodItem` (main class) from your IDE. The JavaFX GUI will launch automatically.

7. **Access the H2 Console** (optional):
   - Open `http://localhost:8080/h2-console` in a browser for database inspection.
   - Use JDBC URL: `jdbc:h2:mem:leftovers`, username: `sa`, no password.

## Usage
1. **Post Food**:
   - In the "Post Food" tab, enter a food title, description, latitude, and longitude.
   - Click "Post Food" to share the item. The default location is (51.505, -0.09).
2. **View Nearby Food**:
   - In the "View Nearby" tab, enter your latitude, longitude, and search distance (default: 10 km).
   - Click "Search Nearby" to list available food items.
   - The canvas displays your location (circle) and nearby food (squares).
3. **Claim Food**:
   - Click a food item in the list to claim it, marking it as unavailable.
   - The list and canvas update automatically.

## Notes
- **Authentication**: Omitted for simplicity. Add Spring Security for production use.
- **Database**: Uses an in-memory H2 database. For persistence, configure a database like PostgreSQL in `application.properties`.
- **Location**: JavaFX lacks native geolocation, so coordinates are entered manually. For a real app, integrate a geolocation API or use a platform-specific library.
- **Map**: The canvas-based map is a simple visualization. For advanced mapping, consider embedding a web view with Leaflet or using a Java mapping library like JXMapViewer.
- **Enhancements**: Add user profiles, image uploads, or notifications for a production-ready app.
- **Mobile Adaptation**: For mobile deployment, use frameworks like Gluon Mobile (requires additional setup).

## Troubleshooting
- **JavaFX Setup**: Ensure the JavaFX SDK path is correctly set in your IDE or build tool.
- **H2 Console Access**: Verify `spring.h2.console.enabled=true` and use the correct JDBC URL.
- **Dependencies**: Run `mvn dependency:resolve` if build issues occur.
- **GUI Not Launching**: Check that VM options include the JavaFX module path and modules.

## Contributing
Feel free to fork the repository, submit issues, or create pull requests to enhance features like authentication, advanced mapping, or mobile support.

## License
This project is licensed under the Creative Commons Legal Code License.


### Placement
- Save this content as `README.md` in the **root directory** of the project:
  ```
  leftovers/
  ├── README.md
  ├── pom.xml
  ├── src/
  │   ├── main/
  │   │   ├── java/
  │   │   │   └── com/leftovers/app/
  │   │   │       ├── FoodItem.java
  │   │   │       ├── User.java
  │   │   │       ├── Location.java
  │   │   │       └── MainApp.java
  │   │   └── resources/
  │   │       └── application.properties
  ```

### Changes from Provided README
- **App Type**: Updated to describe a desktop app using JavaFX instead of a web app with HTML/Leaflet/Tailwind CSS.
- **Features**: Revised to reflect the JavaFX GUI (tabs for posting/viewing, canvas-based map) and manual location input instead of browser geolocation.
- **Tech Stack**: Removed HTML, JavaScript, Tailwind CSS, and Leaflet; added JavaFX.
- **Project Structure**: Removed `src/main/resources/static/index.html` and added `MainApp.java`.
- **Setup Instructions**: Updated to include JavaFX setup (SDK, VM options) and removed `index.html` generation.
- **Usage**: Described the JavaFX interface (tabs, list, canvas) instead of web-based map and listings.
- **Notes**: Added JavaFX-specific notes (e.g., manual coordinates, basic map) and mobile adaptation suggestions.
- **License**: Retained the Creative Commons Legal Code License as specified.

### Notes
- The `README.md` assumes the `pom.xml` and `application.properties` provided earlier are used, ensuring all dependencies and configurations are covered.
- The license was kept as "Creative Commons Legal Code License" per your input, though this is ambiguous (Creative Commons has multiple licenses, e.g., CC BY 4.0). If you meant a specific Creative Commons license, please clarify, or I can suggest a standard like MIT.
- If you want additional sections (e.g., API endpoints, testing instructions) or modifications, let me know!
