package com.example.productivitycoach.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiService {

    private static final String API_KEY = "AIzaSyC5ggUIB0UDX_9CIWjXlNMBDS8Hp4mPukg";

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public String analyserObjectif(String objectif) {
        try {
            URL apiTarget = new URL(API_URL);

            HttpURLConnection conn = (HttpURLConnection) apiTarget.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = "Tu es un assistant de productivité. Génère un plan d'action pour : "
                    + objectif
                    + ". Réponds UNIQUEMENT en JSON avec ce format exact, sans texte avant ou après : "
                    + "{ \"plan\": [ { \"titre\": \"...\", \"priorite\": \"HAUTE\" } ] }";

            JSONObject part = new JSONObject();
            part.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);

            System.out.println(" Requête envoyée : " + requestBody.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println(" Code réponse : " + responseCode);
            System.out.println(" Réponse brute : " + response.toString());

            if (responseCode != 200) {
                System.err.println(" Code erreur : " + responseCode);
                System.err.println(" Réponse API brute : " + response.toString());
                return "{ \"plan\": [] }";
            }

            JSONObject resJson = new JSONObject(response.toString());
            String texteReponse = resJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            texteReponse = texteReponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            System.out.println(" Réponse IA extraite : " + texteReponse);
            return texteReponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"plan\": [] }";
        }
    }
}