// MainApp.java
package com.leftovers.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class MainApp extends Application {
    private RestTemplate restTemplate = new RestTemplate();
    private double userLat = 51.505; // Default user location
    private double userLon = -0.09;
    private static final String API_URL = "http://localhost:8080/api/food";

    public static void launchApp() {
        Application.launch(MainApp.class);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Leftovers - Free Food Sharing");

        // Main layout
        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();

        // Tab 1: Post Food
        VBox postFoodTab = createPostFoodTab();
        Tab postTab = new Tab("Post Food", postFoodTab);
        postTab.setClosable(false);

        // Tab 2: View Nearby Food
        VBox viewFoodTab = createViewFoodTab();
        Tab viewTab = new Tab("View Nearby", viewFoodTab);
        viewTab.setClosable(false);

        tabPane.getTabs().addAll(postTab, viewTab);
        root.setCenter(tabPane);

        // Scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPostFoodTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        titleField.setPromptText("Food Title");
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);
        TextField latField = new TextField(String.valueOf(userLat));
        latField.setPromptText("Latitude");
        TextField lonField = new TextField(String.valueOf(userLon));
        lonField.setPromptText("Longitude");
        Button postButton = new Button("Post Food");

        postButton.setOnAction(e -> {
            try {
                FoodItem foodItem = new FoodItem();
                foodItem.setTitle(titleField.getText());
                foodItem.setDescription(descField.getText());
                foodItem.setLatitude(Double.parseDouble(latField.getText()));
                foodItem.setLongitude(Double.parseDouble(lonField.getText()));
                foodItem.setPostedAt(LocalDateTime.now());
                foodItem.setAvailable(true);

                restTemplate.postForObject(API_URL, foodItem, FoodItem.class);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Food posted successfully!");
                titleField.clear();
                descField.clear();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to post food: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(
                new Label("Share Food"), titleField, descField,
                new Label("Location"), latField, lonField, postButton
        );
        return vbox;
    }

    private VBox createViewFoodTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        TextField latField = new TextField(String.valueOf(userLat));
        latField.setPromptText("Your Latitude");
        TextField lonField = new TextField(String.valueOf(userLon));
        lonField.setPromptText("Your Longitude");
        TextField distanceField = new TextField("10");
        distanceField.setPromptText("Distance (km)");
        Button searchButton = new Button("Search Nearby");
        ListView<String> foodList = new ListView<>();
        Canvas mapCanvas = new Canvas(400, 200);

        searchButton.setOnAction(e -> {
            try {
                double lat = Double.parseDouble(latField.getText());
                double lon = Double.parseDouble(lonField.getText());
                double distance = Double.parseDouble(distanceField.getText());
                userLat = lat;
                userLon = lon;

                FoodItem[] listings = restTemplate.getForObject(
                        API_URL + "/nearby?latitude={lat}&longitude={lon}&distance={dist}",
                        FoodItem[].class, lat, lon, distance
                );

                foodList.getItems().clear();
                if (listings != null) {
                    for (FoodItem item : listings) {
                        foodList.getItems().add(
                                item.getId() + ": " + item.getTitle() + " - " + item.getDescription() +
                                " (" + item.getLatitude() + ", " + item.getLongitude() + ")"
                        );
                    }
                    drawMap(mapCanvas, listings, lat, lon);
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load listings: " + ex.getMessage());
            }
        });

        foodList.setOnMouseClicked(e -> {
            String selected = foodList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Long id = Long.parseLong(selected.split(":")[0]);
                try {
                    restTemplate.postForObject(API_URL + "/{id}/claim", null, Void.class, id);
                    searchButton.fire(); // Refresh list
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Food claimed!");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to claim food: " + ex.getMessage());
                }
            }
        });

        vbox.getChildren().addAll(
                new Label("Find Nearby Food"), latField, lonField, distanceField, searchButton,
                new Label("Available Food"), foodList, mapCanvas
        );
        return vbox;
    }

    private void drawMap(Canvas canvas, FoodItem[] listings, double userLat, double userLon) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Simple map: center is user location, scale coordinates
        double scale = 10; // Pixels per degree for visualization
        gc.fillOval(200 - 5, 100 - 5, 10, 10); // User location (center)

        if (listings != null) {
            for (FoodItem item : listings) {
                double x = 200 + (item.getLongitude() - userLon) * scale;
                double y = 100 - (item.getLatitude() - userLat) * scale; // Invert y for canvas
                gc.fillRect(x - 3, y - 3, 6, 6); // Food item as square
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
