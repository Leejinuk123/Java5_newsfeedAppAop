package com.sparta.newsfeedapp.exception;

public class DeletedUserException extends IllegalArgumentException{
    public DeletedUserException() {
        super("삭제된 유저입니다.");
    }
}
