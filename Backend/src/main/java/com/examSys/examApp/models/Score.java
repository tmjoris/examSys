package com.examSys.examApp.models;
import jakarta.persistence.*;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int score;
    private String studentName;
    private String studentEmail;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    // Add student information or user ID if needed
    // private Long studentId;

    // Constructors, getters, and setters
    public Score() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
     public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getStudentEmail(){
        return studentEmail;
    }
    
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
    // public Long getStudentId() {
    //     return studentId;
    // }

    // public void setStudentId(Long studentId) {
    //     this.studentId = studentId;
    // }
}