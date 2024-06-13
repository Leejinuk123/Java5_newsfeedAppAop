package com.sparta.newsfeedapp.entity;

import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post(PostRequestDto requestDto, User user) {
        this.content = requestDto.getContent();
        this.user = user;
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
