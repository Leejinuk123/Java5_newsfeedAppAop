package com.sparta.newsfeedapp.entity;

import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {
    @Test
    @DisplayName("Post Update Test")
    public void update(){
        //given
        PostRequestDto requestDto = new PostRequestDto("Test Contents");
        User testUser = new User();
        Post testPost = new Post(requestDto, testUser);

        PostRequestDto updateRequestDto = new PostRequestDto("Update Test Contents");

        //when
        testPost.update(updateRequestDto);

        //then
        assertEquals("Update Test Contents", testPost.getContent());
    }
}