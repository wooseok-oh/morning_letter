package com.morningletter.mail;

import com.morningletter.config.LetterProperties;
import com.morningletter.letter.DailyLetter;
import com.morningletter.letter.LetterComposer;
import com.morningletter.letter.LetterFormatter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class LetterMailService {

    private static final Logger log = LoggerFactory.getLogger(LetterMailService.class);

    private final JavaMailSender mailSender;
    private final LetterComposer letterComposer;
    private final LetterFormatter letterFormatter;
    private final LetterProperties letterProperties;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public LetterMailService(
            JavaMailSender mailSender,
            LetterComposer letterComposer,
            LetterFormatter letterFormatter,
            LetterProperties letterProperties
    ) {
        this.mailSender = mailSender;
        this.letterComposer = letterComposer;
        this.letterFormatter = letterFormatter;
        this.letterProperties = letterProperties;
    }

    public DailyLetter sendToday() {
        if (!letterProperties.enabled()) {
            throw new IllegalStateException("아침 편지가 비활성화되어 있습니다.");
        }
        if (!isMailConfigured()) {
            throw new IllegalStateException(
                    "메일 설정이 완료되지 않았습니다. NAVER_MAIL_USERNAME, NAVER_MAIL_PASSWORD 환경변수를 확인하세요."
            );
        }

        String[] recipients = letterProperties.safeRecipients();
        if (recipients.length == 0) {
            throw new IllegalStateException("수신 메일 주소가 설정되지 않았습니다.");
        }

        DailyLetter letter = letterComposer.composeToday();
        String subject = letterFormatter.subject(letter, letterProperties.safeSubjectPrefix());
        String plainText = letterFormatter.plainText(letter);
        String html = letterFormatter.html(letter);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailUsername);
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(plainText, html);
            mailSender.send(message);
            log.info("아침 편지 발송 완료 (recipients={}, source={})", recipients.length, letter.source());
            return letter;
        } catch (MailAuthenticationException ex) {
            throw new IllegalStateException(
                    "메일 서버 인증에 실패했습니다. 네이버 앱 비밀번호가 만료·변경되었을 수 있습니다. "
                            + "네이버 ID > 보안설정 > 2단계 인증 > 애플리케이션 비밀번호 관리에서 재발급한 뒤 "
                            + "NAVER_MAIL_PASSWORD를 갱신하세요. "
                            + "(원본: " + ex.getMessage() + ")",
                    ex
            );
        } catch (MailException | MessagingException ex) {
            throw new IllegalStateException("메일 발송 실패: " + ex.getMessage(), ex);
        }
    }

    public boolean isMailConfigured() {
        return mailUsername != null && !mailUsername.isBlank()
                && mailPassword != null && !mailPassword.isBlank();
    }
}
