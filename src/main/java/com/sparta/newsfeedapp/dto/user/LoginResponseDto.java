package com.sparta.newsfeedapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String refreshToken;

//    public LoginResponseDto(String token, String refreshToken) {
//        this.token = token;
//        this.refreshToken = refreshToken;
//    }
}
