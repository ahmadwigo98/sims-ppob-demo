package com.nutech.simsppob.controller;

import com.nutech.simsppob.rest.BaseResponse;
import com.nutech.simsppob.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class UserTransactionController {

    private final UserService userService;

    @GetMapping("/balance")
    public BaseResponse getBalance(@RequestHeader(name="Authorization") String authHeader) {
        String jwt = null;
        if (!(authHeader == null) && authHeader.startsWith(("Bearer "))) {
            jwt = authHeader.substring(7);
        }
        return userService.getBalance(jwt);
    }

    @PostMapping("/topup")
    public BaseResponse topupBalance(@RequestHeader(name="Authorization") String authHeader,
                                     @RequestBody Map<String, String> request) {
        String topupAmount = request.get("top_up_amount");
        String jwt = null;
        if (!(authHeader == null) && authHeader.startsWith(("Bearer "))) {
            jwt = authHeader.substring(7);
        }
        return userService.topupBalance(jwt, topupAmount);
    }
    @PostMapping("/transaction")
    public BaseResponse doTransaction(@RequestHeader(name="Authorization") String authHeader,
                                     @RequestBody Map<String, String> request) {
        String serviceCode = request.get("service_code");
        String jwt = null;
        if (!(authHeader == null) && authHeader.startsWith(("Bearer "))) {
            jwt = authHeader.substring(7);
        }
        return userService.doTransaction(jwt, serviceCode);
    }

    @GetMapping("/transaction/history")
    public BaseResponse getTransactionHistory(@RequestHeader(name="Authorization") String authHeader,
                                              @RequestParam(name = "limit", required = false) Integer limit,
                                              @RequestParam(name = "offset", required = false) Integer offset) {
        String jwt = null;
        if (!(authHeader == null) && authHeader.startsWith(("Bearer "))) {
            jwt = authHeader.substring(7);
        }
        return userService.getTransactionHistory(jwt, limit, offset);
    }
}
