package com.sparta.newsfeedapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 기본 오류 발생시
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(ex +"An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // black listed token 입력시 발생
    @ExceptionHandler(BlackListedTokenException.class)
    public ResponseEntity<String> handleCustomException(BlackListedTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);  //403
    }

    // Token 미입력시 발생
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handleCustomException(TokenNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);  //401
    }

    // 본인이 작성하지 않은 글 수정시 발생
    @ExceptionHandler(UserMismatchException.class)
    public ResponseEntity<String> handleCustomException(UserMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);   //403
    }

    // 비밀번호 불일치시 발생
    @ExceptionHandler(PasswordMistmatchException.class)
    public ResponseEntity<String> handleCustomException(PasswordMistmatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);  //401
    }

    // 삭제된 사용자일시 발생
    @ExceptionHandler(DeletedUserException.class)
    public ResponseEntity<String> handleCustomException(DeletedUserException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);   // 403
    }

    // user id 미입력시 발생
    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<String> handleCustomException(UserIdNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //500
    }

    // post id 미입력시 발생
    @ExceptionHandler(PostIdNotFoundException.class)
    public ResponseEntity<String> handleCustomException(PostIdNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //500
    }

    // comment id 미입력시 발생
    @ExceptionHandler(CommentIdNotFoundException.class)
    public ResponseEntity<String> handleCustomException(CommentIdNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //500
    }

    // user 를 찾을 수 없을시 발생
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleCustomException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);  //500
    }
}

