package com.substring.resume.analyzer.services.implementation;

import com.substring.resume.analyzer.payload.AiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {
    private final ChatClient chatClient;
    public AiResponse askAi(String query){
        String responseText=chatClient.
                prompt(query)
                .call()
                .content();
        return new AiResponse(responseText);
    }
}
