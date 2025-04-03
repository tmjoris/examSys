package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StudentExamScene extends Application {
    private Stage primaryStage;
    private String name;
    private String role;

    public StudentExamScene(Stage primaryStage, String name, String role){
        this.primaryStage = primaryStage;
        this.name = name;
        this.role = role;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Available Exams");
        showAvailableExams();
    }

    private void showAvailableExams() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Label examsLabel = new Label("Available Exams:");
        vbox.getChildren().add(examsLabel);

        JsonArray exams = fetchExams();
        if (exams.size() == 0) {
            Label noExamsLabel = new Label("No exams available.");
            vbox.getChildren().add(noExamsLabel);
        } else {
            for (int i = 0; i < exams.size(); i++) {
                JsonObject exam = exams.get(i).getAsJsonObject();
                Button examButton = new Button(exam.get("examName").getAsString());
                examButton.setOnAction(e -> takeExam(primaryStage, exam.get("id").getAsLong(), name, role));
                vbox.getChildren().add(examButton);
            }
        }

        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private JsonArray fetchExams() {
        try {
            URL url = new URL("http://localhost:8080/api/exams");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = conn.getInputStream();
                Scanner scanner = new Scanner(responseStream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                return JsonParser.parseString(response).getAsJsonArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    private void takeExam(Stage primaryStage, long examId, String name, String role) {
        System.out.println("Taking Exam ID: " + examId);
        ExamScene examScene = new ExamScene(primaryStage, examId, name, role);
        primaryStage.setScene(examScene.getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
