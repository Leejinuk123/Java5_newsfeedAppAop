package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.user.DeleteRequestDto;
import com.sparta.newsfeedapp.dto.user.ProfileResponseDto;
import com.sparta.newsfeedapp.dto.user.SignupRequestDto;
import com.sparta.newsfeedapp.dto.user.UpdateRequestDto;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.exception.DeletedUserException;
import com.sparta.newsfeedapp.exception.PasswordMistmatchException;
import com.sparta.newsfeedapp.exception.UserNotFoundException;
import com.sparta.newsfeedapp.repository.UserRepository;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.RequiredTypes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    User testUser;

    @AfterAll
    public void deleteTestUser(){
        userRepository.delete(testUser);
    }

    @Test
    @DisplayName("회원가입 기능 테스트")
    @Order(1)
//    @Disabled
    void signup() {
        SignupRequestDto requestDto = new SignupRequestDto("tester12345", "tester12345!", "test@email.com", "테스터", "테스터입니다.");
        this.testUser = new User(
                requestDto.getUserId(),
                requestDto.getPassword(),
                requestDto.getEmail(),
                requestDto.getName(),
                requestDto.getBio(),
                UserStatusEnum.UNCHECKED);

        userService.signup(requestDto);

        User findUser = userRepository.findByEmail("test@email.com").orElseThrow(UserNotFoundException::new);

        assertEquals("테스터", findUser.getName());
        assertEquals("tester12345", findUser.getUserId());
        assertNotEquals("tester12345!", findUser.getPassword());
        assertEquals("테스터입니다.", findUser.getBio());
        assertEquals("test@email.com", findUser.getEmail());
    }

    @Test
    @DisplayName("프로필 조회하기 기능 성공 테스트")
    @Order(2)
    void getProfile() {
        this.testUser = userRepository.findByEmail("test@email.com").orElseThrow(UserNotFoundException::new);

        ProfileResponseDto responseDto = userService.getProfile(testUser);

        assertEquals("테스터", responseDto.getName());
        assertEquals("tester12345", responseDto.getUserId());
        assertEquals("test@email.com", responseDto.getEmail());
        assertEquals("테스터입니다.", responseDto.getBio());
        assertEquals(UserStatusEnum.UNCHECKED, responseDto.getUserStatus());
    }

    @Test
    @DisplayName("프로필 업데이트 기능 성공 테스트")
    @Order(3)
    void updateProfile() {
        UpdateRequestDto requestDto = new UpdateRequestDto("newname", "new@email.com", "newbio","tester12345!", "newpassword12345!");
        userService.updateProfile(requestDto, testUser);
        this.testUser = userRepository.findByEmail("new@email.com").orElseThrow(UserNotFoundException::new);

        assertEquals("newname", testUser.getName());
        assertEquals("tester12345", testUser.getUserId());
        assertEquals("new@email.com", testUser.getEmail());
        assertEquals("newbio", testUser.getBio());
        assertEquals(UserStatusEnum.UNCHECKED, testUser.getUserStatus());
    }

    @Test
    @DisplayName("유저 id로 DB에서 user 정보 가져오기 성공 테스트")
    @Order(4)
    void loadUserByUserId() {
        User getUser = userService.loadUserByUserId(testUser.getUserId());

        assertEquals(testUser.getUserId(), getUser.getUserId());
        assertEquals(testUser.getEmail(), getUser.getEmail());
        assertEquals(testUser.getBio(), getUser.getBio());
        assertEquals(testUser.getName(), getUser.getName());
        assertEquals(testUser.getUserStatus(), getUser.getUserStatus());
    }

    @Test
    @DisplayName("프로필 업데이트 기능 실패(비밀번호가 틀렸을 때) 테스트")
    @Order(5)
    void updateProfileFail1() {
        UpdateRequestDto requestDto = new UpdateRequestDto("newname", "new@email.com", "newbio","incorrectPassword123!", "tester12345!");

        PasswordMistmatchException exception = assertThrows(PasswordMistmatchException.class, () -> {
            userService.updateProfile(requestDto, testUser);
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("프로필 업데이트 기능 실패(기존의 비밀번호와 같은 비밀번호로 변경하려고 할 때) 테스트")
    @Order(6)
    void updateProfileFail2() {
        UpdateRequestDto requestDto = new UpdateRequestDto("newname", "new@email.com", "newbio","newpassword12345!", "newpassword12345!");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateProfile(requestDto, testUser);
        });

        assertEquals("현재 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유저 삭제 기능 실패(비밀번호가 틀렸을 때) 테스트")
    @Order(7)
    void deleteUser1() {
        DeleteRequestDto requestDto = new DeleteRequestDto("incorrectPassword123!");
        PasswordMistmatchException exception = assertThrows(PasswordMistmatchException.class, () -> {
            userService.deleteUser(requestDto, testUser);
        });
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유저 삭제 기능 성공 테스트")
    @Order(8)
    void deleteUser2() {
        DeleteRequestDto requestDto = new DeleteRequestDto("newpassword12345!");
        userService.deleteUser(requestDto, testUser);

        assertEquals(UserStatusEnum.DELETED, testUser.getUserStatus());
        assertNotEquals(UserStatusEnum.ACTIVE, testUser.getUserStatus());
        assertNotEquals(UserStatusEnum.UNCHECKED, testUser.getUserStatus());
    }


    @Test
    @DisplayName("프로필 업데이트 기능 실패(삭제된 user 의 프로필을 조회할 때) 테스트")
    @Order(9)
    void updateProfileFail3() {
        UpdateRequestDto requestDto = new UpdateRequestDto("newname", "new@email.com", "newbio","newpassword12345!", "tester12345!");

        DeletedUserException exception = assertThrows(DeletedUserException.class, () -> {
            userService.updateProfile(requestDto, testUser);
        });

        assertEquals("삭제된 유저입니다.", exception.getMessage());
    }

//    @Test
//    void logout() {
//    }
}