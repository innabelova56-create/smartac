package com.smart.smartAI.ai;

/**
 * Ответ от AI API
 */
public class AIResponse {
    private final double probability;
    private final String error;

    public AIResponse(double probability, String error) {
        this.probability = probability;
        this.error = error;
    }

    public double getProbability() {
        return probability;
    }

    public String getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isCheating(double threshold) {
        return probability >= threshold;
    }
}
