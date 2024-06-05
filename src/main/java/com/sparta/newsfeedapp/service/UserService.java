package com.sparta.newsfeedapp.service;

import com.sparta.newsfeedapp.dto.userRequestDto.SignupRequestDto;
import com.sparta.newsfeedapp.dto.userRequestDto.deleteRequestDto;
import com.sparta.newsfeedapp.entity.User;
import com.sparta.newsfeedapp.entity.UserStatusEnum;
import com.sparta.newsfeedapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UserService")
@Component
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequestDto requestDto) {
        String userId = requestDto.getUserId();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        String name = requestDto.getName();

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUserId(userId);
        if (checkUsername.isPresent()) {
            User existUser = checkUsername.get();
            if (existUser.getUserStatus().equals(UserStatusEnum.DELETED)) {
                throw new IllegalArgumentException("탈퇴한 UserId입니다: " + userId);
            } else {
                throw new IllegalArgumentException("중복된 UserId가 존재합니다: " + userId);
            }
        }

        // email 중복확인
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
      
        // 사용자 등록
        User user = new User(userId, password, email, name, UserStatusEnum.ACTIVE);
        userRepository.save(user);
        log.info("회원가입 완료");
    }

    @Transactional
    public void deleteUser(deleteRequestDto requestDto) {
        String userId =  requestDto.getUserId();
        String userPassword = requestDto.getPassword();

        User existUser = loadUserByUserId(userId);
        if (!passwordEncoder.matches(userPassword, existUser.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        existUser.deactivateUser();

    }

    public User loadUserByUserId(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + userId));
    }
}