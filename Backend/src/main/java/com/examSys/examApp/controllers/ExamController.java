package com.examSys.examApp.controllers;

import com.examSys.examApp.models.*;
import com.examSys.examApp.repositories.*;
import com.examSys.examApp.dto.QuestionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ScoreRepository scoreRepository;

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

    //updates an exam question
    @PutMapping("/{examId}/questions/{questionId}/update")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @RequestBody QuestionDTO questionDTO) {

        Exam exam = examRepository.findById(examId)
                .orElse(null);

        if (exam == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Question existingQuestion = questionRepository.findById(questionId)
                .orElse(null);

        if (existingQuestion == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!existingQuestion.getExam().getId().equals(examId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        existingQuestion.setExam(exam);
        existingQuestion.setQuestionText(questionDTO.getQuestionText());
        existingQuestion.setOptions(questionDTO.getOptions());
        existingQuestion.setCorrectAnswer(questionDTO.getCorrectAnswer());

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }


     // Get all questions for a specific exam
    @GetMapping("/{examId}/questions")
    public List<Question> getQuestionsForExam(@PathVariable Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new RuntimeException("Exam not found"));
        return questionRepository.findByExam(exam);
    }
    
    @PostMapping("/{examId}/scores")
    public ResponseEntity<Score> submitScore(@PathVariable Long examId, @RequestBody Map<String, Object> payload) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Score score = new Score();
        score.setExam(exam);
        score.setScore((int) payload.get("score"));
        score.setStudentName((String) payload.get("studentName"));
        score.setStudentEmail((String) payload.get("studentEmail"));
        Score savedScore = scoreRepository.save(score);
        return new ResponseEntity<>(savedScore, HttpStatus.CREATED);
    }
   
    
    // Get all exams for a student
    @GetMapping("/")
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
}
