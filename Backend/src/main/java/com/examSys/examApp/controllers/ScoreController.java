package com.examSys.examApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.examSys.examApp.models.Score;
import com.examSys.examApp.models.Exam;
import com.examSys.examApp.repositories.ScoreRepository;
import com.examSys.examApp.repositories.ExamRepository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ExamRepository examRepository;

    @GetMapping("/student/{email}")
    public ResponseEntity<List<Map<String, Object>>> getScoresForStudent(@PathVariable String email) {
        List<Score> scores = scoreRepository.findByStudentEmail(email);
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Score score : scores) {
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("score", score.getScore());
            scoreData.put("studentName", score.getStudentName());
            scoreData.put("studentEmail", score.getStudentEmail());
            scoreData.put("examName", score.getExam().getExamName()); // Assuming Exam has getName()
            scoreData.put("examId", score.getExam().getId());

            Exam exam = examRepository.findById(score.getExam().getId()).orElse(null);
            if (exam != null && exam.getQuestions() != null) {
                scoreData.put("totalQuestions", exam.getQuestions().size());
            } else {
                scoreData.put("totalQuestions", "N/A");
            }
            response.add(scoreData);
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
