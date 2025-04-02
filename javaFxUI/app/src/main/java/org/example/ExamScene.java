package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ExamScene {

    private Stage primaryStage;
    private long examId;
    private String name;
    private String role;
    private Scene scene;

    public ExamScene(Stage primaryStage, long examId, String name, String role) {
        this.primaryStage = primaryStage;
        this.examId = examId;
        this.name = name;
        this.role = role;
        initializeScene();
    }

    private void initializeScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label examTitleLabel = new Label("Exam Questions");
        layout.getChildren().add(examTitleLabel);

        // Fetch questions and add them to the layout
        fetchAndDisplayQuestions(layout);

        scene = new Scene(layout, 600, 400); // Adjust size as needed
    }

    private void fetchAndDisplayQuestions(VBox layout) {
        ExamService examService = new ExamService();
        try {
            List<JsonObject> questions = examService.fetchQuestionsForExam(examId);

            if (questions.isEmpty()) {
                Label noQuestionsLabel = new Label("No questions found for this exam.");
                layout.getChildren().add(noQuestionsLabel);
            } else {
                for (JsonObject question : questions) {
                    // Extract question details
                    String questionText = question.get("questionText").getAsString();
                    String optionA = question.get("optionA").getAsString();
                    String optionB = question.get("optionB").getAsString();
                    String optionC = question.get("optionC").getAsString();
                    String optionD = question.get("optionD").getAsString();

                    // Create UI elements to display the question
                    Label questionLabel = new Label(questionText);
                    Label optionALabel = new Label("A. " + optionA);
                    Label optionBLabel = new Label("B. " + optionB);
                    Label optionCLabel = new Label("C. " + optionC);
                    Label optionDLabel = new Label("D. " + optionD);

                    layout.getChildren().addAll(questionLabel, optionALabel, optionBLabel, optionCLabel, optionDLabel, new Label("")); // Add an empty label for spacing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load questions: " + e.getMessage());
            layout.getChildren().add(errorLabel);
        }
    }

    public Scene getScene() {
        return scene;
    }

    // Inner class for ExamService
    private class ExamService {
        public List<JsonObject> fetchQuestionsForExam(long examId) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/exams/" + examId + "/questions"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = new Gson().fromJson(response.body(), JsonArray.class);
                List<JsonObject> questions = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    questions.add(element.getAsJsonObject());
                }
                return questions;
            } else {
                throw new Exception("Failed to fetch questions: " + response.statusCode());
            }
        }
    }
}