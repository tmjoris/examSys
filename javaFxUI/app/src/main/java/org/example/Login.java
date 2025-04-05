package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.stage.Screen; 
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCombination;

public class Login extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("USIU Africa Exam System");
        loadLoginScene();
        primaryStage.show();
    }

    private void loadLoginScene() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #282828;");

        // USIU AFRICA text
        Label titleLabelTop = new Label("USIU");
        titleLabelTop.setFont(Font.font("Times New Roman", 76));
        titleLabelTop.setTextFill(Color.WHITE);
        titleLabelTop.setScaleY(2.0);
        vbox.getChildren().add(titleLabelTop);

        Text newline = new Text("\n");
        vbox.getChildren().add(newline);

        Label titleLabelBottom = new Label("AFRICA");
        titleLabelBottom.setFont(Font.font("Times New Roman", 50)); 
        titleLabelBottom.setTextFill(Color.WHITE);
        titleLabelBottom.setScaleY(2.0);        
        vbox.getChildren().add(titleLabelBottom);

        Text newlineTwo = new Text("\n");
        vbox.getChildren().add(newlineTwo);

        // GridPane for input fields and buttons
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

          // Username (Label and TextField in a VBox)
        VBox usernameBox = new VBox();
        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: white; -fx-text-fill: white;");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        grid.add(usernameBox, 1, 0);

        // Password (Label and PasswordField in a VBox)
        VBox passwordBox = new VBox();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-background-color: transparent; -fx-border-width: 0 0 1 0; -fx-border-color: white; -fx-text-fill: white;"); 
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        Text newlineThree = new Text("\n");
        passwordBox.getChildren().add(newlineThree);
        grid.add(passwordBox, 1, 1);      

        Button loginButton = new Button("Sign In");
        loginButton.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 1; -fx-text-fill: white;");
        loginButton.setPrefWidth(300);
        grid.add(loginButton, 1, 2);

        final Label messageLabel = new Label();
        messageLabel.setTextFill(Color.WHITE);
        grid.add(messageLabel, 1, 3);

        loginButton.setOnAction(e -> {
            String email = usernameField.getText();
            String password = passwordField.getText();
            performLogin(email, password, messageLabel);
        });

        vbox.getChildren().add(grid);
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(vbox, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
    }

    private void performLogin(String email, String password, Label messageLabel) {
        Task<String> loginTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                JsonObject json = new JsonObject();
                json.addProperty("email", email);
                json.addProperty("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            }
        };

        loginTask.setOnSucceeded(event -> {
            String response = loginTask.getValue();
            Gson gson = new Gson();
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);

            if (responseObject.has("success") && responseObject.get("success").getAsBoolean()) {
                // Extract name and role from response
                String name = responseObject.get("name").getAsString();
                String role = responseObject.get("role").getAsString();
                Platform.runLater(() -> loadDashboardScene(name, role, email));
            } else {
                Platform.runLater(() -> messageLabel.setText("Invalid credentials."));
            }
        });

        loginTask.setOnFailed(event -> {
            Platform.runLater(() -> messageLabel.setText("Login failed."));
        });

        new Thread(loginTask).start();
    }
    
    
    private void loadDashboardScene(String name, String role, String email) {
        Platform.runLater(() -> {
            try {
                new Dashboard(primaryStage, name, role, email).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
