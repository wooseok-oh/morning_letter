package com.morningletter.letter;

import java.util.List;

public record LetterSeed(
        String id,
        String english,
        String attribution,
        String korean,
        String translationNote,
        List<String> paragraphs,
        String smallStep,
        String closing
) {
}
