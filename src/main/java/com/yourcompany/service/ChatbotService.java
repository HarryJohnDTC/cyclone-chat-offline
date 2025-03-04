package com.yourcompany.service;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class ChatbotService {
    private static final String API_URL = "http://localhost:11434/api/generate";
    private OkHttpClient client;

    public ChatbotService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public String getChatbotResponse(String userInput) throws IOException {
        JSONObject json = new JSONObject();
        json.put("model", "llama3.2");
        json.put("stream", false);
        json.put("temperature", 0.1);
        json.put("top_p", 0.8);
        json.put("repeat_penalty", 1.1);
        json.put("num_ctx", 2048);
        json.put("num_thread", 4);
        json.put("prompt", userInput);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            JSONObject responseObject = new JSONObject(response.body().string());
            return responseObject.getString("response");
        }
    }
}
