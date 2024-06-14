package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.comment.CommentCreateRequestDto;
import com.sparta.newsfeedapp.dto.comment.CommentResponseDto;
import com.sparta.newsfeedapp.dto.comment.CommentUpdateRequestDto;
import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.entity.Comment;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.exception.PostIdNotFoundException;
import com.sparta.newsfeedapp.exception.UserNotFoundException;
import com.sparta.newsfeedapp.repository.CommentRepository;
import com.sparta.newsfeedapp.repository.PostRepository;
import com.sparta.newsfeedapp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentServiceTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRepository userRepository;
    User user;
    Post post;

    @BeforeAll
    public void beforeSetup() {
        User dummyUser = new User("dummyUser", "dummyPassword", "user@email.com", "dummy", "dummy bio", UserStatusEnum.UNCHECKED);
        userRepository.save(dummyUser);

        PostRequestDto postRequestDto = new PostRequestDto("dummy Contents");
        Post dummyPost = new Post(postRequestDto, dummyUser);
        postRepository.save(dummyPost);

        this.user = userRepository.findByUserId("dummyUser").orElseThrow(UserNotFoundException::new);
        this.post = postRepository.findById(dummyPost.getId()).orElseThrow(PostIdNotFoundException::new);
    }

    @AfterAll
    public void AfterSetup() {
        this.post = postRepository.findById(post.getId()).orElseThrow(PostIdNotFoundException::new);
        postRepository.delete(post);
        User dummyUser = userRepository.findByUserId("dummyUser").orElseThrow(UserNotFoundException::new);
        userRepository.delete(dummyUser);
    }

    @Test
    @Order(1)
    @DisplayName("댓글 작성 기능 성공 테스트")
    void createComment() {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto("댓글 작성 테스트입니다.");
        CommentResponseDto responseDto = commentService.createComment(requestDto, post.getId(), user);

        assertEquals(requestDto.getContent(),responseDto.getContent());
    }

    @Test
    @Order(2)
    @DisplayName("댓글 수정 기능 성공 테스트")
    void updateComment() {
        this.post = postRepository.findById(post.getId()).orElseThrow(PostIdNotFoundException::new);
        List<Comment> commentList = commentRepository.findByPostId(post.getId());
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("댓글 수정 테스트입니다.");
        ResponseEntity<String> response = commentService.updateComment(requestDto, commentList.get(0).getId(), user);

        // 반환된 ResponseEntity 에서 수정된 댓글의 내용을 추출
        String expectedResponse = "성공적으로 수정했습니다. (" + requestDto.getContent() + ")";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.OK, response.getStatusCode()); // HTTP 상태코드가 200 OK 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
    }

    @Test
    @Order(3)
    @DisplayName("댓글 수정 기능 실패 테스트 - 존재하지 않는 댓글 ID")
    void updateCommentNotFound() {
        // 존재하지 않는 댓글 ID를 사용하여 수정 요청
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("댓글 수정 테스트입니다.");

        // 존재하지 않는 댓글 ID를 사용하여 수정 요청
        Long nonExistingCommentId = 999L; // 존재하지 않는 댓글 ID (임의로 설정)
        ResponseEntity<String> response = commentService.updateComment(requestDto, nonExistingCommentId, user);

        // 반환된 ResponseEntity에서 메시지를 추출
        String expectedResponse = "Comment를 찾지 못해 수정하지 못했습니다.";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // HTTP 상태코드가 404 NOT FOUND 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
    }

    @Test
    @Order(4)
    @DisplayName("댓글 수정 기능 실패 테스트 - 댓글 작성자가 아닌 사용자")
    void updateCommentUserMismatch() {
        // 다른 사용자의 댓글 ID와 요청 내용으로 수정 요청
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto("댓글 수정 테스트입니다.");

        // 다른 사용자의 댓글 ID를 가져옴 (현재 사용자와 다른 사용자의 댓글)
        User anotherUser = userRepository.findByUserId("anotherUser").orElseThrow(UserNotFoundException::new);
        List<Comment> commentList = commentRepository.findByUserId(anotherUser.getId()); // 다른 사용자의 댓글 리스트 가져오기

        if (commentList.isEmpty()) {
            fail("다른 사용자의 댓글이 존재하지 않습니다. 먼저 테스트 데이터를 준비해주세요.");
        }

        Long anotherUserCommentId = commentList.get(0).getId(); // 다른 사용자의 첫 번째 댓글 ID 사용

        // 다른 사용자의 댓글 ID와 요청 내용으로 수정 요청
        ResponseEntity<String> response = commentService.updateComment(requestDto, anotherUserCommentId, user);

        // 반환된 ResponseEntity에서 메시지를 추출
        String expectedResponse = "사용자 정보가 일치하지 않습니다.";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // HTTP 상태코드가 403 FORBIDDEN 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
    }

    @Test
    @Order(5)
    @DisplayName("댓글 삭제 기능 실패 테스트 - 존재하지 않는 댓글 ID")
    void deleteCommentNotFound() {
        // 존재하지 않는 댓글 ID를 사용하여 삭제 요청
        Long nonExistingCommentId = 999L; // 존재하지 않는 댓글 ID (임의로 설정)
        ResponseEntity<String> response = commentService.deleteComment(nonExistingCommentId, user);

        // 반환된 ResponseEntity에서 메시지를 추출
        String expectedResponse = "Comment를 찾지 못해 삭제하지 못했습니다.";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // HTTP 상태코드가 404 NOT FOUND 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
    }

    @Test
    @Order(6)
    @DisplayName("댓글 삭제 기능 실패 테스트 - 댓글 작성자가 아닌 사용자")
    void deleteCommentUserMismatch() {
        // 다른 사용자의 댓글 생성
        User anotherUser = new User("dummyUser", "dummyPassword", "user@email.com", "dummy", "dummy bio", UserStatusEnum.UNCHECKED);
        List<Comment> commentList = commentRepository.findByPostId(post.getId());
        // 댓글 삭제 요청 (현재 사용자로 인증)
        ResponseEntity<String> response = commentService.deleteComment(commentList.get(0).getId(), anotherUser);

        // 반환된 ResponseEntity에서 메시지를 추출
        String expectedResponse = "사용자 정보가 일치하지 않습니다.";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // HTTP 상태코드가 403 FORBIDDEN 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
    }

    @Test
    @Order(7)
    @DisplayName("댓글 삭제 기능 성공 테스트")
    void deleteCommentSuccess() {
        // 댓글 생성
        List<Comment> commentList = commentRepository.findByPostId(post.getId());

        // 댓글 삭제 요청
        ResponseEntity<String> response = commentService.deleteComment(commentList.get(0).getId(), user);

        // 반환된 ResponseEntity에서 메시지를 추출
        String expectedResponse = "성공적으로 삭제했습니다.";
        String actualResponse = response.getBody();

        // 결과 검증
        assertEquals(HttpStatus.OK, response.getStatusCode()); // HTTP 상태코드가 200 OK 여부 확인
        assertEquals(expectedResponse, actualResponse); // 반환된 메시지가 예상한 내용과 일치하는지 확인
        assertFalse(commentRepository.existsById(commentList.get(0).getId())); // 삭제된 댓글이 더 이상 존재하지 않는지 확인
    }
}