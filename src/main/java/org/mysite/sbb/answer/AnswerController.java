package org.mysite.sbb.answer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mysite.sbb.question.Question;
import org.mysite.sbb.question.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid AnswerForm answerForm,
                               BindingResult bindingResult
    ) {
        Question question = questionService.getQuestion(id);
        if(bindingResult.hasErrors()) {
            model.addAttribute("question",question);
            return "question_detail";
        }
        answerService.create(question, answerForm.getContent());

        return String.format("redirect:/question/detail/%s",id);
    }
}