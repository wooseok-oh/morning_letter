package com.morningletter.letter;

import java.util.List;

public record LetterDraft(
        String korean,
        String translationNote,
        List<String> paragraphs,
        String smallStep,
        String closing
) {
}
