package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.util.Scanner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MakeExamScene extends Application {

    private Stage primaryStage;
    private String name;
    private String role;

    public MakeExamScene(Stage primaryStage, String name, String role) {
        this.primaryStage = primaryStage;
        this.name = name;
        this.role = role;
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Make Exam");

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        // Course selection
        Label courseLabel = new Label("Course:");
        TextField courseField = new TextField();
        courseField.setPromptText("Enter course name");

        // Exam name input
        Label examNameLabel = new Label("Exam Name:");
        TextField examNameField = new TextField();
        examNameField.setPromptText("Enter exam name");

        // Button to submit exam
        Button submitButton = new Button("Create Exam");
        submitButton.setOnAction(e -> createExam(courseField.getText(), examNameField.getText()));

        vbox.getChildren().addAll(courseLabel, courseField, examNameLabel, examNameField, submitButton);

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createExam(String course, String examName) {
        try {
            URL url = new URL("http://localhost:8080/api/exams/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format("{\"course\":\"%s\", \"examName\":\"%s\"}", course, examName);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    Scanner scanner = new Scanner(conn.getInputStream());
                    String responseBody = scanner.useDelimiter("\\A").next();
                    scanner.close();

                    JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
                    long examId = responseJson.get("id").getAsLong();  // Extract exam ID

                    System.out.println("Exam Created Successfully! ID: " + examId);

                    Platform.runLater(() -> loadAddQuestionScene(primaryStage, examId, name, role));
            } else {
                System.out.println("Failed to create exam: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadAddQuestionScene(Stage primaryStage, long examId, String name, String role) {
        AddQuestionScene addQuestionScene = new AddQuestionScene(primaryStage, examId, name, role);
        primaryStage.setScene(addQuestionScene.getScene());
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Scene getScene() {
        return primaryStage.getScene();
    }
}
