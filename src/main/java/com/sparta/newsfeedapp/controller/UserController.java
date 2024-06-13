package com.sparta.newsfeedapp.controller;

import com.sparta.newsfeedapp.dto.user.SignupRequestDto;
import com.sparta.newsfeedapp.dto.user.DeleteRequestDto;
import com.sparta.newsfeedapp.dto.user.UpdateRequestDto;
import com.sparta.newsfeedapp.dto.user.ProfileResponseDto;
import com.sparta.newsfeedapp.security.UserDetailsImpl;
import com.sparta.newsfeedapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto){
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.");
    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody DeleteRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.deleteUser(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
    }

    @GetMapping("/users/profile")
    public ProfileResponseDto getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getProfile(userDetails.getUser());
    }

    @PutMapping("/users/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.updateProfile(requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("수정이 완료되었습니다.");
    }

    // logout
    @PostMapping("/users/logout")
    public void logout(HttpServletRequest request) throws IOException {
        userService.logout(request);
    }
}