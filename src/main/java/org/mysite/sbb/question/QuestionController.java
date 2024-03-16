package org.mysite.sbb.question;

import jakarta.validation.Valid;
import org.mysite.sbb.answer.AnswerForm;
import org.mysite.sbb.user.SiteUser;
import org.mysite.sbb.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

//RestController 는 Json 형식으로 데이터 response 하기때문에 html 파일 못 읽음
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    /**
     *
     * @param model html 파일에 전달할 모델
     * @param page 게시물을 보기 원하는 해당 페이지
     * @return 해당 페이지에 있는 게시물 10개
     */
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging);

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
     *
     * @return question_form
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(
            QuestionForm questionForm
    ) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        // 제목이나 내용이 비어 있을 때
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        SiteUser siteUser = userService.getUser(principal.getName());

        // 질문 저장
        questionService.create(questionForm.getSubject(), questionForm.getContent(),siteUser);

        // 질문 저장 후 질문 목록으로 다시 이동
        return "redirect:/question/list";
    }
}