package com.morningletter.schedule;

import com.morningletter.config.LetterProperties;
import com.morningletter.mail.LetterMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "letter.schedule-enabled", havingValue = "true")
public class LetterScheduler {

    private static final Logger log = LoggerFactory.getLogger(LetterScheduler.class);

    private final LetterMailService letterMailService;
    private final LetterProperties letterProperties;

    public LetterScheduler(LetterMailService letterMailService, LetterProperties letterProperties) {
        this.letterMailService = letterMailService;
        this.letterProperties = letterProperties;
    }

    @Scheduled(cron = "${letter.schedule:0 0 7 * * *}", zone = "${letter.timezone:Asia/Seoul}")
    public void send() {
        if (!letterProperties.enabled()) {
            log.info("아침 편지가 비활성화되어 스케줄을 건너뜁니다.");
            return;
        }
        letterMailService.sendToday();
    }
}
