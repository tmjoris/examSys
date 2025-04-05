package com.examSys.examApp.repositories;

import com.examSys.examApp.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}
