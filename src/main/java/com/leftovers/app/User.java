// User.java
package com.leftovers.app;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Entity
@Data
@Component
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private double latitude;
    private double longitude;

    public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
    }
}
