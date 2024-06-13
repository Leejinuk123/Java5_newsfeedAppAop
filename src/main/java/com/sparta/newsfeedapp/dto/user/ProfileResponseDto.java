package com.sparta.newsfeedapp.dto.user;

import com.sparta.newsfeedapp.entity.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private String userId;
    private String name;
    private String bio;
    private String email;
    private UserStatusEnum userStatus;

//    public ProfileResponseDto(String userId, String name, String bio, String email, UserStatusEnum userStatus) {
//        this.userId = userId;
//        this.name = name;
//        this.bio = bio;
//        this.email = email;
//        this.userStatus = userStatus;
//    }
}
