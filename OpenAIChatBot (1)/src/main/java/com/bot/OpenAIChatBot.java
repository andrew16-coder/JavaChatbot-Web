package com.bot;

import java.io.IOException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIChatBot {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        OkHttpClient client = new OkHttpClient();

        System.out.println("ChatBot: Hello! Type your question (or 'exit' to quit):");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) break;

            String jsonBody = new Gson().toJson(new ChatRequest(userInput));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Error: " + response.code());
                } else {
                    String responseBody = response.body().string();
                    JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                    String botReply = json
                            .getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                    System.out.println("ChatBot: " + botReply.trim());
                }
            }
        }

        scanner.close();
    }

    static class ChatRequest {
        String model = "gpt-3.5-turbo";
        Message[] messages;

        ChatRequest(String userMessage) {
            messages = new Message[] {
                new Message("user", userMessage)
            };
        }

        static class Message {
            String role;
            String content;

            Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }
}
