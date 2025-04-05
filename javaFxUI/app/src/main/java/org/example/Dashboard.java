package org.example;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Dashboard extends Application {
    private String userName;
    private String userRole;
    private String email;
    private Stage primaryStage;
    private String completedExamName;

    public Dashboard(Stage primaryStage, String name, String role, String email) {
        this.primaryStage = primaryStage;
        this.userName = name;
        this.userRole = role;
        this.email = email;
    }
    
    public Dashboard(Stage primaryStage, String name, String role, String email, String completedExamName) {
        this.primaryStage = primaryStage;
        this.userName = name;
        this.userRole = role;
        this.email = email;
        this.completedExamName = completedExamName;
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dashboard");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #333333;");

        // Left Sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Top Navigation
        HBox topNav = createTopNav();
        root.setTop(topNav);

        // Content Area (Placeholder)
        VBox contentArea = createContentArea();
        root.setCenter(contentArea);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #282828;");
        sidebar.setPadding(new Insets(20));

        Label logoLabel = new Label("USIU\nAFRICA");
        logoLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 24));
        logoLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(userName);
        nameLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-pref-width: 150; -fx-alignment: baseline-left;");

        VBox buttonContainer = new VBox(10);
        buttonContainer.getChildren().addAll(getRoleButtons());

        Button signOutButton = createSidebarButton("Sign Out");
        signOutButton.setOnAction(event -> signOutButtonHandler());

        sidebar.getChildren().addAll(nameLabel, buttonContainer, signOutButton);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-pref-width: 150; -fx-alignment: baseline-left;");
        return button;
    }

    private void signOutButtonHandler() {
    this.userName = ""; 
    this.userRole = ""; 
    this.email = "";

  
    Platform.runLater(() -> {
        try {
            new Login().start(primaryStage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    }

    private HBox createTopNav() {
        HBox topNav = new HBox();
        topNav.setStyle("-fx-background-color: #333333; -fx-padding: 10;");

        Button menuButton = new Button("☰");
        menuButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        Label showAllLabel = new Label("Show All");
        showAllLabel.setStyle("-fx-text-fill: white;");

        topNav.getChildren().addAll(menuButton, showAllLabel);
        topNav.setAlignment(Pos.CENTER_RIGHT);
        topNav.setSpacing(10);
        return topNav;
    }

    private Button[] getRoleButtons() {
        switch (userRole) {
            case "Student":
                Button studentExamButton = createSidebarButton("Exams");
                studentExamButton.setOnAction(e -> loadStudentExamScene(primaryStage, this.userName, this.userRole, email));
                return new Button[]{studentExamButton};
            case "Instructor":
                Button makeExamButton = createSidebarButton("Make Exam");
                makeExamButton.setOnAction(e -> loadMakeExamScene(primaryStage, this.userName, this.userRole, email)); 
                return new Button[]{makeExamButton, createSidebarButton("Student Grades")};
            case "admin":
                Button addStudentButton = createSidebarButton("Add Student");
                addStudentButton.setOnAction(e -> loadCreateUserScene("Student")); // Pass the role
                Button addInstructorButton = createSidebarButton("Add Instructor");
                addInstructorButton.setOnAction(e -> loadCreateUserScene("Instructor")); // Pass the role
                return new Button[]{addStudentButton, addInstructorButton};            default:
                return new Button[]{};
        }
    }

    private VBox createContentArea() {
        VBox contentArea = new VBox(10);
        contentArea.setStyle("-fx-background-color: white; -fx-padding: 20;");
        if("Student".equals(this.userRole)){
            if (completedExamName != null) {
                Label completedExamLabel = new Label("Completed Exam: " + completedExamName);
                contentArea.getChildren().add(completedExamLabel);
            }

            fetchAndDisplayScores(contentArea);
        }
        return contentArea;
    }
    
    private void loadCreateUserScene(String role) {
        VBox createUserLayout = new VBox(10);
        createUserLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Create New " + role);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label emailLabel = new Label("Enter " +role +"'s email");
        emailLabel.setFont(Font.font("Arial", 13));
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Label passwordLabel = new Label("Enter Password");
        passwordLabel.setFont(Font.font("Arial", 13));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label nameLabel = new Label("Enter " +role +"'s name");
        nameLabel.setFont(Font.font("Arial", 13));
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button createButton = new Button("Create " + role);
        Label messageLabel = new Label();

        createButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                messageLabel.setText("All fields are required.");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Make HTTP POST request to /api/admin/createUser
            AdminService adminService = new AdminService();
            try {
                Map<String, Object> response = adminService.createUser(email, password, name, role);
                if ((boolean) response.get("success")) {
                    messageLabel.setText((String) response.get("message"));
                    messageLabel.setTextFill(Color.GREEN);
                    // Optionally clear the form fields
                    emailField.clear();
                    passwordField.clear();
                    nameField.clear();
                } else {
                    messageLabel.setText((String) response.get("message"));
                    messageLabel.setTextFill(Color.RED);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("Failed to create user: " + ex.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        });
        
        Button backToDashboardButton = new Button("Back to Dashboard");
            backToDashboardButton.setOnAction(e -> {
            start(primaryStage);
        });
        
        createUserLayout.getChildren().addAll(
                titleLabel, 
                emailLabel,
                emailField, 
                passwordLabel, 
                passwordField, 
                nameLabel, 
                nameField, 
                createButton, 
                messageLabel,
                backToDashboardButton);
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene createUserScene = new Scene(createUserLayout, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(createUserScene);
        primaryStage.setTitle("Create " + role);
    }
    
    
     private class AdminService {
        public Map<String, Object> createUser(String email, String password, String name, String role) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/admin/createUser"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("{\"email\":\"%s\", \"password\":\"%s\", \"name\":\"%s\", \"role\":\"%s\"}", email, password, name, role),
                            StandardCharsets.UTF_8))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), Map.class);
        }
    }

    
    private void fetchAndDisplayScores(VBox contentArea) {
        ScoreService scoreService = new ScoreService();
        Label scoresTitle = new Label("SCORES");
        scoresTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;"); // Basic styling
        contentArea.getChildren().add(scoresTitle);
        try {
            List<JsonObject> scores = scoreService.fetchScoresForStudent(email);

            if (scores.isEmpty()) {
                Label noScoresLabel = new Label("No scores found.");
                contentArea.getChildren().add(noScoresLabel);
            } else {
                Label previousScoresLabel = new Label("Previous Scores:");
                contentArea.getChildren().add(previousScoresLabel);
                for (JsonObject scoreData : scores) {
                    String examName = scoreData.get("examName").getAsString();
                    int scoreValue = scoreData.get("score").getAsInt();
                    JsonElement totalQuestionsElement = scoreData.get("totalQuestions");
                    String totalQuestions = "N/A";
                    if (totalQuestionsElement != null && !totalQuestionsElement.isJsonNull()) {
                        totalQuestions = totalQuestionsElement.getAsString();
                    }
                    Label scoreLabel = new Label(examName + ": " + scoreValue + " / " + totalQuestions);
                    contentArea.getChildren().add(scoreLabel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load scores: " + e.getMessage());
            contentArea.getChildren().add(errorLabel);
        }
    }

     private void loadMakeExamScene(Stage primaryStage, String name, String role, String email) {
        Platform.runLater(() -> {
            try {
                new MakeExamScene(primaryStage, name, role, email).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadStudentExamScene(Stage primaryStage, String name, String role, String email) {
        Platform.runLater(() -> {
            try {
                new StudentExamScene(primaryStage, name, role, email).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private class ScoreService {
        public List<JsonObject> fetchScoresForStudent(String email) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/scores/student/" + email)) // Adjust endpoint as needed
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = new Gson().fromJson(response.body(), JsonArray.class);
                List<JsonObject> scores = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    scores.add(element.getAsJsonObject());
                }
                return scores;
            } else {
                throw new Exception("Failed to fetch scores: " + response.statusCode());
            }
        }
    }
}