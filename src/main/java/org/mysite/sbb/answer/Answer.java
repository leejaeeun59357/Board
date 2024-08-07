package org.mysite.sbb.answer;

import jakarta.persistence.*;
import lombok.*;
import org.mysite.sbb.question.Question;
import org.mysite.sbb.user.SiteUser;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    public void modify(String content, LocalDateTime modifyDate) {
        this.content = content;
        this.modifyDate = modifyDate;
    }
}
