package com.nutech.simsppob.controller;

import com.nutech.simsppob.model.LoginRequest;
import com.nutech.simsppob.model.RegistrationRequest;
import com.nutech.simsppob.rest.BaseResponse;
import com.nutech.simsppob.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    @GetMapping
    public String home() {
        return "Welcome to Sims PPOB with Spring Boot";
    }

    @PostMapping (value = "/registration")
    public BaseResponse registration (@RequestBody RegistrationRequest registrationRequest) {
        return userService.register(registrationRequest);
    }

    @PostMapping (value = "/login")
    public BaseResponse login (@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

}
