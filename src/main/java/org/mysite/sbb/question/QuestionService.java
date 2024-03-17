package org.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mysite.sbb.exception.DataNotFoundException;
import org.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    /**
     * 입력받은 값을 가진 question 저장
     *
     * @param subject 입력받은 subject
     * @param content 입력받은 content
     */
    public void create(String subject, String content, SiteUser author) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(author);
        questionRepository.save(question);
    }

    /**
     * 조회를 원하는 페이지에 보여줄 게시물의 갯수는 10개
     *
     * @param page 조회할 페이지의 번호
     * @return 해당 페이지의 Question
     */
    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    /**
     * Question 수정
     * @param question 수정될 question
     * @param subject 수정된 subject
     * @param content 수정된 content
     */
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        questionRepository.save(question);
    }

    /**
     * 질문 삭제
     * @param question 삭제할 질문
     */
    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    /**
     * 추천 기능
     * @param question 추천될 질문
     * @param siteUser 추천자
     */
    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        questionRepository.save(question);
    }
}