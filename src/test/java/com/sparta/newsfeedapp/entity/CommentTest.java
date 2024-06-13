package com.sparta.newsfeedapp.entity;

import com.sparta.newsfeedapp.dto.comment.CommentCreateRequestDto;
import com.sparta.newsfeedapp.dto.comment.CommentUpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("댓글 수정(Update) 기능 테스트")
    public void update(){
        //given
        CommentCreateRequestDto createRequestDto = new CommentCreateRequestDto("test contents");
        User testUser = new User();
        Post testPost = new Post();
        Comment testComment = new Comment(createRequestDto, testUser, testPost);
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("Update test contents");
        //when
        testComment.update(requestDto);
        //then
        assertEquals("Update test contents", testComment.getContent());
    }
}