package com.sparta.newsfeedapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "대소문자 포함 영문 + 숫자만 허용됩니다.")
    @Size(min = 10, max = 20, message = "사용자 ID는 최소 10글자 이상, 최대 20글자 이하여야 합니다.")
    @NotBlank(message = "userId는 필수입니다.")
    private String userId;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-_=+\\\\|\\[{\\]};:'\",<.>/?]).{10,}$", message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.")
    @Size(min = 10, message = "비밀번호는 최소 10글자 이상이어야 합니다.")
    @NotBlank(message = "paaword는 필수입니다.")
    private String password;

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotBlank(message = "email은 필수입니다.")
    private String email;

    @Size(max = 8, message = "이름은 최대 8자입니다.")
    @NotBlank(message = "name은 필수입니다.")
    private String name;

    @Size(max = 20, message = "자기소개는 최대 20자입니다.")
    private String bio;
}
