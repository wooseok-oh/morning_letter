package com.morningletter.letter;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class LetterComposer {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final LetterCatalog catalog;
    private final LetterEnhancer enhancer;

    public LetterComposer(LetterCatalog catalog, LetterEnhancer enhancer) {
        this.catalog = catalog;
        this.enhancer = enhancer;
    }

    public DailyLetter composeToday() {
        return compose(LocalDate.now(KST));
    }

    public DailyLetter compose(LocalDate date) {
        LetterSeed seed = catalog.forDate(date);
        LetterDraft draft = enhancer.tryEnhance(seed, date).filter(this::usable).orElse(null);
        if (draft == null) {
            return fromSeed(date, seed, "catalog");
        }
        return new DailyLetter(
                date,
                opening(date),
                seed.english(),
                seed.attribution(),
                draft.korean().trim(),
                blankTo(draft.translationNote(), seed.translationNote()),
                draft.paragraphs().stream().map(String::trim).filter(line -> !line.isBlank()).toList(),
                draft.smallStep().trim(),
                blankTo(draft.closing(), seed.closing()),
                "ai"
        );
    }

    private DailyLetter fromSeed(LocalDate date, LetterSeed seed, String source) {
        return new DailyLetter(
                date,
                opening(date),
                seed.english(),
                seed.attribution(),
                seed.korean(),
                seed.translationNote(),
                seed.paragraphs(),
                seed.smallStep(),
                seed.closing(),
                source
        );
    }

    private boolean usable(LetterDraft draft) {
        if (draft == null || draft.korean() == null || draft.paragraphs() == null || draft.smallStep() == null) {
            return false;
        }
        if (!containsHangul(draft.korean()) || !containsHangul(draft.smallStep())) {
            return false;
        }
        List<String> paragraphs = draft.paragraphs().stream()
                .filter(line -> line != null && !line.isBlank())
                .toList();
        if (paragraphs.size() < 2) {
            return false;
        }
        return paragraphs.stream().allMatch(this::containsHangul);
    }

    private boolean containsHangul(String text) {
        return text != null && text.codePoints().anyMatch(ch ->
                (ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0x1100 && ch <= 0x11FF));
    }

    private String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    static String opening(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return "주말 아침입니다. 무언가를 해내야만 괜찮은 하루가 되는 것은 아닙니다.";
        }
        if (day == DayOfWeek.MONDAY) {
            return "월요일입니다. 한 주를 정복하라는 말 대신, 오늘 하루만 건사해도 충분하다고 적습니다.";
        }
        return "평일의 아침입니다. 거창한 각오 없이, 오늘 앞에 놓인 일만 봐도 됩니다.";
    }
}
