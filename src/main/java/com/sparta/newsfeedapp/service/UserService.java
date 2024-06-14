package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.user.ProfileResponseDto;
import com.sparta.newsfeedapp.dto.user.SignupRequestDto;
import com.sparta.newsfeedapp.dto.user.DeleteRequestDto;
import com.sparta.newsfeedapp.dto.user.UpdateRequestDto;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.exception.DeletedUserException;
import com.sparta.newsfeedapp.exception.PasswordMistmatchException;
import com.sparta.newsfeedapp.exception.TokenNotFoundException;
import com.sparta.newsfeedapp.exception.UserNotFoundException;
import com.sparta.newsfeedapp.repository.UserRepository;
import com.sparta.newsfeedapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.sparta.newsfeedapp.entity.UserStatusEnum.DELETED;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UserService")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final com.sparta.newsfeedapp.service.JwtBlacklistService jwtBlacklistService;

    public void signup(SignupRequestDto requestDto) {
        String userId = requestDto.getUserId();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        String name = requestDto.getName();
        String bio = requestDto.getBio();

        // 사용자 등록
        User user = new User(userId, password, email, name, bio, UserStatusEnum.UNCHECKED);
        userRepository.save(user);
        log.info("회원가입 완료");
    }

    @Transactional
    public void deleteUser(DeleteRequestDto requestDto, User user) {
        String userPassword = requestDto.getPassword();
        if (!passwordEncoder.matches(userPassword, user.getPassword())){
            throw new PasswordMistmatchException();
        }
        user.setStatusToDeleted();
        userRepository.save(user);
    }

    public ProfileResponseDto getProfile(User user) {
        String userId = user.getUserId();
        String name = user.getName();
        String email = user.getEmail();
        String bio = user.getBio();
        UserStatusEnum userStatus = user.getUserStatus();
        if (user.getBio() == null) bio = "자기소개가 없습니다.";

        return new ProfileResponseDto(userId,name,bio,email,userStatus);
    }

    @Transactional
    public void updateProfile(UpdateRequestDto requestDto, User user) {
        if(user.getUserStatus() == DELETED){
            log.info("삭제된 사용자입니다");
            throw new DeletedUserException();
        }
        User checkUser = loadUserByUserId(user.getUserId());
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new PasswordMistmatchException();
        }

        String newPassword = null;
        String newName = null;
        String newEmail = null;
        String newBio = null;

        if (requestDto.getNewPassword() != null){ //비밀번호가 널이 아니면 아래를 실행 ( 비밀번호가 있으면 아래를 실행 )
            if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())){
                throw new IllegalArgumentException("현재 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
            }
            newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        }
        newName = requestDto.getName();
        newEmail = requestDto.getEmail();
        newBio = requestDto.getBio();
      
        checkUser.update(newName, newEmail, newPassword, newBio);
    }

    public User loadUserByUserId(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
    }

    // logout
    public void logout(HttpServletRequest request) throws TokenNotFoundException {
        String accessToken = request.getHeader("Authorization").substring(7);
        String refreshToken = request.getHeader("RefreshToken").substring(7);

        User user = loadUserByUserId(jwtService.extractUserId(accessToken));
        user.setRefreshToken("logged out");


        LocalDateTime accessExpiration = jwtService.extractExpiration(accessToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        jwtBlacklistService.blacklistToken(accessToken, accessExpiration);
        LocalDateTime refreshExpiration = jwtService.extractExpiration(refreshToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        jwtBlacklistService.blacklistToken(refreshToken, refreshExpiration);

        SecurityContextHolder.clearContext();
        log.info("logout success");
    }
}
