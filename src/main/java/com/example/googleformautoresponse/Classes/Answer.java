package com.example.googleformautoresponse.Classes;

import java.util.Random;

public class Answer {
    String entity;
    float[] frequency;
    String[] answer;

    Random random;

    public float[] getFrequency() {
        return frequency;
    }

    public String[] getAnswer() {
        return answer;
    }

    public String getEntity() {
        return entity;
    }

    public void setAnswer(String[] answer) {
        this.answer = answer;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setFrequency(float[] frequency) {
        this.frequency = frequency;
    }

    public Answer(String entity, float[] frequency, String[] answers){
        setAnswer(answers);
        setEntity(entity);
        setFrequency(frequency);
        this.random = new Random();
    }

    public String getRandomAnswer() {
        // Создаем кумулятивный массив частот
        float[] cumulativeFrequency = new float[frequency.length];
        cumulativeFrequency[0] = frequency[0];
        for (int i = 1; i < frequency.length; i++) {
            cumulativeFrequency[i] = cumulativeFrequency[i - 1] + frequency[i];
        }

        // Генерируем случайное число от 0 до 1
        float rand = random.nextFloat();

        // Определяем ответ на основе случайного числа и кумулятивного массива
        for (int i = 0; i < cumulativeFrequency.length; i++) {
            if (rand <= cumulativeFrequency[i]) {
                return answer[i];
            }
        }

        // На случай ошибок возвращаем последний ответ
        return answer[answer.length - 1];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ").append(entity).append("\n");
        sb.append("Frequency: ");
        if (frequency != null) {
            for (float f : frequency) {
                sb.append(f).append(" ");
            }
        }
        sb.append("\n");
        sb.append("Answers: ");
        if (answer != null) {
            for (String ans : answer) {
                sb.append(ans).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
