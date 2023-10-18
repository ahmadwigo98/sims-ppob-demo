package com.nutech.simsppob.service;

import com.nutech.simsppob.model.LoginRequest;
import com.nutech.simsppob.model.RegistrationRequest;
import com.nutech.simsppob.rest.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    BaseResponse register (RegistrationRequest registrationRequest);
    BaseResponse login (LoginRequest loginRequest);
    BaseResponse getBalance(String jwt);
    BaseResponse topupBalance(String jwt, Integer topupAmount);
    BaseResponse doTransaction(String jwt, String serviceCode);
    BaseResponse getTransactionHistory(String jwt, Integer limit, Integer offset);
}
