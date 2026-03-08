package com.smart.smartAI.ai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.smartAI.data.TickData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * HTTP клиент для связи с AI API
 */
public class AIClient {
    private final String apiUrl;
    private final Logger logger;
    private final Gson gson;
    private boolean enabled;

    public AIClient(String apiUrl, Logger logger) {
        this.apiUrl = apiUrl;
        this.logger = logger;
        this.gson = new Gson();
        this.enabled = true;
    }

    public CompletableFuture<AIResponse> predict(List<TickData> ticks) {
        return CompletableFuture.supplyAsync(() -> {
            if (!enabled) {
                return new AIResponse(0.0, "AI disabled");
            }

            try {
                // Подготовка данных
                double[][] ticksArray = new double[ticks.size()][8];
                for (int i = 0; i < ticks.size(); i++) {
                    TickData tick = ticks.get(i);
                    ticksArray[i] = new double[]{
                        tick.deltaYaw, tick.deltaPitch,
                        tick.accelYaw, tick.accelPitch,
                        tick.jerkYaw, tick.jerkPitch,
                        tick.gcdErrorYaw, tick.gcdErrorPitch
                    };
                }

                JsonObject request = new JsonObject();
                request.add("ticks", gson.toJsonTree(ticksArray));

                // HTTP запрос
                URL url = new URL(apiUrl + "/predict");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Отправка
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Получение ответа
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }

                        JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
                        double probability = json.get("probability").getAsDouble();
                        return new AIResponse(probability, null);
                    }
                } else {
                    return new AIResponse(0.0, "HTTP " + responseCode);
                }
            } catch (Exception e) {
                logger.warning("[AI] Ошибка запроса: " + e.getMessage());
                return new AIResponse(0.0, e.getMessage());
            }
        });
    }

    public boolean checkHealth() {
        try {
            URL url = new URL(apiUrl + "/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
