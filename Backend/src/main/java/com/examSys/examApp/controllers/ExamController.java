package com.examSys.examApp.controllers;

import com.examSys.examApp.models.Exam;
import com.examSys.examApp.models.Question;
import com.examSys.examApp.repositories.ExamRepository;
import com.examSys.examApp.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private QuestionRepository questionRepository;

    // Create a new exam
    @PostMapping("/create")
    public Exam createExam(@RequestBody Exam exam) {
        return examRepository.save(exam);
    }

    // Add a question to an exam
    @PostMapping("/{examId}/questions")
    public Question addQuestion(@PathVariable Long examId, @RequestBody Question question) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new RuntimeException("Exam not found"));
        question.setExam(exam);
        return questionRepository.save(question);
    }

     // Get all questions for a specific exam
    @GetMapping("/{examId}/questions")
    public List<Question> getQuestionsForExam(@PathVariable Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new RuntimeException("Exam not found"));
        return questionRepository.findByExam(exam);
    }

    // Get all exams for a student
    @GetMapping("/")
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
}
