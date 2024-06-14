package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.dto.post.PostResponseDto;
import com.sparta.newsfeedapp.entity.Comment;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.exception.PostIdNotFoundException;
import com.sparta.newsfeedapp.exception.UserMismatchException;
import com.sparta.newsfeedapp.repository.CommentRepository;
import com.sparta.newsfeedapp.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    public final PostRepository postRepository;
    public final CommentRepository commentRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        // 인가 기능 추가하기
        Post post = new Post(requestDto, user);

        postRepository.save(post);

        return new PostResponseDto(post);
    }

    public List<PostResponseDto> getAllPost() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public PostResponseDto getPost(Long id) {
        return new PostResponseDto(postRepository.findById(id).orElseThrow(
                PostIdNotFoundException::new
        ));
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {
        Post post = findPostById(id);
        if (!post.getUser().getId().equals(user.getId())){

            throw new UserMismatchException();
        }
        post.update(requestDto);
        return new PostResponseDto(post);
    }

    public Long deletePost(Long id, User user) {
        Post post = findPostById(id);
        if (!post.getUser().getId().equals(user.getId())){
            throw new UserMismatchException();
        }
        postRepository.delete(post);
        return id;
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new PostIdNotFoundException()
        );
    }
}