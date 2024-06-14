package com.sparta.newsfeedapp.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeedapp.config.WebSecurityConfig;
import com.sparta.newsfeedapp.controller.CommentController;
import com.sparta.newsfeedapp.dto.comment.CommentCreateRequestDto;
import com.sparta.newsfeedapp.dto.comment.CommentResponseDto;
import com.sparta.newsfeedapp.dto.comment.CommentUpdateRequestDto;
import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.dto.post.PostResponseDto;
import com.sparta.newsfeedapp.entity.Comment;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.mvc.MockSpringSecurityFilter;
import com.sparta.newsfeedapp.security.UserDetailsImpl;
import com.sparta.newsfeedapp.service.CommentService;
import com.sparta.newsfeedapp.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class CommentControllerTest {

    private User testUser;
    private Post testPost;

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
        //원래 시큐리티 필터는 이미 위에서 제거했고, 해당 코드는 직접 만든 가짜 필터를 중간에 끼워넣는 코드다.
    }

    private void mockUserSetup() {
        // Mock 테스트 유져 생성
        String userId = "tester12345";
        String password = "tester12345!";
        String email = "test@email.com";
        String name = "tester";
        String bio = "tester's bio";
        UserStatusEnum status = UserStatusEnum.ACTIVE;
        User user = new User(userId, password, email, name, bio, status);

        this.testUser = user;
        PostRequestDto postRequestDto = new PostRequestDto("테스트 내용입니다");
        this.testPost = new Post(postRequestDto, testUser);

        UserDetailsImpl testUserDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("댓글 등록 성공 테스트")
    void createComment() throws Exception {
        this.mockUserSetup();
        CommentCreateRequestDto commentRequestDto = new CommentCreateRequestDto("댓글 내용입니다.");
        Comment comment = new Comment(commentRequestDto, testUser, testPost);
        CommentResponseDto responseDto = new CommentResponseDto(comment);

        given(commentService.createComment(any(CommentCreateRequestDto.class),any(Long.class),any(User.class))).willReturn(responseDto);

        String postInfo = objectMapper.writeValueAsString(commentRequestDto);

        // when - then
        mvc.perform(post("/api/comments/1")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("댓글 내용입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateCommentSuccess() throws Exception {
        this.mockUserSetup();
        CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto("댓글 내용입니다.");
        Comment comment = new Comment(commentCreateRequestDto, testUser, testPost);

        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("댓글 수정 내용입니다.");
        comment.update(commentUpdateRequestDto);

        ResponseEntity<String> response = new ResponseEntity<>("성공적으로 수정했습니다. (" + comment.getContent() + ")", HttpStatus.OK);

        given(commentService.updateComment(any(CommentUpdateRequestDto.class),any(Long.class),any(User.class))).willReturn(response);

        String postInfo = objectMapper.writeValueAsString(commentUpdateRequestDto);

        // when - then
        mvc.perform(put("/api/comments/1")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트")
    void updateCommentFail() throws Exception {
        this.mockUserSetup();
        CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto("댓글 내용입니다.");
        Comment comment = new Comment(commentCreateRequestDto, testUser, testPost);

        CommentUpdateRequestDto commentUpdateRequestDto = new CommentUpdateRequestDto("댓글 수정 내용입니다.");
        comment.update(commentUpdateRequestDto);

        ResponseEntity<String> response = new ResponseEntity<>("Comment를 찾지 못해 수정하지 못했습니다.", HttpStatus.NOT_FOUND);

        given(commentService.updateComment(any(CommentUpdateRequestDto.class),any(Long.class),any(User.class))).willReturn(response);

        String postInfo = objectMapper.writeValueAsString(commentUpdateRequestDto);

        // when - then
        mvc.perform(put("/api/comments/1")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteCommentSuccess() throws Exception  {
        this.mockUserSetup();
        ResponseEntity<String> response = new ResponseEntity<>("성공적으로 삭제했습니다.", HttpStatus.OK);
        given(commentService.deleteComment(any(Long.class),any(User.class))).willReturn(response);
        // when - then
        mvc.perform(delete("/api/comments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트")
    void deleteCommentFail() throws Exception  {
        this.mockUserSetup();
        ResponseEntity<String> response = new ResponseEntity<>("Comment를 찾지 못해 삭제하지 못했습니다.", HttpStatus.NOT_FOUND);
        given(commentService.deleteComment(any(Long.class),any(User.class))).willReturn(response);
        // when - then
        mvc.perform(delete("/api/comments/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}