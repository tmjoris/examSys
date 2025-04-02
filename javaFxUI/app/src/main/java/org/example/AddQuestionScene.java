package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;

public class AddQuestionScene {
    private Stage primaryStage;
    private long examId;
    private Scene scene;
    private String name;
    private String role;

    public AddQuestionScene(Stage primaryStage, long examId, String name, String role) {
        this.primaryStage = primaryStage;
        this.examId = examId;
        this.name = name;
        this.role = role;
        initializeScene();
    }

    private void initializeScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label questionLabel = new Label("Enter Question:");
        TextField questionField = new TextField();

        Label optionALabel = new Label("Option A:");
        TextField optionAField = new TextField();

        Label optionBLabel = new Label("Option B:");
        TextField optionBField = new TextField();

        Label optionCLabel = new Label("Option C:");
        TextField optionCField = new TextField();

        Label optionDLabel = new Label("Option D:");
        TextField optionDField = new TextField();

        Label correctAnswerLabel = new Label("Correct Answer (A/B/C/D):");
        ComboBox<String> correctAnswerBox = new ComboBox<>();
        correctAnswerBox.getItems().addAll("A", "B", "C", "D");

        Button addQuestionButton = new Button("Add Question");
        addQuestionButton.setOnAction(e -> {
            addQuestion(
                questionField.getText(),
                optionAField.getText(),
                optionBField.getText(),
                optionCField.getText(),
                optionDField.getText(),
                correctAnswerBox.getValue()
            );
        });

        Button finishButton = new Button("Finish");
        finishButton.setOnAction(e -> returnToDashboard(name, role));

        layout.getChildren().addAll(
            questionLabel, questionField,
            optionALabel, optionAField,
            optionBLabel, optionBField,
            optionCLabel, optionCField,
            optionDLabel, optionDField,
            correctAnswerLabel, correctAnswerBox,
            addQuestionButton, finishButton
        );

        scene = new Scene(layout, 400, 500);
    }

    private void addQuestion(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        try {
            URL url = new URL("http://localhost:8080/api/questions/add");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("examId", examId);
            jsonObject.addProperty("questionText", question);
            jsonObject.addProperty("optionA", optionA);
            jsonObject.addProperty("optionB", optionB);
            jsonObject.addProperty("optionC", optionC);
            jsonObject.addProperty("optionD", optionD);
            jsonObject.addProperty("correctAnswer", correctAnswer);

            String jsonInput = jsonObject.toString();

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Question added successfully!");
            } else {
                System.out.println("Failed to add question: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToDashboard(String name, String role) {
        Platform.runLater(() -> {
            try {
                new Dashboard(primaryStage, name, role).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}
