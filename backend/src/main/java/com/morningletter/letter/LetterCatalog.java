package com.morningletter.letter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LetterCatalog {

    private final ObjectMapper objectMapper;
    private List<LetterSeed> seeds = List.of();

    public LetterCatalog(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void reload() {
        try {
            ClassPathResource resource = new ClassPathResource("letters/catalog.json");
            List<LetterSeed> loaded = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {}
            );
            if (loaded == null || loaded.isEmpty()) {
                throw new IllegalStateException("편지 카탈로그가 비어 있습니다.");
            }
            this.seeds = List.copyOf(loaded);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("letters/catalog.json 을 읽지 못했습니다.", ex);
        }
    }

    public List<LetterSeed> all() {
        return seeds;
    }

    public LetterSeed forDate(java.time.LocalDate date) {
        int index = Math.floorMod((int) date.toEpochDay(), seeds.size());
        return seeds.get(index);
    }
}
