package com.elcilc.clicle.service;

import com.elcilc.clicle.exception.ErrorCode;
import com.elcilc.clicle.exception.ClicleApplicationException;
import com.elcilc.clicle.model.AlarmArgs;
import com.elcilc.clicle.model.AlarmEvent;
import com.elcilc.clicle.model.AlarmType;
import com.elcilc.clicle.model.entity.AlarmEntity;
import com.elcilc.clicle.model.entity.UserEntity;
import com.elcilc.clicle.repository.AlarmEntityRepository;
import com.elcilc.clicle.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final static String ALARM_NAME = "alarm";

    private final AlarmEntityRepository alarmEntityRepository;
    private final EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public void send(AlarmType type, AlarmArgs args, UserEntity receiver) {
        AlarmEntity entity = AlarmEntity.of(type, args, receiver);
        alarmEntityRepository.save(entity);
        emitterRepository.get(receiver.getId()).ifPresentOrElse(it -> {
                    try {
                        it.send(SseEmitter.event()
                                .id(entity.getId().toString())
                                .name(ALARM_NAME)
                                .data(new AlarmEvent()));
                    } catch (IOException exception) {
                        emitterRepository.delete(receiver.getId());
                        throw new ClicleApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
                    }
                },
                () -> log.info("No emitter founded")
        );
    }


    public SseEmitter connectNotification(Integer userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);
        emitter.onCompletion(() -> emitterRepository.delete(userId));
        emitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            log.info("send");
            emitter.send(SseEmitter.event()
                    .id("id")
                    .name(ALARM_NAME)
                    .data("connect completed"));
        } catch (IOException exception) {
            throw new ClicleApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
        }
        return emitter;
    }

}
