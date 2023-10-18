package com.nutech.simsppob.service;

import com.nutech.simsppob.configuration.JwtService;
import com.nutech.simsppob.model.*;
import com.nutech.simsppob.repository.TransactionRepository;
import com.nutech.simsppob.repository.UserAccountRepository;
import com.nutech.simsppob.util.ResponseUtilEnum;
import com.nutech.simsppob.rest.BaseResponse;
import com.nutech.simsppob.repository.UserRepository;
import com.nutech.simsppob.util.ServiceTypeEnum;
import com.nutech.simsppob.util.TransactionTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private boolean isValidEmail (String email) {
        final String VALID_EMAIL_PATTER = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.matches(VALID_EMAIL_PATTER, email);
    }

    private User getUserByJwt (String jwt) {
        return userRepository.findByEmail(getEmailByJwt(jwt));
    }
    private UserAccount getUserAccountByJwt (String jwt) {
        return userAccountRepository.findByEmail(getEmailByJwt(jwt));
    }

    private String getEmailByJwt (String jwt) {
        return jwtService.extractEmail(jwt);
    }

    private String generateTransactionInvoiceNumberByJwt (String jwt) {
        Integer transactionSize = transactionRepository.countByUserAccount(getUserAccountByJwt(jwt));
        String invoiceDate =  new SimpleDateFormat("ddMMyyyy").format(new Date());
        return String.format("INV%s-%03d", invoiceDate, transactionSize + 1);
    }

    @Override
    public BaseResponse register(RegistrationRequest registrationRequest) {

        BaseResponse baseResponse = null;
        User user = userRepository.findByEmail(registrationRequest.getEmail());
        if (user == null) {
            if (!isValidEmail(registrationRequest.getEmail())) {
                baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Parameter email tidak sesuai format");
            } else if (registrationRequest.getPassword().length() < 8) {
                baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Password length minimal 8 karakter");
            } else {
                User newUser = User.builder()
                        .email(registrationRequest.getEmail())
                        .firstName(registrationRequest.getFirst_name())
                        .lastName(registrationRequest.getLast_name())
                        .password(passwordEncoder.encode(registrationRequest.getPassword()))
                        .build();

                userRepository.save(newUser);

                // Create User Account as well
                UserAccount userAccount = UserAccount.builder()
                        .email(registrationRequest.getEmail())
                        .balance(Integer.valueOf(0))
                        .build();

                userAccountRepository.save(userAccount);

                baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Registrasi berhasil silahkan login");
            }
        } else baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Email sudah terdaftar");

        return baseResponse;
    }

    @Override
    public BaseResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        BaseResponse baseResponse = null;
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user != null) {
            if (!isValidEmail(loginRequest.getEmail())) {
                baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Parameter email tidak sesuai format");
            } else if (loginRequest.getPassword().length() < 8) {
                baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Panjang password kurang dari 8 karakter");
            } else if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
                baseResponse = new BaseResponse(ResponseUtilEnum.WRONG_CREDENTIAL.statusCode, "Username atau password salah");
            } else {
                var jwtToken = jwtService.generateToken(user);
                baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Login Sukses", Map.of("token", jwtToken));
            }
        } else {
            baseResponse = new BaseResponse(ResponseUtilEnum.USER_NOT_EXIST.statusCode, String.format("User dengan email: %s belum terdaftar", loginRequest.getEmail()));
        }
        return baseResponse;
    }

    @Override
    public BaseResponse getBalance(String jwt) {
        BaseResponse baseResponse = null;

        if (jwtService.isTokenValid(jwt, getUserByJwt(jwt))) {
            baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Get Balance Berhasil", Map.of("balance", getUserAccountByJwt(jwt).getBalance()));
        } else {
            baseResponse = new BaseResponse(ResponseUtilEnum.INVALID_TOKEN.statusCode, "Token tidak tidak valid atau kadaluwarsa");
        }

        return baseResponse;
    }

    @Override
    public BaseResponse topupBalance(String jwt, Integer topupAmount) {
        BaseResponse baseResponse = null;

        if (topupAmount > 0) {
            if (jwtService.isTokenValid(jwt, getUserByJwt(jwt))) {
                Integer newBalance = getUserAccountByJwt(jwt).getBalance() + topupAmount;
                getUserAccountByJwt(jwt).setBalance(newBalance);
                userAccountRepository.save(getUserAccountByJwt(jwt));
                baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Top Up Balance berhasil", Map.of("balance", newBalance));
            } else {
                baseResponse = new BaseResponse(ResponseUtilEnum.INVALID_TOKEN.statusCode, "Token tidak tidak valid atau kadaluwarsa");
            }
        } else {
            baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0");
        }

        return baseResponse;
    }

    @Override
    public BaseResponse doTransaction(String jwt, String serviceCode) {
        BaseResponse baseResponse = null;

        if (ObjectUtils.containsConstant(ServiceTypeEnum.values(), serviceCode)) {
            if (!(getUserAccountByJwt(jwt).getBalance() < ServiceTypeEnum.valueOf(serviceCode).serviceTariff)) {
                Transaction newTransaction = Transaction.builder()
                        .invoiceNumber(generateTransactionInvoiceNumberByJwt(jwt))
                        .serviceCode(ServiceTypeEnum.valueOf(serviceCode).name())
                        .serviceName(ServiceTypeEnum.valueOf(serviceCode).serviceName)
                        .transactionType(TransactionTypeEnum.PAYMENT)
                        .totalAmount(ServiceTypeEnum.valueOf(serviceCode).serviceTariff)
                        .createdOn(new Date())
                        .userAccount(getUserAccountByJwt(jwt))
                        .build();

                transactionRepository.save(newTransaction);
                userAccountRepository.updateBalance(ServiceTypeEnum.valueOf(serviceCode).serviceTariff, getEmailByJwt(jwt));

                baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Transaksi berhasil", newTransaction.toObjectData());
            } else {
                baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Saldo tidak mencukupi");
            }
        } else {
            baseResponse = new BaseResponse(ResponseUtilEnum.BAD_REQUEST.statusCode, "Service ataus Layanan tidak ditemukan");
        }

        return baseResponse;
    }

    @Override
    public BaseResponse getTransactionHistory(String jwt, Integer limit, Integer offset) {
        BaseResponse baseResponse = null;
        limit = limit == null ? Integer.MAX_VALUE : limit;
        offset = offset == null ? 0 : offset;
        List<Transaction> transactions = transactionRepository.findByUserAccount(getUserAccountByJwt(jwt), limit, offset);

        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("offset", offset);
        responseData.put("limit", transactions.size());
        responseData.put("records", transactions);

        baseResponse = new BaseResponse(ResponseUtilEnum.SUCCESS.statusCode, "Get History Berhasil", responseData);
        return baseResponse;
    }
}
