package example.apilimittest.global.service;


import example.apilimittest.global.presentation.dto.SignInCauseDto;
import example.apilimittest.global.presentation.dto.SignInResultDto;
import example.apilimittest.global.presentation.dto.SignUpCauseDto;
import example.apilimittest.global.presentation.dto.SignUpResultDto;
import jakarta.servlet.http.HttpServletResponse;

public interface SignService {
    SignUpResultDto signUp(SignUpCauseDto request);

    SignInResultDto signIn(SignInCauseDto request, HttpServletResponse response) throws RuntimeException;
}
