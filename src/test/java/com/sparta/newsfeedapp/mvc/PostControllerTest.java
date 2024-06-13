package com.sparta.newsfeedapp.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeedapp.config.WebSecurityConfig;
import com.sparta.newsfeedapp.controller.PostController;
import com.sparta.newsfeedapp.dto.post.PostRequestDto;
import com.sparta.newsfeedapp.dto.post.PostResponseDto;
import com.sparta.newsfeedapp.entity.Comment;
import com.sparta.newsfeedapp.entity.Post;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.repository.CommentRepository;
import com.sparta.newsfeedapp.repository.PostRepository;
import com.sparta.newsfeedapp.security.UserDetailsImpl;
import com.sparta.newsfeedapp.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PostController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class PostControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PostService postService;

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
        User testUser = new User(userId, password, email, name, bio, status);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("Post 등록")
    void test1() throws Exception {
        this.mockUserSetup();
        //Jackson 라이브러리에서 requestDto 를 역직렬화 하기 위해서는 requestDto 부분에 꼭 기본 생성자가 있어야한다.
        PostRequestDto requestDto = new PostRequestDto("테스트 내용입니다");

        Post post = new Post(requestDto, new User());

        PostResponseDto responseDto = new PostResponseDto(post);

        given(postService.createPost(any(PostRequestDto.class),any(User.class))).willReturn(responseDto);

        String postInfo = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/posts")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 전체 조회")
    public void test() throws Exception {
        //given
        PostResponseDto responseDto1 = new PostResponseDto(1L, 1L, "첫 번째 게시글", null, null);
        PostResponseDto responseDto2 = new PostResponseDto(2L, 1L, "두 번째 게시글", null, null);
        List<PostResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(responseDto1);
        responseDtoList.add(responseDto2);

        given(postService.getAllPost()).willReturn(responseDtoList);

        //when - then
        mvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("첫 번째 게시글"))
                .andExpect(jsonPath("$[1].content").value("두 번째 게시글"))
                .andDo(print());
    }
}