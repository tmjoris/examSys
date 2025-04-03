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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class AddQuestionScene {
    private Stage primaryStage;
    private long examId;
    private Scene scene;
    private String name;
    private String role;
    private List<QuestionData> questions = new ArrayList<>();
    private int editingIndex = -1;
    private Long editingQuestionId = null;

    // Instance variables
    private TextField questionField;
    private TextField optionAField;
    private TextField optionBField;
    private TextField optionCField;
    private TextField optionDField;
    private ComboBox<String> correctAnswerBox;
    private ListView<String> questionListView;
    private Button updateQuestionButton;

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
        questionField = new TextField();

        Label optionALabel = new Label("Option A:");
        optionAField = new TextField();

        Label optionBLabel = new Label("Option B:");
        optionBField = new TextField();

        Label optionCLabel = new Label("Option C:");
        optionCField = new TextField();

        Label optionDLabel = new Label("Option D:");
        optionDField = new TextField();

        Label correctAnswerLabel = new Label("Correct Answer (A/B/C/D):");
        correctAnswerBox = new ComboBox<>();
        correctAnswerBox.getItems().addAll("A", "B", "C", "D");

        Button addQuestionButton = new Button("Add Question");
        addQuestionButton.setOnAction(e -> sendQuestionToServer(
            questionField.getText(),
            optionAField.getText(),
            optionBField.getText(),
            optionCField.getText(),
            optionDField.getText(),
            correctAnswerBox.getValue()
        ));

        Button addAnotherButton = new Button("Add Another Question");
        addAnotherButton.setOnAction(e -> clearFields());

        updateQuestionButton = new Button("Update Question");
        updateQuestionButton.setOnAction(e -> updateQuestion());
        updateQuestionButton.setVisible(false);

        Button finishButton = new Button("Finish");
        finishButton.setOnAction(e -> returnToDashboard(name, role));

        questionListView = new ListView<>();

        layout.getChildren().addAll(
            questionLabel, questionField,
            optionALabel, optionAField,
            optionBLabel, optionBField,
            optionCLabel, optionCField,
            optionDLabel, optionDField,
            correctAnswerLabel, correctAnswerBox,
            addQuestionButton, addAnotherButton, updateQuestionButton,
            questionListView, finishButton
        );

        scene = new Scene(layout, 400, 600);

        updateQuestionListView();
    }

    private void sendQuestionToServer(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        try {
            URL url = new URL("http://localhost:8080/api/exams/" + examId + "/questions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("questionText", question);
            JsonArray optionsArray = new JsonArray();
            optionsArray.add(optionA);
            optionsArray.add(optionB);
            optionsArray.add(optionC);
            optionsArray.add(optionD);

            jsonObject.add("options", optionsArray);
            jsonObject.addProperty("correctAnswer", correctAnswer);

            String jsonInput = jsonObject.toString();

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Question added successfully!");
                // Read the response body
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                JsonObject jsonResponse = com.google.gson.JsonParser.parseString(response.toString()).getAsJsonObject();
                long questionId = jsonResponse.get("id").getAsLong();

                // Create QuestionData object and add it to the list
                QuestionData addedQuestion = new QuestionData(question, optionA, optionB, optionC, optionD, correctAnswer);
                addedQuestion.questionId = questionId; // Store the questionId

                questions.add(addedQuestion);

                clearFields();
                updateQuestionListView();

            } else {
                System.out.println("Failed to add question: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateQuestionOnServer(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer, Long questionId) {
        try {
            URL url = new URL("http://localhost:8080/api/exams/" + examId + "/questions/" + questionId + "/update");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("questionText", question);
            JsonArray optionsArray = new JsonArray();
            optionsArray.add(optionA);
            optionsArray.add(optionB);
            optionsArray.add(optionC);
            optionsArray.add(optionD);

            jsonObject.add("options", optionsArray);
            jsonObject.addProperty("correctAnswer", correctAnswer);

            String jsonInput = jsonObject.toString();

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Question updated successfully!");
                clearFields();
            } else {
                System.out.println("Failed to update question: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateQuestion() {
        if (editingIndex >= 0 && editingIndex < questions.size()) {
            QuestionData updatedData = new QuestionData(questionField.getText(), optionAField.getText(), optionBField.getText(), optionCField.getText(), optionDField.getText(), correctAnswerBox.getValue());
            updatedData.questionId = questions.get(editingIndex).questionId;
            questions.set(editingIndex, updatedData);
            updateQuestionOnServer(updatedData.question, updatedData.optionA, updatedData.optionB, updatedData.optionC, updatedData.optionD, updatedData.correctAnswer, updatedData.questionId);
            editingIndex = -1;
            editingQuestionId = null;
            updateQuestionButton.setVisible(false);
            clearFields();
            updateQuestionListView();
        }
    }

    private void clearFields() {
        questionField.clear();
        optionAField.clear();
        optionBField.clear();
        optionCField.clear();
        optionDField.clear();
        correctAnswerBox.setValue(null);
    }

    private void updateQuestionListView() {
        questionListView.getItems().clear();
        for (int i = 0; i < questions.size(); i++) {
            int index = i;
            String questionText = questions.get(i).question;
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> {
                editingIndex = index;
                editingQuestionId = questions.get(index).questionId; // Store the questionId
                populateFields(questions.get(index));
                updateQuestionButton.setVisible(true);
            });
            questionListView.getItems().add(questionText + "  ");
            questionListView.getItems().add(" ");
            questionListView.getItems().add(editButton.getText());
        }
    }

    private void populateFields(QuestionData questionData) {
        questionField.setText(questionData.question);
        optionAField.setText(questionData.optionA);
        optionBField.setText(questionData.optionB);
        optionCField.setText(questionData.optionC);
        optionDField.setText(questionData.optionD);
        correctAnswerBox.setValue(questionData.correctAnswer);
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

    private static class QuestionData {
        String question;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        String correctAnswer;
        public Long questionId; // Added questionId field

        QuestionData(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }
    }
}