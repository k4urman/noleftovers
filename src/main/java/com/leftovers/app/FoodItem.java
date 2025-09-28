package com.noleftovers.app;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class FoodItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

interface FoodItemRepository extends JpaRepository<FoodItemEntity, Long> {
    @Query("SELECT f FROM FoodItemEntity f WHERE f.available = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(f.latitude)) * " +
           "cos(radians(f.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(f.latitude)))) <= :distance")
    List<FoodItemEntity> findNearbyAvailableFood(
        @Param("lat") Double latitude,
        @Param("lng") Double longitude,
        @Param("distance") Double distanceKm
    );
    
    List<FoodItemEntity> findByAvailableTrue();
}

@RestController
@RequestMapping("/api/food")
class FoodItemController {
    
    @Autowired
    private FoodItemRepository foodItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<FoodItemEntity> createFoodItem(@RequestBody CreateFoodItemRequest request) {
        // Create or get default user
        User user = userRepository.findById(1L)
            .orElseGet(() -> userRepository.save(User.builder()
                .name("Default User")
                .email("user@noleftovers.com")
                .build()));
        
        FoodItemEntity foodItem = FoodItemEntity.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .latitude(request.getLatitude() != null ? request.getLatitude() : 51.505)
            .longitude(request.getLongitude() != null ? request.getLongitude() : -0.09)
            .available(true)
            .createdAt(LocalDateTime.now())
            .user(user)
            .build();
        
        FoodItemEntity saved = foodItemRepository.save(foodItem);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<FoodItemEntity>> getNearbyFood(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double distance) {
        
        List<FoodItemEntity> nearbyFood = foodItemRepository.findNearbyAvailableFood(lat, lng, distance);
        return ResponseEntity.ok(nearbyFood);
    }
    
    @GetMapping
    public ResponseEntity<List<FoodItemEntity>> getAllAvailableFood() {
        List<FoodItemEntity> availableFood = foodItemRepository.findByAvailableTrue();
        return ResponseEntity.ok(availableFood);
    }
    
    @PutMapping("/{id}/claim")
    public ResponseEntity<FoodItemEntity> claimFood(@PathVariable Long id) {
        Optional<FoodItemEntity> optionalFood = foodItemRepository.findById(id);
        if (optionalFood.isPresent()) {
            FoodItemEntity food = optionalFood.get();
            food.setAvailable(false);
            FoodItemEntity updated = foodItemRepository.save(food);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CreateFoodItemRequest {
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
}

@SpringBootApplication
public class FoodItem {
    public static void main(String[] args) {
        // Start Spring Boot application in a separate thread
        Thread springThread = new Thread(() -> {
            SpringApplication.run(FoodItem.class, args);
        });
        springThread.setDaemon(true);
        springThread.start();
        
        // Wait a bit for Spring Boot to start
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Launch JavaFX GUI
        MainApp.main(args);
    }
}
