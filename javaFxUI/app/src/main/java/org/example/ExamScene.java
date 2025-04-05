package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Screen;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import com.google.gson.JsonObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javafx.scene.input.KeyCombination;

public class ExamScene {

    private Stage primaryStage;
    private long examId;
    private String name;
    private String role;
    private Scene scene;
    private Map<Long, String> studentChoices = new HashMap<>(); // Store student choices
    private Map<Long, String> correctAnswers = new HashMap<>(); // Store correct answers
    private String examName;
    private String email;
    
    public ExamScene(Stage primaryStage, long examId, String name, String role, String examName, String email) {
        this.primaryStage = primaryStage;
        this.examId = examId;
        this.name = name;
        this.role = role;
        this.examName = examName;
        this.email = email;
        initializeScene();
    }

    private void initializeScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label examTitleLabel = new Label("Exam Questions");
        layout.getChildren().add(examTitleLabel);

        // Fetch questions and add them to the layout
        fetchAndDisplayQuestions(layout);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> submitAnswers());
        layout.getChildren().add(submitButton);
        
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox mainLayout = new VBox(scrollPane);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        scene = new Scene(mainLayout, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
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
                    long questionId = question.get("id").getAsLong(); // Get question ID
                    String questionText = question.get("questionText").getAsString();
                    JsonArray optionsArray = question.getAsJsonArray("options");
                    String correctAnswer = question.get("correctAnswer").getAsString(); // Get correct answer

                    correctAnswers.put(questionId, correctAnswer); // Store correct answer

                    Label questionLabel = new Label(questionText);
                    layout.getChildren().add(questionLabel);

                    ToggleGroup optionsGroup = new ToggleGroup(); // Group radio buttons

                    for (int i = 0; i < optionsArray.size(); i++) {
                        String option = optionsArray.get(i).getAsString();
                        RadioButton optionButton = new RadioButton(option);
                        optionButton.setToggleGroup(optionsGroup);
                        optionButton.setUserData(option); // Store option text as user data
                        layout.getChildren().add(optionButton);

                        // Listen for selection changes and store the choice
                        optionButton.setOnAction(e -> {
                            RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
                            if (selectedOption != null) {
                                studentChoices.put(questionId, selectedOption.getUserData().toString());
                            }
                        });
                    }

                    layout.getChildren().add(new Label("")); // Add spacing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load questions: " + e.getMessage());
            layout.getChildren().add(errorLabel);
        }
    }

    private void submitAnswers() {
        int score = calculateScore();
        sendScoreToServer(score);
        Platform.runLater(() -> loadDashboardScene()); // Switch to dashboard
    }

    private int calculateScore() {
        int score = 0;
        for (Map.Entry<Long, String> entry : studentChoices.entrySet()) {
            long questionId = entry.getKey();
            String studentAnswer = entry.getValue();
            String correctAnswer = correctAnswers.get(questionId);

            // Map student answer text to letter
            String studentAnswerLetter = getOptionLetter(questionId, studentAnswer);

            if (studentAnswerLetter != null && studentAnswerLetter.equals(correctAnswer)) {
                score++;
            }
        }
        return score;
    }

    // Helper method to get the option letter
    private String getOptionLetter(long questionId, String studentAnswer) {
        try {
            List<JsonObject> questions = new ExamService().fetchQuestionsForExam(examId);
            for (JsonObject question : questions) {
                if (question.get("id").getAsLong() == questionId) {
                    JsonArray optionsArray = question.getAsJsonArray("options");
                    for (int i = 0; i < optionsArray.size(); i++) {
                        if (optionsArray.get(i).getAsString().equals(studentAnswer)) {
                            return String.valueOf((char) ('A' + i)); // Convert index to A, B, C, D
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    private void sendScoreToServer(int score) {
        try {
            URL url = new URL("http://localhost:8080/api/exams/" + examId + "/scores");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("score", score);
            jsonObject.addProperty("studentEmail", email);
            jsonObject.addProperty("studentName", name);

            String jsonInput = jsonObject.toString();

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Score submitted successfully!");
            } else {
                System.out.println("Failed to submit score: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadDashboardScene() {
        try {
            new Dashboard(primaryStage, name, role, email, examName).start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
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