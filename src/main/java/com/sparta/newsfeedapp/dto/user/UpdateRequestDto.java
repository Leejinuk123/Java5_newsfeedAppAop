package com.sparta.newsfeedapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {

    @Size(max = 8, message = "이름은 최대 8자입니다.")
    private String name;

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private String email;

    @Size(max = 20, message = "자기소개는 최대 20자입니다.")
    private String bio;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-_=+\\\\|\\[{\\]};:'\",<.>/?]).{10,}$", message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.")
    @Size(min = 10, message = "비밀번호는 최소 10글자 이상이어야 합니다.")
    @NotBlank(message = "paaword는 필수입니다.")
    private String password;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-_=+\\\\|\\[{\\]};:'\",<.>/?]).{10,}$", message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.")
    @Size(min = 10, message = "비밀번호는 최소 10글자 이상이어야 합니다.")
    private String newPassword;
}