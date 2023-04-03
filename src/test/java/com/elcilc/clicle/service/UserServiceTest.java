package com.elcilc.clicle.service;

import com.elcilc.clicle.exception.ErrorCode;
import com.elcilc.clicle.exception.ClicleApplicationException;
import com.elcilc.clicle.fixture.TestInfoFixture;
import com.elcilc.clicle.fixture.UserEntityFixture;
import com.elcilc.clicle.model.User;
import com.elcilc.clicle.model.UserRole;
import com.elcilc.clicle.model.entity.UserEntity;
import com.elcilc.clicle.repository.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserEntityRepository userEntityRepository;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void LoginShouldWorkProperly() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(fixture.getUserName());
        userEntity.setPassword(fixture.getPassword());
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.matches(fixture.getPassword(), fixture.getPassword())).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> userService.login(fixture.getUserName(), fixture.getPassword()));
    }

    @Test
    void ShouldThrowsErrorIfUserDoesNotExistAtLogin() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class
                , () -> userService.login(fixture.getUserName(), fixture.getPassword()));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void ShouldThrowsErrorIfPasswordNotExistAtLogin() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), "password1")));
        when(bCryptPasswordEncoder.matches(fixture.getPassword(), "password1")).thenReturn(false);

        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class
                , () -> userService.login(fixture.getUserName(), fixture.getPassword()));

        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    void SignUpShouldWorkProperly() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(fixture.getPassword()))
                .thenReturn("password_encrypt");
        when(userEntityRepository.save(any()))
                .thenReturn(UserEntityFixture.get(fixture.getUserName(), "password_encrypt"));

        Assertions.assertDoesNotThrow(() -> userService.join(fixture.getUserName(), fixture.getPassword()));
    }


    @Test
    void ShouldThrowsErrorIfDuplicateIDExistsOrNotAtSignUp() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));

        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class,
                () -> userService.join(fixture.getUserName(), fixture.getPassword()));

        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, exception.getErrorCode());
    }

}
