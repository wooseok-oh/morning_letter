package com.morningletter.letter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LetterComposerTest {

    private LetterCatalog catalog;
    private LetterComposer composer;

    @BeforeEach
    void setUp() {
        catalog = new LetterCatalog(new ObjectMapper());
        catalog.reload();
        composer = new LetterComposer(catalog, (seed, date) -> Optional.empty());
    }

    @Test
    void catalogHasTwelveLetters() {
        assertThat(catalog.all()).hasSize(12);
    }

    @Test
    void composeUsesKoreanAndEnglishFromCatalog() {
        DailyLetter letter = composer.compose(LocalDate.of(2026, 10, 29));

        assertThat(letter.englishQuote()).isNotBlank();
        assertThat(letter.koreanQuote()).containsPattern("[가-힣]");
        assertThat(letter.translationNote()).containsPattern("[가-힣]");
        assertThat(letter.paragraphs()).isNotEmpty();
        assertThat(letter.smallStep()).containsPattern("[가-힣]");
        assertThat(letter.opening()).contains("아침");
        assertThat(letter.source()).isEqualTo("catalog");
    }

    @Test
    void differentDatesCanSelectDifferentSeeds() {
        DailyLetter first = composer.compose(LocalDate.of(2026, 1, 1));
        DailyLetter later = composer.compose(LocalDate.of(2026, 1, 2));

        assertThat(first.englishQuote()).isNotEqualTo(later.englishQuote());
    }
}
