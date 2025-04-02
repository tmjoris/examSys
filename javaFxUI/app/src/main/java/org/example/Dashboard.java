package org.example;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Dashboard extends Application {
    private String userName;
    private String userRole;
    private Stage primaryStage;

    public Dashboard(Stage primaryStage, String name, String role) {
        this.primaryStage = primaryStage;
        this.userName = name;
        this.userRole = role;
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

        Scene scene = new Scene(root, 1200, 800);
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
                return new Button[]{createSidebarButton("Exams"), createSidebarButton("Grades")};
            case "Instructor":
                Button makeExamButton = createSidebarButton("Make Exam");
                makeExamButton.setOnAction(e -> loadMakeExamScene(primaryStage, this.userName, this.userRole)); 
                return new Button[]{makeExamButton, createSidebarButton("Student Grades")};
            case "admin":
                return new Button[]{createSidebarButton("Add Student"), createSidebarButton("Add Instructor")};
            default:
                return new Button[]{};
        }
    }

    private VBox createContentArea() {
        VBox contentArea = new VBox(10);
        contentArea.setStyle("-fx-background-color: white; -fx-padding: 20;");

        return contentArea;
    }

    private VBox createActivityItem(String time, String course, String description, String details) {
        VBox item = new VBox(2);
        Label timeLabel = new Label(time);
        Label courseLabel = new Label(course);
        Label descriptionLabel = new Label(description);
        Label detailsLabel = new Label(details);

        timeLabel.setStyle("-fx-text-fill: black;");
        courseLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-text-fill: black;");
        detailsLabel.setStyle("-fx-text-fill: black;");

        item.getChildren().addAll(timeLabel, courseLabel, descriptionLabel, detailsLabel);
        return item;
    }

     private void loadMakeExamScene(Stage primaryStage, String name, String role) {
        Platform.runLater(() -> {
            try {
                new MakeExamScene(primaryStage, name, role).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}