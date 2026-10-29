package com.morningletter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;

@ConfigurationProperties(prefix = "letter")
public record LetterProperties(
        boolean enabled,
        boolean oneshot,
        boolean scheduleEnabled,
        String recipient,
        String schedule,
        String timezone,
        String subjectPrefix,
        Ai ai
) {

    private static final String DEFAULT_RECIPIENTS =
            "ohws6373@naver.com,thdud2720@naver.com,jka5513@naver.com,eogod147@naver.com";

    public LetterProperties {
        if (ai == null) {
            ai = new Ai(true, "", "https://api.groq.com/openai", "llama-3.3-70b-versatile", 60);
        }
    }

    public String[] safeRecipients() {
        String raw = recipient == null || recipient.isBlank() ? DEFAULT_RECIPIENTS : recipient.trim();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toArray(String[]::new);
    }

    public String safeSchedule() {
        return schedule != null && !schedule.isBlank() ? schedule : "0 0 7 * * *";
    }

    public String safeTimezone() {
        return timezone != null && !timezone.isBlank() ? timezone : "Asia/Seoul";
    }

    public String safeSubjectPrefix() {
        return subjectPrefix != null && !subjectPrefix.isBlank() ? subjectPrefix : "하루의 힘";
    }

    public record Ai(
            boolean enabled,
            String apiKey,
            String baseUrl,
            String model,
            int timeoutSeconds
    ) {
        public boolean configured() {
            return enabled && apiKey != null && !apiKey.isBlank();
        }

        public String safeBaseUrl() {
            return baseUrl != null && !baseUrl.isBlank() ? baseUrl : "https://api.groq.com/openai";
        }

        public String safeModel() {
            return model != null && !model.isBlank() ? model : "llama-3.3-70b-versatile";
        }

        public int safeTimeoutSeconds() {
            return timeoutSeconds > 0 ? timeoutSeconds : 60;
        }
    }
}
