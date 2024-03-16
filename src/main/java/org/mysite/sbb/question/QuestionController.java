package org.mysite.sbb.question;

import jakarta.validation.Valid;
import org.mysite.sbb.answer.AnswerForm;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//RestController 는 Json 형식으로 데이터 response 하기때문에 html 파일 못 읽음
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 목록을 보여줌
     * @param model html 파일에 전달할 객체
     * @return question_list html 파일
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id
    , AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    /**
     * 질문 생성을 위한 form 리턴
     * @return question_form
     */
    @GetMapping("/create")
    public String questionCreate(
            QuestionForm questionForm
    ) {
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult
    ) {
        // 제목이나 내용이 비어 있을 때
        if(bindingResult.hasErrors()) {
            return "question_form";
        }

        // 질문 저장
        questionService.create(questionForm.getSubject(),questionForm.getContent());

        // 질문 저장 후 질문 목록으로 다시 이동
        return "redirect:/question/list";
    }
}