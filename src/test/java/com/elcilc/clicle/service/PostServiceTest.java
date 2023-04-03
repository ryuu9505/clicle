package com.elcilc.clicle.service;

import com.elcilc.clicle.exception.ErrorCode;
import com.elcilc.clicle.exception.ClicleApplicationException;
import com.elcilc.clicle.fixture.TestInfoFixture;
import com.elcilc.clicle.fixture.UserEntityFixture;
import com.elcilc.clicle.model.entity.PostEntity;
import com.elcilc.clicle.model.entity.UserEntity;
import com.elcilc.clicle.repository.PostEntityRepository;
import com.elcilc.clicle.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @MockBean
    UserEntityRepository userEntityRepository;

    @MockBean
    PostEntityRepository postEntityRepository;

    @Test
    void CreatingPostShouldWorkProperly() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
        Assertions.assertDoesNotThrow(() -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
    }


    @Test
    void ShouldThrowsErrorIfUserDoesNotExistWhenCreatingPost() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void ShouldThrowsErrorIfPostDoesNotExistWhenEditingPost() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () ->
                postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("FIXING")
    @Test
    void ShouldThrowsErrorIfUserDoesNotExistWhenEditingPost() {

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void ShouldThrowsErrorIfUserDoesNotCorrectWithAuthorWhenEditingPost() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

    @Test
    void ShouldThrowsErrorIfPostDoesNotExistWhenDeletingPost() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Disabled("FIXING")
    @Test
    void ShouldThrowsErrorIfUserDoesNotExistWhenDeletingPost() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void ShouldThrowsErrorIfUserDoesNotCorrectWithAuthorWhenDeletingPost() {
        PostEntity mockPostEntity = mock(PostEntity.class);
        UserEntity mockUserEntity = mock(UserEntity.class);

        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(mockUserEntity));
        when(mockPostEntity.getUser()).thenReturn(mock(UserEntity.class));
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.delete(fixture.getUserId(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }


    @Disabled("FIXING")
    @Test
    void ShouldThrowsErrorIfUserDoesNotExistsWhenRetrievingMyPostList() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        ClicleApplicationException exception = Assertions.assertThrows(ClicleApplicationException.class, () -> postService.my(fixture.getUserId(), mock(Pageable.class)));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void RetrievingPostListShouldWorkProperly() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void RetrievingMyPostListShouldWorkProperly() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAllByUserId(any(), eq(pageable))).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.my(fixture.getUserId(), pageable));
    }

}
