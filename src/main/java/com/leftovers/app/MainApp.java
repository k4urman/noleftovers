package com.noleftovers.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;

public class MainApp extends Application {
    
    private static final String BASE_URL = "http://localhost:8080/api/food";
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private ListView<FoodItemDisplay> foodListView;
    private ObservableList<FoodItemDisplay> foodItems;
    private Canvas mapCanvas;
    private double userLat = Location.DEFAULT_LATITUDE;
    private double userLng = Location.DEFAULT_LONGITUDE;
    private double searchRadius = Location.DEFAULT_SEARCH_RADIUS;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NoLeftovers - Food Sharing App");
        
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createPostFoodTab(), createViewNearbyTab());
        
        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize food list
        foodItems = FXCollections.observableArrayList();
        loadNearbyFood();
    }
    
    private Tab createPostFoodTab() {
        Tab tab = new Tab("Post Food");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Food Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("e.g., Fresh Apples");
        
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the food item...");
        descArea.setPrefRowCount(3);
        
        Label latLabel = new Label("Latitude:");
        TextField latField = new TextField(String.valueOf(Location.DEFAULT_LATITUDE));
        
        Label lngLabel = new Label("Longitude:");
        TextField lngField = new TextField(String.valueOf(Location.DEFAULT_LONGITUDE));
        
        Button postButton = new Button("Post Food");
        postButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String description = descArea.getText().trim();
                double lat = Double.parseDouble(latField.getText());
                double lng = Double.parseDouble(lngField.getText());
                
                if (title.isEmpty()) {
                    showAlert("Error", "Title is required");
                    return;
                }
                
                if (!Location.isValidCoordinates(lat, lng)) {
                    showAlert("Error", "Invalid coordinates");
                    return;
                }
                
                postFoodItem(title, description, lat, lng);
                
                // Clear form
                titleField.clear();
                descArea.clear();
                latField.setText(String.valueOf(Location.DEFAULT_LATITUDE));
                lngField.setText(String.valueOf(Location.DEFAULT_LONGITUDE));
                
                showAlert("Success", "Food item posted successfully!");
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid coordinates format");
            }
        });
        
        content.getChildren().addAll(
            titleLabel, titleField,
            descLabel, descArea,
            latLabel, latField,
            lngLabel, lngField,
            postButton
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private Tab createViewNearbyTab() {
        Tab tab = new Tab("View Nearby");
        tab.setClosable(false);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Search controls
        HBox searchBox = new HBox(10);
        
        Label latLabel = new Label("Your Latitude:");
        TextField userLatField = new TextField(String.valueOf(userLat));
        userLatField.setPrefWidth(100);
        
        Label lngLabel = new Label("Longitude:");
        TextField userLngField = new TextField(String.valueOf(userLng));
        userLngField.setPrefWidth(100);
        
        Label radiusLabel = new Label("Distance (km):");
        TextField radiusField = new TextField(String.valueOf(searchRadius));
        radiusField.setPrefWidth(80);
        
        Button searchButton = new Button("Search Nearby");
        searchButton.setOnAction(e -> {
            try {
                userLat = Double.parseDouble(userLatField.getText());
                userLng = Double.parseDouble(userLngField.getText());
                searchRadius = Double.parseDouble(radiusField.getText());
                
                if (!Location.isValidCoordinates(userLat, userLng)) {
                    showAlert("Error", "Invalid coordinates");
                    return;
                }
                
                loadNearbyFood();
                updateMap();
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid number format");
            }
        });
        
        searchBox.getChildren().addAll(
            latLabel, userLatField,
            lngLabel, userLngField,
            radiusLabel, radiusField,
            searchButton
        );
        
        // Food list
        foodListView = new ListView<>();
        foodListView.setPrefHeight(200);
        foodListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FoodItemDisplay selected = foodListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    claimFood(selected.getId());
                }
            }
        });
        
        // Map canvas
        mapCanvas = new Canvas(400, 300);
        mapCanvas.setStyle("-fx-border-color: black;");
        
        Label mapLabel = new Label("Map (Circle = You, Squares = Food):");
        Label instructionLabel = new Label("Double-click a food item in the list to claim it");
        
        content.getChildren().addAll(
            searchBox,
            new Label("Available Food Items:"),
            foodListView,
            instructionLabel,
            mapLabel,
            mapCanvas
        );
        
        tab.setContent(content);
        
        return tab;
    }
    
    private void postFoodItem(String title, String description, double lat, double lng) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            String jsonInput = String.format(
                "{\"title\":\"%s\",\"description\":\"%s\",\"latitude\":%f,\"longitude\":%f}",
                title, description, lat, lng
            );
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                loadNearbyFood(); // Refresh list
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to post food item: " + ex.getMessage());
        }
    }
    
    private void loadNearbyFood() {
        try {
            String urlStr = String.format("%s/nearby?lat=%f&lng=%f&distance=%f", 
                BASE_URL, userLat, userLng, searchRadius);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse JSON response
                JsonNode jsonArray = objectMapper.readTree(response.toString());
                List<FoodItemDisplay> items = new ArrayList<>();
                
                for (JsonNode node : jsonArray) {
                    FoodItemDisplay item = new FoodItemDisplay(
                        node.get("id").asLong(),
                        node.get("title").asText(),
                        node.get("description").asText(),
                        node.get("latitude").asDouble(),
                        node.get("longitude").asDouble(),
                        node.get("available").asBoolean()
                    );
                    if (item.isAvailable()) {
                        items.add(item);
                    }
                }
                
                Platform.runLater(() -> {
                    foodItems.clear();
                    foodItems.addAll(items);
                    if (foodListView != null) {
                        foodListView.setItems(foodItems);
                    }
                    updateMap();
                });
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void claimFood(Long foodId) {
        try {
            URL url = new URL(BASE_URL + "/" + foodId + "/claim");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            
            if (conn.getResponseCode() == 200) {
                showAlert("Success", "Food claimed successfully!");
                loadNearbyFood(); // Refresh list
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to claim food: " + ex.getMessage());
        }
    }
    
    private void updateMap() {
        if (mapCanvas == null) return;
        
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Draw background
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        // Calculate bounds for mapping coordinates to canvas
        double minLat = userLat - searchRadius / 111.0; // Rough conversion
        double maxLat = userLat + searchRadius / 111.0;
        double minLng = userLng - searchRadius / (111.0 * Math.cos(Math.toRadians(userLat)));
        double maxLng = userLng + searchRadius / (111.0 * Math.cos(Math.toRadians(userLat)));
        
        // Draw user location (circle)
        gc.setFill(Color.BLUE);
        double userX = mapCoordToCanvas(userLng, minLng, maxLng, mapCanvas.getWidth());
        double userY = mapCoordToCanvas(userLat, minLat, maxLat, mapCanvas.getHeight());
        gc.fillOval(userX - 5, userY - 5, 10, 10);
        
        // Draw food items (squares)
        gc.setFill(Color.RED);
        for (FoodItemDisplay food : foodItems) {
            double foodX = mapCoordToCanvas(food.getLongitude(), minLng, maxLng, mapCanvas.getWidth());
            double foodY = mapCoordToCanvas(food.getLatitude(), minLat, maxLat, mapCanvas.getHeight());
            gc.fillRect(foodX - 3, foodY - 3, 6, 6);
        }
    }
    
    private double mapCoordToCanvas(double coord, double min, double max, double canvasSize) {
        return ((coord - min) / (max - min)) * canvasSize;
    }
    
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

class FoodItemDisplay {
    private Long id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private boolean available;
    
    public FoodItemDisplay(Long id, String title, String description, 
                          double latitude, double longitude, boolean available) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isAvailable() { return available; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%.3f, %.3f)", 
            title, description, latitude, longitude);
    }
}
