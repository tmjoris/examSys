package com.examSys.examApp.repositories;

import com.examSys.examApp.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
