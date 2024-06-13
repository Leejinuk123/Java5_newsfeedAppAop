package com.sparta.newsfeedapp.entity;

import com.sparta.newsfeedapp.dto.user.UpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    private final User testUser = new User("1", "password", "test@email.com", "tester", "tester's bio", UserStatusEnum.ACTIVE);

    @Test
    @DisplayName("유저 프로필 수정(update) 기능 정상 성공 테스트(모든 값이 들어있을 때)")
    public void test1(){
        //given
        UpdateRequestDto requestDto = new UpdateRequestDto("newTester", "newTest@email.com", "tester's new bio", "password", "newPassword");

        String newPassword = requestDto.getNewPassword();
        String newName = requestDto.getName();
        String newEmail = requestDto.getEmail();
        String newBio = requestDto.getBio();

        //when
        testUser.update(newName, newEmail, newPassword, newBio);

        //then
        assertEquals("newTester", testUser.getName());
        assertEquals("newTest@email.com", testUser.getEmail());
        assertEquals("tester's new bio", testUser.getBio());
        assertEquals("newPassword", testUser.getPassword());
    }

    @Test
    @DisplayName("유저 프로필 수정(update) 기능 정상 성공 테스트(일부 값이 null 일 때)")
    public void test2(){
        //given
        UpdateRequestDto requestDto = new UpdateRequestDto("newTester", null, "tester's new bio", "password", null);

        String newPassword = requestDto.getNewPassword();
        String newName = requestDto.getName();
        String newEmail = requestDto.getEmail();
        String newBio = requestDto.getBio();

        //when
        testUser.update(newName, newEmail, newPassword, newBio);

        //then
        assertEquals("newTester", testUser.getName());
        assertEquals("test@email.com", testUser.getEmail());
        assertEquals("tester's new bio", testUser.getBio());
        assertEquals("password", testUser.getPassword());
    }

    @Test
    @DisplayName("유저 삭제하기 테스트")
    public void test3(){
        //given
        //when
        testUser.setStatusToDeleted();
        //then
        assertEquals(UserStatusEnum.DELETED, testUser.getUserStatus());
    }

    @Test
    @DisplayName("유저 이메일 인증 완료 상태로 만들기 테스트")
    public void test4(){
        User unCheckedUser = new User("2", "password", "test@email.com", "tester", "tester's bio", UserStatusEnum.UNCHECKED);
        //given
        //when
        unCheckedUser.setStatusToChecked();
        //then
        assertEquals(UserStatusEnum.ACTIVE, unCheckedUser.getUserStatus());
    }
}