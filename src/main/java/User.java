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

    // Repository interface
    public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
    }
}

// Location.java
package com.leftovers.app;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Data
public class Location {
    // Utility methods to generate frontend and config
    public void generateFrontend() throws IOException {
        String htmlContent = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Leftovers - Free Food Sharing</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                #map { height: 400px; }
            </style>
        </head>
        <body class="bg-gray-100">
            <div class="container mx-auto p-4">
                <h1 class="text-3xl font-bold mb-4">Leftovers - Share Free Food</h1>
                
                <!-- Post Food Form -->
                <div class="mb-4 p-4 bg-white rounded shadow">
                    <h2 class="text-xl font-semibold mb-2">Share Food</h2>
                    <div class="space-y-2">
                        <input id="title" class="w-full p-2 border rounded" placeholder="Food Title">
                        <textarea id="description" class="w-full p-2 border rounded" placeholder="Description"></textarea>
                        <button onclick="postFood()" class="bg-green-500 text-white p-2 rounded hover:bg-green-600">
                            Post Food
                        </button>
                    </div>
                </div>
                
                <!-- Map -->
                <div id="map" class="mb-4"></div>
                
                <!-- Listings -->
                <div id="listings" class="space-y-4"></div>
            </div>
            
            <script>
                let map, userMarker;
                
                function initMap(lat, lng) {
                    map = L.map('map').setView([lat, lng], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);
                    userMarker = L.marker([lat, lng]).addTo(map)
                        .bindPopup('You are here').openPopup();
                }
                
                navigator.geolocation.getCurrentPosition(
                    pos => {
                        const { latitude, longitude } = pos.coords;
                        initMap(latitude, longitude);
                        loadListings(latitude, longitude);
                    },
                    err => {
                        console.error(err);
                        initMap(51.505, -0.09);
                        loadListings(51.505, -0.09);
                    }
                );
                
                async function loadListings(lat, lng) {
                    const response = await fetch(
                        `/api/food/nearby?latitude=${lat}&longitude=${lng}&distance=10`
                    );
                    const listings = await response.json();
                    const listingsDiv = document.getElementById('listings');
                    listingsDiv.innerHTML = '';
                    
                    listings.forEach(listing => {
                        const div = document.createElement('div');
                        div.className = 'p-4 bg-white rounded shadow';
                        div.innerHTML = `
                            <h3 class="text-lg font-semibold">${listing.title}</h3>
                            <p>${listing.description}</p>
                            <p class="text-sm text-gray-500">
                                Posted: ${new Date(listing.postedAt).toLocaleString()}
                            </p>
                            <button onclick="claimFood(${listing.id})" 
                                    class="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 mt-2">
                                Claim
                            </button>
                        `;
                        listingsDiv.appendChild(div);
                        
                        L.marker([listing.latitude, listing.longitude])
                            .addTo(map)
                            .bindPopup(`<b>${listing.title}</b><br>${listing.description}`);
                    });
                }
                
                async function postFood() {
                    const title = document.getElementById('title').value;
                    const description = document.getElementById('description').value;
                    const { latitude, longitude } = map.getCenter();
                    
                    const response = await fetch('/api/food', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            title,
                            description,
                            latitude,
                            longitude
                        })
                    });
                    
                    if (response.ok) {
                        document.getElementById('title').value = '';
                        document.getElementById('description').value = '';
                        loadListings(latitude, longitude);
                    }
                }
                
                async function claimFood(id) {
                    const response = await fetch(`/api/food/${id}/claim`, {
                        method: 'POST'
                    });
                    if (response.ok) {
                        const { latitude, longitude } = map.getCenter();
                        loadListings(latitude, longitude);
                    }
                }
            </script>
        </body>
        </html>
        """;
        
        Files.write(Paths.get("src/main/resources/static/index.html"), htmlContent.getBytes());
    }

    public void generateConfig() throws IOException {
        String configContent = """
        spring.datasource.url=jdbc:h2:mem:leftovers
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.jpa.hibernate.ddl-auto=update
        spring.h2.console.enabled=true
        """;
        
        Files.write(Paths.get("src/main/resources/application.properties"), configContent.getBytes());
    }
}
```
