package org.mysite.sbb.answer;

import lombok.RequiredArgsConstructor;
import org.mysite.sbb.exception.DataNotFoundException;
import org.mysite.sbb.question.Question;
import org.mysite.sbb.user.SiteUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = Answer.builder()
                .content(content)
                .createDate(LocalDateTime.now())
                .question(question)
                .author(author)
                .build();
        return answerRepository.save(answer);
    }

    public Answer getAnswer(Integer id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
        return answer;
    }

    public Answer modify(Integer id,String loginUserId, String content) {
        Answer answer = this.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(loginUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        answer.modify(content, LocalDateTime.now());
        return answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        answerRepository.save(answer);
    }
}