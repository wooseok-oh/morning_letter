package com.morningletter.config;

import com.morningletter.mail.LetterMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
@ConditionalOnProperty(name = "letter.oneshot", havingValue = "true")
public class LetterOneShotRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LetterOneShotRunner.class);

    private final LetterMailService letterMailService;
    private final ConfigurableApplicationContext applicationContext;

    public LetterOneShotRunner(LetterMailService letterMailService, ConfigurableApplicationContext applicationContext) {
        this.letterMailService = letterMailService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("하루의 힘 편지 발송 시작");
        try {
            letterMailService.sendToday();
            log.info("하루의 힘 편지 발송 완료");
            exit(0);
        } catch (Exception ex) {
            log.error("하루의 힘 편지 발송 실패: {}", ex.getMessage(), ex);
            exit(1);
        }
    }

    private void exit(int code) {
        int exitCode = SpringApplication.exit(applicationContext, () -> code);
        System.exit(exitCode);
    }
}
