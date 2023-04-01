package com.elcilc.clicle.service;

import com.elcilc.clicle.exception.ClicleApplicationException;
import com.elcilc.clicle.exception.ErrorCode;
import com.elcilc.clicle.model.User;
import com.elcilc.clicle.model.entity.UserEntity;
import com.elcilc.clicle.repository.UserEntityRepository;
import com.elcilc.clicle.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;


    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                        () -> new ClicleApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName is %s", userName)));
    }

    public String login(String userName, String password) {
        UserEntity userEntity = userRepository.findByUserName(userName).orElseThrow(() -> new ClicleApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new ClicleApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        return JwtTokenUtils.generateAccessToken(userName, secretKey, expiredTimeMs);
    }

    @Transactional
    public User join(String userName, String password) {
        // check the userId not exist
        userRepository.findByUserName(userName).ifPresent(it -> {
            throw new ClicleApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("(userName: %s)", userName));
        });

        UserEntity savedUser = userRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(savedUser);
    }

}
