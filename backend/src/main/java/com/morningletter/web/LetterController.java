package com.morningletter.web;

import com.morningletter.letter.DailyLetter;
import com.morningletter.letter.LetterComposer;
import com.morningletter.letter.LetterFormatter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LetterController {

    private final LetterComposer letterComposer;
    private final LetterFormatter letterFormatter;

    public LetterController(LetterComposer letterComposer, LetterFormatter letterFormatter) {
        this.letterComposer = letterComposer;
        this.letterFormatter = letterFormatter;
    }

    @GetMapping("/api/letters/today")
    public DailyLetter today() {
        return letterComposer.composeToday();
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String preview() {
        DailyLetter letter = letterComposer.composeToday();
        return letterFormatter.html(letter);
    }
}
