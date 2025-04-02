package com.examSys.examApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.examSys.examApp.repositories.ExamRepository;
import com.examSys.examApp.repositories.QuestionRepository;
import com.examSys.examApp.dto.QuestionDTO;
import com.examSys.examApp.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/questions")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    @PostMapping("/add")
    public Question addQuestion(@RequestBody QuestionDTO questionDTO) {
        logger.info("Received QuestionDTO: {}", questionDTO);
        Exam exam = examRepository.findById(questionDTO.getExamId())
                                  .orElseThrow(() -> new RuntimeException("Exam not found"));

        Question question = new Question();
        question.setExam(exam);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setOptions(questionDTO.getOptions()); // Store list of options
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());

        return questionRepository.save(question);
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint reached!";
    }

    @GetMapping("/")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}
