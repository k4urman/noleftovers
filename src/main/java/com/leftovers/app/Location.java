package com.noleftovers.app;

import java.io.FileWriter;
import java.io.IOException;

public class Location {
    
    /**
     * Calculates the distance between two geographic points using the Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km
        
        return distance;
    }
    
    /**
     * Validates if the given coordinates are valid latitude and longitude values
     * @param latitude The latitude to validate
     * @param longitude The longitude to validate
     * @return true if coordinates are valid, false otherwise
     */
    public static boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
    
    /**
     * Generates the application.properties configuration file
     */
    public static void generateConfig() {
        String config = """
            spring.datasource.url=jdbc:h2:mem:noleftovers
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
            logging.level.com.noleftovers.app=DEBUG
            """;
        
        try (FileWriter writer = new FileWriter("src/main/resources/application.properties")) {
            writer.write(config);
            System.out.println("application.properties generated successfully!");
        } catch (IOException e) {
            System.err.println("Error generating application.properties: " + e.getMessage());
        }
    }
    
    /**
     * Default coordinates for London (used as fallback)
     */
    public static final double DEFAULT_LATITUDE = 51.505;
    public static final double DEFAULT_LONGITUDE = -0.09;
    
    /**
     * Default search radius in kilometers
     */
    public static final double DEFAULT_SEARCH_RADIUS = 10.0;
}
