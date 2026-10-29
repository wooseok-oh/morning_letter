package com.morningletter.letter;

import java.time.LocalDate;
import java.util.List;

public record DailyLetter(
        LocalDate date,
        String opening,
        String englishQuote,
        String attribution,
        String koreanQuote,
        String translationNote,
        List<String> paragraphs,
        String smallStep,
        String closing,
        String source
) {
}
