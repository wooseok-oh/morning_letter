package com.morningletter.letter;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LetterFormatterTest {

    private final LetterFormatter formatter = new LetterFormatter();

    @Test
    void plainTextKeepsEnglishAndKoreanTogether() {
        DailyLetter letter = sample();

        String text = formatter.plainText(letter);
        String html = formatter.html(letter);

        assertThat(formatter.subject(letter, "하루의 힘")).contains("10월 29일");
        assertThat(text).contains("This too shall pass.");
        assertThat(text).contains("이 또한 지나갑니다.");
        assertThat(text).contains("오늘 하나만");
        assertThat(html).contains("This too shall pass.");
        assertThat(html).contains("이 또한 지나갑니다.");
        assertThat(html).doesNotContain("<script");
    }

    private DailyLetter sample() {
        return new DailyLetter(
                LocalDate.of(2026, 10, 29),
                "평일의 아침입니다. 거창한 각오 없이, 오늘 앞에 놓인 일만 봐도 됩니다.",
                "This too shall pass.",
                "페르시아 속담",
                "이 또한 지나갑니다.",
                "좋은 시간도 무거운 시간도 머물지 않는다는 말입니다.",
                List.of("오늘 하루만 건사해도 됩니다.", "그 정도로 충분합니다."),
                "숨을 네 번만 천천히 내쉬어 보세요.",
                "내일 아침에도 편지가 갑니다.",
                "catalog"
        );
    }
}
