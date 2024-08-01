package org.mysite.sbb.question;

import jakarta.persistence.*;
import lombok.*;
import org.mysite.sbb.answer.Answer;
import org.mysite.sbb.user.SiteUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    public void modify(String subject, String content, LocalDateTime modifyDate) {
        this.subject = subject;
        this.content = content;
        this.modifyDate = modifyDate;
    }
}