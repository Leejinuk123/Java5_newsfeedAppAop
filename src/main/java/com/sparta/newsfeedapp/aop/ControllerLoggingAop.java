package com.sparta.newsfeedapp.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j(topic = "LoggingAop")
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerLoggingAop {

    private final HttpServletRequest req;

    @Pointcut("execution(* com.sparta.newsfeedapp.controller.CommentController.*(..))")
    private void comment(){}

    @Pointcut("execution(* com.sparta.newsfeedapp.controller.PostController.*(..))")
    private void post(){}

    @Pointcut("execution(* com.sparta.newsfeedapp.controller.UserController.*(..))")
    private void user(){}

    @Pointcut("execution(* com.sparta.newsfeedapp.controller.AuthController.*(..))")
    private void auth(){}

    @Pointcut("execution(* com.sparta.newsfeedapp.controller.MailController.*(..))")
    private void mail(){}

    @Before("comment() || post() || user() || auth() || mail()")
    public void loggingBefore(){
        String method = req.getMethod();
        String url = req.getRequestURI();
        log.info("HTTP Method : {} , Request URL : {}", method, url);
    }
}
