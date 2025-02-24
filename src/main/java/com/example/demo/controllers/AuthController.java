package com.example.demo.controllers;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.constants.CommonType;
import com.example.demo.dtos.CommonResponse;
import com.example.demo.modules.auth.AuthService;
import com.example.demo.modules.auth.dtos.AuthDTO;
import com.example.demo.modules.token.TokenService;
import com.example.demo.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    @PostMapping("/token/revoke")
    public ResponseEntity<CommonResponse<Object>> revokeRefreshTokens() {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            tokenService.deactivatePastTokens(userId, CommonType.REFRESH_TOKEN);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<CommonResponse<Object>> enableOtp() {
        try {
            var result = authService.enableOtp();
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<CommonResponse<Object>> confirmOtp(@RequestBody AuthDTO requestDTO) {
        try {
            authService.confirmOtp(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<CommonResponse<Object>> disableOtp(@RequestBody AuthDTO requestDTO) {
        try {
            authService.disableOtp(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
