package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.dto.post.PostResponseDto;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.exception.PasswordMistmatchException;
import com.sparta.newsfeedapp.exception.PostIdNotFoundException;
import com.sparta.newsfeedapp.exception.UserMismatchException;
import com.sparta.newsfeedapp.exception.UserNotFoundException;
import com.sparta.newsfeedapp.repository.CommentRepository;
import com.sparta.newsfeedapp.repository.PostRepository;
import com.sparta.newsfeedapp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostServiceTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;

    User user;
    Post post;

    @BeforeAll
    public void beforeSetup() {
        User dummyUser = new User("dummyUser", "dummyPassword", "user@email.com", "dummy", "dummy bio", UserStatusEnum.UNCHECKED);
        userRepository.save(dummyUser);
        this.user = userRepository.findByUserId("dummyUser").orElseThrow(UserNotFoundException::new);
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
    @DisplayName("게시글 등록 성공 테스트")
    void createPost() {
        PostRequestDto requestDto = new PostRequestDto("테스트 게시글입니다.");
        PostResponseDto responseDto = postService.createPost(requestDto, user);
        this.post = postRepository.findById(responseDto.getId()).orElseThrow(PostIdNotFoundException::new);

        assertEquals(post.getId(), responseDto.getId());
        assertEquals(post.getContent(), responseDto.getContent());
    }

    @Test
    @Order(2)
    @DisplayName("전체 게시글 조회 기능 성공 테스트")
    void getAllPost() {
        List<PostResponseDto> responseDtoList = postService.getAllPost();

        assertEquals(post.getId(), responseDtoList.get(0).getId());
        assertEquals(post.getContent(), responseDtoList.get(0).getContent());
    }

    @Test
    @Order(3)
    @DisplayName("단일 게시글 조회 기능 실패(postId를 찾을 수 없을 때) 테스트")
    public void test(){
        PostIdNotFoundException exception = assertThrows(PostIdNotFoundException.class, () -> {
            postService.getPost(999999L);
        });
        assertEquals("PostId를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("단일 게시글 조회 기능 성공 테스트")
    void getPosts() {
        PostResponseDto responseDto = postService.getPost(post.getId());
        assertEquals(post.getId(), responseDto.getId());
        assertEquals(post.getContent(), responseDto.getContent());
    }

    @Test
    @Order(5)
    @DisplayName("게시글 수정 기능 실패(다른 작성자의 게시글을 수정하려고 할 때) 테스트")
    void updatePostFail() {
        User dummyUser = new User("dummyUser2", "dummyPassword2", "user2@email.com", "dummy2", "dummy bio2", UserStatusEnum.UNCHECKED);
        PostRequestDto requestDto = new PostRequestDto("게시글 수정 테스트입니다.");
        UserMismatchException exception = assertThrows(UserMismatchException.class, () -> {
            postService.updatePost(post.getId(), requestDto, dummyUser);
        });
        assertEquals("본인이 작성한 글만 수정가능합니다.", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("게시글 수정 기능 성공 테스트")
    void updatePostSuccess() {
        PostRequestDto requestDto = new PostRequestDto("게시글 수정 테스트입니다.");
        postService.updatePost(post.getId(), requestDto, user);
        this.post = postRepository.findById(post.getId()).orElseThrow(PostIdNotFoundException::new);
        assertEquals(requestDto.getContent(), post.getContent());
    }

    @Test
    @Order(7)
    @DisplayName("게시글 삭제 기능 실패(다른 작성자의 게시글을 수정하려고 할 때) 테스트")
    void deletePostFail() {
        User dummyUser = new User("dummyUser2", "dummyPassword2", "user2@email.com", "dummy2", "dummy bio2", UserStatusEnum.UNCHECKED);
        UserMismatchException exception = assertThrows(UserMismatchException.class, () -> {
            postService.deletePost(post.getId(), dummyUser);
        });
        assertEquals("본인이 작성한 글만 수정가능합니다.", exception.getMessage());
    }

//    @Test
//    @Order(8)
//    @DisplayName("게시글 삭제 기능 성공 테스트")
//    void deletePostSuccess() {
//        this.post = postRepository.findById(post.getId()).orElseThrow(PostIdNotFoundException::new);
//        Long id = postService.deletePost(post.getId(), user);
//        assertEquals(post.getId(), id);
//    }
}