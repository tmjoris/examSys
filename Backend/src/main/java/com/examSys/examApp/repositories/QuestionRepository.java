package com.examSys.examApp.repositories;
import java.util.List;
import com.examSys.examApp.models.Question;
import com.examSys.examApp.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExam(Exam exam); // Add this line
}
