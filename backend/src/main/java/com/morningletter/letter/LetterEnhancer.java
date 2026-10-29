package com.morningletter.letter;

import java.time.LocalDate;
import java.util.Optional;

public interface LetterEnhancer {

    Optional<LetterDraft> tryEnhance(LetterSeed seed, LocalDate date);
}
