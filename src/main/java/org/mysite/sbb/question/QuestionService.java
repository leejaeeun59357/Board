package org.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.*;
import org.mysite.sbb.answer.Answer;
import org.mysite.sbb.exception.DataNotFoundException;
import org.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

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
        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .createDate(LocalDateTime.now())
                .author(author)
                .build();
        questionRepository.save(question);
    }

    /**
     * 조회를 원하는 페이지에 보여줄 게시물의 갯수는 10개
     *
     * @param page 조회할 페이지의 번호
     * @return 해당 페이지의 Question
     */
    public Page<Question> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(keyword);
        return questionRepository.findAll(spec,pageable);
    }

    public void modify(Integer id, QuestionForm questionForm, String loginUserId) {
        Question question = this.getQuestion(id);

        if(!question.getAuthor().getUsername().equals(loginUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }

        question.modify(questionForm.getSubject(), questionForm.getContent(), LocalDateTime.now());

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


    /**
     * 키워드가 들어있는 모든 항목 반환
     * @param kw 검색할 키워드
     * @return 여러 조건 중 하나라도 만족하는 경우 해당 항목 반환
     */
    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // q : Root 자료형. 즉, 기준이 되는 Question 엔티티의 객체 의미, 질문 내용과 내용 검색을 위해 필요
                // u1 : Question 엔티티와 SiteUser 엔티티를 아우터 조인 하여 만든 SiteUser 엔티티 객체, 두 엔티티는 author 속성으로 연결되어 있음
                // a : Question 엔티티와 Answer 엔티티를 아우터 조인하여 만든 Answer 엔티티 객체, 두 엔티티는 answerList 속성으로 연결되어 있음, 답변 내용 검색을 위해 필요
                // u2 : a 객체와 SiteUser 엔티티를 다시 한번 아우터 조인, 답변 작성자 검색을 위해 필요

                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
}