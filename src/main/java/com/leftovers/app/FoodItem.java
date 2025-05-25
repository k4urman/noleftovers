// FoodItem.java
package com.leftovers.app;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@Component
@Entity
@Data
@RestController
@RequestMapping("/api/food")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private LocalDateTime postedAt;
    private boolean isAvailable;

    @ManyToOne
    private User user;

    public static void main(String[] args) {
        SpringApplication.run(FoodItem.class, args);
        // Launch JavaFX GUI after Spring Boot starts
        new Thread(() -> MainApp.launchApp()).start();
    }

    // Repository interface
    public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
        @Query("SELECT f FROM FoodItem f WHERE f.isAvailable = true " +
               "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) * " +
               "cos(radians(f.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
               "sin(radians(f.latitude)))) < :distance")
        List<FoodItem> findNearbyListings(double latitude, double longitude, double distance);
    }

    // Controller logic
    @GetMapping("/nearby")
    public List<FoodItem> getNearbyListings(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double distance,
            FoodItemRepository repository) {
        return repository.findNearbyListings(latitude, longitude, distance);
    }

    @PostMapping
    public FoodItem createListing(@RequestBody FoodItem foodItem, FoodItemRepository repository) {
        foodItem.setPostedAt(LocalDateTime.now());
        foodItem.setAvailable(true);
        return repository.save(foodItem);
    }

    @PostMapping("/{id}/claim")
    public void claimListing(@PathVariable Long id, FoodItemRepository repository) {
        FoodItem foodItem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not found"));
        foodItem.setAvailable(false);
        repository.save(foodItem);
    }
}
