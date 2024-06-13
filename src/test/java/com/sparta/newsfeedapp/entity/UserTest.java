package com.sparta.newsfeedapp.entity;

import com.sparta.newsfeedapp.dto.user.UpdateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    @Test
    @DisplayName("유저 프로필 수정(update) 기능 정상 성공 테스트(모든 값이 들어있을 때)")
    public void test1(){
        //given
        UpdateRequestDto requestDto = new UpdateRequestDto("newTester", "newTest@email.com", "tester's new bio", "password", "newPassword");
        User testUser = new User("1", "password", "test@email.com", "tester", "tester's bio", UserStatusEnum.ACTIVE);

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
        User testUser = new User("1", "password", "test@email.com", "tester", "tester's bio", UserStatusEnum.ACTIVE);

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

//    @Test
//    @DisplayName("유저 프로필 수정(update) 기능 삭제된 사용자 예외처리 테스트")
//    public void test(){
//        //given
//        UpdateRequestDto requestDto = new UpdateRequestDto("newTester", "newTest@email.com", "tester's new bio", "password", "newPassword");
//        User testUser = new User("1", "password", "test@email.com", "tester", "tester's bio", UserStatusEnum.DELETED);
//
//        String newPassword = requestDto.getNewPassword();
//        String newName = requestDto.getName();
//        String newEmail = requestDto.getEmail();
//        String newBio = requestDto.getBio();
//        //when
//        Exception exception = assertThrows(DeletedUserException.class, () -> {
//                    testUser.update(newName, newEmail, newPassword, newBio);
//                });
//        //then
//        assertEquals(
//                "삭제된 유저입니다.",
//                exception.getMessage()
//        );
//    }

}