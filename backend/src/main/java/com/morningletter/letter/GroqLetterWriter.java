package com.morningletter.letter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningletter.config.LetterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class GroqLetterWriter implements LetterEnhancer {

    private static final Logger log = LoggerFactory.getLogger(GroqLetterWriter.class);

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE", Locale.KOREAN);
    private static final String SYSTEM_PROMPT = """
            당신은 아침 7시에 도착하는 짧은 한국어 편지를 씁니다.
            독자는 특별한 성공을 꿈꾸기보다, 오늘 하루를 살아가는 어른입니다.

            규칙:
            - 본문, 번역, 실천, 맺음말은 모두 한국어 해요체로 씁니다.
            - 영어 글귀의 뜻을 지키되, 직역이 어색하면 한국어 문맥에 맞게 다듬습니다.
            - 왜 그렇게 옮겼는지 translationNote에 한두 문장으로 설명합니다.
            - 훈계하지 않습니다. 주식, 투자, 재테크, 종교 권유를 넣지 않습니다.
            - 과장된 희망이나 공허한 응원("너는 할 수 있어" 반복)을 피합니다.
            - paragraphs는 2~3개, 각 문단은 2문장 안쪽입니다.
            - 영어 원문과 출처는 바꾸지 않습니다. JSON만 반환합니다.
            {
              "korean": "자연스러운 한국어 번역",
              "translationNote": "번역을 이렇게 한 이유",
              "paragraphs": ["문단", "문단"],
              "smallStep": "오늘 할 수 있는 아주 작은 행동 하나",
              "closing": "짧은 맺음말"
            }
            """;

    private final LetterProperties properties;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public GroqLetterWriter(LetterProperties properties, ObjectMapper objectMapper, WebClient.Builder webClientBuilder) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Optional<LetterDraft> tryEnhance(LetterSeed seed, LocalDate date) {
        LetterProperties.Ai ai = properties.ai();
        if (ai == null || !ai.configured()) {
            return Optional.empty();
        }

        try {
            String content = request(ai, seed, date);
            return Optional.of(parse(content));
        } catch (Exception ex) {
            log.warn("아침 편지 AI 작성 실패, 카탈로그 글로 보냅니다: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private String request(LetterProperties.Ai ai, LetterSeed seed, LocalDate date) {
        String userPrompt = """
                날짜: %s
                영어 원문: %s
                출처: %s
                참고 번역: %s
                참고 번역 설명: %s

                참고 번역의 뜻을 유지하되, 오늘 날짜의 분위기에 맞게 본문을 새로 쓰세요.
                """.formatted(
                date.format(DATE),
                seed.english(),
                seed.attribution(),
                seed.korean(),
                seed.translationNote()
        );

        Map<String, Object> body = Map.of(
                "model", ai.safeModel(),
                "temperature", 0.7,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        String raw = webClient.post()
                .uri(ai.safeBaseUrl().replaceAll("/$", "") + "/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(ai.apiKey().trim()))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(ai.safeTimeoutSeconds()));

        JsonNode root = readTree(raw);
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (content.isMissingNode() || content.asText().isBlank()) {
            throw new IllegalStateException("AI 응답에 본문이 없습니다.");
        }
        return content.asText();
    }

    private LetterDraft parse(String content) {
        String json = stripFence(content);
        JsonNode node = readTree(json);
        List<String> paragraphs = new ArrayList<>();
        JsonNode paragraphNode = node.path("paragraphs");
        if (paragraphNode.isArray()) {
            paragraphNode.forEach(item -> paragraphs.add(item.asText("")));
        }
        return new LetterDraft(
                node.path("korean").asText(""),
                node.path("translationNote").asText(""),
                paragraphs,
                node.path("smallStep").asText(""),
                node.path("closing").asText("")
        );
    }

    private JsonNode readTree(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (Exception ex) {
            throw new IllegalStateException("JSON 파싱 실패", ex);
        }
    }

    private String stripFence(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstBreak = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstBreak > 0 && lastFence > firstBreak) {
                return trimmed.substring(firstBreak + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
