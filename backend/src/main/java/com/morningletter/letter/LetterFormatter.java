package com.morningletter.letter;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class LetterFormatter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN);

    public String subject(DailyLetter letter, String prefix) {
        String safePrefix = prefix == null || prefix.isBlank() ? "하루의 힘" : prefix.trim();
        return "[" + safePrefix + "] " + letter.date().format(DATE);
    }

    public String plainText(DailyLetter letter) {
        StringBuilder builder = new StringBuilder();
        builder.append(letter.date().format(DATE)).append("\n\n");
        builder.append(letter.opening()).append("\n\n");
        builder.append("오늘의 글귀\n");
        builder.append(letter.englishQuote()).append("\n");
        builder.append("— ").append(letter.attribution()).append("\n\n");
        builder.append(letter.koreanQuote()).append("\n");
        builder.append(letter.translationNote()).append("\n\n");
        for (String paragraph : letter.paragraphs()) {
            builder.append(paragraph).append("\n\n");
        }
        builder.append("오늘 하나만\n");
        builder.append(letter.smallStep()).append("\n\n");
        builder.append(letter.closing()).append("\n");
        return builder.toString().stripTrailing();
    }

    public String html(DailyLetter letter) {
        StringBuilder builder = new StringBuilder();
        builder.append("""
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>하루의 힘</title>
                </head>
                <body style="margin:0;padding:0;background:#f4efe6;color:#2b2723;font-family:'Apple SD Gothic Neo','Noto Sans KR','Malgun Gothic',sans-serif;">
                  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#f4efe6;padding:28px 12px;">
                    <tr><td align="center">
                      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="max-width:560px;background:#fffdf8;border:1px solid #e6dccb;border-radius:16px;padding:32px 28px;">
                        <tr><td>
                          <p style="margin:0 0 8px 0;font-size:13px;letter-spacing:0.08em;color:#8a6a45;">하루의 힘</p>
                          <h1 style="margin:0 0 18px 0;font-size:24px;font-weight:700;line-height:1.4;">""");
        builder.append(escape(letter.date().format(DATE)));
        builder.append("""
                </h1>
                          <p style="margin:0 0 28px 0;font-size:16px;line-height:1.75;">""");
        builder.append(escape(letter.opening()));
        builder.append("""
                </p>
                          <p style="margin:0 0 8px 0;font-size:13px;color:#8a6a45;">오늘의 글귀</p>
                          <p style="margin:0;font-size:18px;line-height:1.6;font-family:Georgia,'Times New Roman',serif;color:#1f1a16;">""");
        builder.append(escape(letter.englishQuote()));
        builder.append("</p><p style=\"margin:8px 0 18px 0;font-size:14px;color:#6d645b;\">— ");
        builder.append(escape(letter.attribution()));
        builder.append("</p><p style=\"margin:0 0 8px 0;font-size:18px;line-height:1.7;font-weight:700;\">");
        builder.append(escape(letter.koreanQuote()));
        builder.append("</p><p style=\"margin:0 0 24px 0;font-size:14px;line-height:1.7;color:#6d645b;\">");
        builder.append(escape(letter.translationNote()));
        builder.append("</p>");
        for (String paragraph : letter.paragraphs()) {
            builder.append("<p style=\"margin:0 0 14px 0;font-size:16px;line-height:1.8;\">");
            builder.append(escape(paragraph));
            builder.append("</p>");
        }
        builder.append("""
                          <div style="margin-top:8px;padding:16px 18px;background:#f7f1e6;border-radius:12px;">
                            <p style="margin:0 0 6px 0;font-size:13px;color:#8a6a45;">오늘 하나만</p>
                            <p style="margin:0;font-size:16px;line-height:1.7;">""");
        builder.append(escape(letter.smallStep()));
        builder.append("""
                </p>
                          </div>
                          <p style="margin:24px 0 0 0;font-size:15px;line-height:1.7;color:#4e473f;">""");
        builder.append(escape(letter.closing()));
        builder.append("""
                </p>
                        </td></tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """);
        return builder.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
