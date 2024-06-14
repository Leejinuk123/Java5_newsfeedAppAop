package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.exception.UserNotFoundException;
import com.sparta.newsfeedapp.repository.UserRepository;
import com.sparta.newsfeedapp.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDetailsServiceImplTest {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    public void beforeSetup(){
        User dummyUser = new User("dummyUser", "dummyPassword", "user@email.com", "dummy", "dummy bio", UserStatusEnum.UNCHECKED);
        userRepository.save(dummyUser);
    }

    @AfterAll
    public void AfterSetup(){
        User dummyUser = userRepository.findByUserId("dummyUser").orElseThrow(UserNotFoundException::new);
        userRepository.delete(dummyUser);
    }

    @Test
    @DisplayName("유저 이름으로 user db 에서 가져오기 기능 성공 테스트")
    void loadUserByUsernameSuccess() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("dummyUser");
        assertEquals("dummyUser",userDetails.getUsername());
    }

    @Test
    @DisplayName("유저 이름으로 user db 에서 가져오기 기능 실패(찾으려는 유저가 없을 때) 테스트")
    void loadUserByUsernameFail() {
        String userId = "Not Found notExistDummyUser";
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(userId);
        });
        assertEquals("Not Found " + userId, exception.getMessage());
    }
}