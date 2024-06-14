package com.sparta.newsfeedapp.controller;

import com.sparta.newsfeedapp.dto.comment.CommentResponseDto;
import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.dto.post.PostResponseDto;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.security.UserDetailsImpl;
import com.sparta.newsfeedapp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    public final PostService postService;

    // 뉴스피드 생성
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails.getUser());
    }

    // 뉴스피드 전체 조회
    @GetMapping("/posts")
    public List<PostResponseDto> getAllPost() {
        return postService.getAllPost();
    }

    // 뉴스피드 일부 조회
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 댓글 조회
    @GetMapping("/posts/{id}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long id) {
        return postService.getComments(id).stream().map(CommentResponseDto::new).toList();

    }

    // 뉴스피드 수정
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    // 뉴스피드 삭제
    @DeleteMapping("/posts/{id}")
    public Long deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails.getUser());
    }

    // 페이지네이션
    @GetMapping("/page")
    public Page<Post> getPosts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return postService.getPosts(pageable);
    }


}
