package com.substring.resume.analyzer.services.implementation;

import com.substring.resume.analyzer.services.TextCleaner;
import org.springframework.stereotype.Service;

@Service
public class TextCleanerImpl implements TextCleaner {
    @Override
    public String clean(String rawString) {
        return rawString
                .replaceAll("\\r","-->")
                .replaceAll("\\s+", " ")

                ;
    }
}
