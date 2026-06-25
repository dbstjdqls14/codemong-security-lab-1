package com.codemong.securitylab.auth;

import com.codemong.securitylab.auth.dto.LoginRequest;
import com.codemong.securitylab.auth.dto.LoginResponse;
import com.codemong.securitylab.auth.dto.RefreshRequest;
import com.codemong.securitylab.auth.dto.SignupRequest;
import com.codemong.securitylab.auth.dto.TokenResponse;
import com.codemong.securitylab.security.JwtTokenProvider;
import com.codemong.securitylab.user.User;
import com.codemong.securitylab.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(request.password());
        userRepository.save(new User(request.email(), encodedPassword, request.nickname()));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return LoginResponse.accessOnly(jwtTokenProvider.createAccessToken(user.getEmail()));
    }

    public TokenResponse refresh(RefreshRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Step 04에서 구현하세요.");
    }
}
