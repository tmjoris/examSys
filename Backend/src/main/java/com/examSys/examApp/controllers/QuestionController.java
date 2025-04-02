import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.examSys.examApp.repositories.*;
import com.examSys.examApp.dto.QuestionDTO;
import com.examSys.examApp.models.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    @PostMapping("/add")
    public Question addQuestion(@RequestBody QuestionDTO questionDTO) {
        Exam exam = examRepository.findById(questionDTO.getExamId())
                                  .orElseThrow(() -> new RuntimeException("Exam not found"));

        Question question = new Question();
        question.setExam(exam);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setOptions(questionDTO.getOptions()); // Store list of options
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());

        return questionRepository.save(question);
    }
}
